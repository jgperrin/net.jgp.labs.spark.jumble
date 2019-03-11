package net.jgp.labs.spark.jumble;

import static org.apache.spark.sql.functions.callUDF;
import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.concat;
import static org.apache.spark.sql.functions.concat_ws;
import static org.apache.spark.sql.functions.expr;
import static org.apache.spark.sql.functions.lit;
import static org.apache.spark.sql.functions.split;
import static org.apache.spark.sql.functions.trim;
import static org.apache.spark.sql.functions.when;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import net.jgp.labs.spark.jumble.models.Puzzle;
import net.jgp.labs.spark.jumble.models.Word;
import net.jgp.labs.spark.jumble.udf.CharacterExtractorUdf;
import net.jgp.labs.spark.jumble.udf.SortStringUdf;
import net.jgp.labs.spark.jumble.udf.SubtractStringUdf;
import net.jgp.labs.spark.jumble.utils.JumbleException;
import net.jgp.labs.spark.jumble.utils.JumbleUtils;
import net.jgp.labs.spark.jumble.utils.K;
import net.jgp.labs.spark.jumble.utils.SparkUtils;

/**
 * Solves a Jumble game.
 * 
 * The Jumble trivia is defined in an external YAML file.
 * 
 * @author jgp
 */
public class JumbleSolverApp {
  private static Logger log =
      LoggerFactory.getLogger(JumbleSolverApp.class);

  private SparkSession spark = null;
  private Dataset<Row> dictionaryDf = null;
  private String cwd = null;

  /**
   * main() is your entry point to the application.
   * 
   * @param args
   *          not used
   */
  public static void main(String[] args) {
    JumbleSolverApp app = new JumbleSolverApp();
    String[] games =
        { "puzzle1"};//, "puzzle2", "puzzle3", "puzzle4", "puzzle5" };
    String game = null;
    try {
      for (int i = 0; i < games.length; i++) {
        game = games[i];
        app.play(game);
      }
    } catch (IOException e) {
      log.error(
          "An IO error happened while trying to load game {}: {}",
          game, e.getMessage(), e);
    } catch (JumbleException e) {
      log.error(
          "An error occured while playing game {}: {}",
          game, e.getMessage(), e);
    }
  }

  /**
   * Initializes the system, creates a session, loads the dictionary.
   * Initialization can be reused for different game.
   */
  public JumbleSolverApp() {
    // Local initialization
    this.cwd = System.getProperty("user.dir");

    // Creates a session on a local master
    spark = SparkSession.builder()
        .appName("Jumble solver")
        .config("spark.driver.maxResultSize", "3g")
        .master("local[*]")
        .getOrCreate();

    spark.udf().register(
        "extractChars",
        new CharacterExtractorUdf(),
        DataTypes.StringType);
    spark.udf().register(
        "subtractString",
        new SubtractStringUdf(),
        DataTypes.StringType);
    spark.udf().register(
        "sortString",
        new SortStringUdf(),
        DataTypes.StringType);

    StructType schema = DataTypes.createStructType(new StructField[] {
        DataTypes.createStructField(
            K.WORD,
            DataTypes.StringType,
            false),
        DataTypes.createStructField(
            K.FREQUENCY,
            DataTypes.StringType,
            false) });

    dictionaryDf = spark.read().format("csv")
        .option("sep", ":")
        .option("ignoreLeadingWhiteSpace", true)
        .schema(schema)
        .load("data/freq_dict.json");
    // .load("data/freq_dict_puzzle6.json");

    dictionaryDf = dictionaryDf.filter(col(K.FREQUENCY).isNotNull());
    dictionaryDf =
        dictionaryDf.withColumn(K.FREQ_TRIM, trim(col(K.FREQUENCY)));
    dictionaryDf = dictionaryDf
        .withColumn(
            K.RAW_FREQ,
            split(col(K.FREQ_TRIM), ",")
                .getItem(0)
                .cast(DataTypes.IntegerType))
        .drop(K.FREQUENCY)
        .drop(K.FREQ_TRIM);

    // If frequency is 0, it is actually very rare, so to ease sorting,
    // let's assume it's more than the max (set to 9887), so 10000.
    dictionaryDf = dictionaryDf
        .withColumn(
            K.FREQ,
            when(col(K.RAW_FREQ).equalTo(0), 10000)
                .otherwise(col(K.RAW_FREQ)))
        .drop(K.RAW_FREQ);
    dictionaryDf = dictionaryDf.cache();
    dictionaryDf.createOrReplaceTempView(K.ALL_WORDS);
  }

  /**
   * Start a game!
   * 
   * @param title
   * @throws IOException
   * @throws JumbleException
   */
  private void play(String title) throws IOException, JumbleException {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    Puzzle puzzle = mapper.readValue(
        new File(cwd + "/data/" + title + ".yaml"),
        Puzzle.class);

    log.info("Now playing: {}", puzzle.getTitle());
    long t0 = System.currentTimeMillis();

    // Phase 1: Pre-solving 1st part of the brain teaser
    // ----
    Dataset<Row> puzzlePhase1Df = null;
    int ph1WordCount = 0;
    for (Word w : puzzle.getWords()) {
      ph1WordCount++;
      Dataset<Row> anagramsDf =
          buildPhase1Anagrams(w.getWord(), w.getCharToReport(),
              ph1WordCount);
      if (puzzlePhase1Df == null) {
        puzzlePhase1Df = anagramsDf
            .withColumn(
                K.REV_SCORE,
                col(K.FREQ + "_1").cast(DataTypes.LongType))
            .withColumn(K.FINAL_CLUE, col(K.CHARS + "_1"));
      } else {
        puzzlePhase1Df = puzzlePhase1Df
            .crossJoin(anagramsDf)
            .withColumn(
                K.REV_SCORE,
                expr(K.REV_SCORE + "*" + K.FREQ + "_" + ph1WordCount))
            .withColumn(
                K.FINAL_CLUE,
                concat(
                    col(K.FINAL_CLUE),
                    col(K.CHARS + "_" + ph1WordCount)));
      }
    }
    puzzlePhase1Df = puzzlePhase1Df.withColumn(K.FINAL_CLUE,
        callUDF("sortString", col(K.FINAL_CLUE)));
    puzzlePhase1Df = puzzlePhase1Df.cache();
    if (log.isTraceEnabled()) {
      puzzlePhase1Df.show();
    }

    log.info("Tentative solutions for phase 1:");
    prettyPrint(puzzlePhase1Df, ph1WordCount);

    long t1 = System.currentTimeMillis();
    log.debug("Phase 1: took {} ms.", (t1 - t0));

    // Phase 2: Solving second part of the teaser
    // ----
    // Gets the list of words from phase 1
    List<Row> rootsTmp = puzzlePhase1Df
        .select(K.FINAL_CLUE)
        .as(K.DIFF)
        .distinct()
        .collectAsList();
    Set<String> roots = SparkUtils.getStringSetFromRows(rootsTmp);
    Dataset<Row> puzzlePhase2Df = null;
    Dataset<Row> tmpDf;
    int ph2WordCount = 0;
    for (int charInWord : puzzle.getFinalClue()) {
      ph2WordCount++;
      log.debug(
          "Final clue: word #{}/{}.", ph2WordCount,
          puzzle.getFinalClue().size());
      Dataset<Row> df = null;
      int rootIndex = 0;
      for (String root : roots) {
        log.debug(
            "Looking for all permutations of: {} #{}/{}.",
            root, ++rootIndex, roots.size());
        Dataset<Row> df0 =
            buildPhase2Anagrams(root, charInWord, ph2WordCount, K.MAX_RESULT);

        // Combining all dataframes
        if (df == null) {
          df = df0.cache();
        } else {
          df = df.unionByName(df0).cache();
        }
      }
      if (df == null) {
        throw new JumbleException("Could not complete phase 2");
      }
      tmpDf = df.select(K.DIFF + "_" + ph2WordCount).distinct();
      roots = SparkUtils.getSortedStringSetFromRows(tmpDf.collectAsList());

      if (puzzlePhase2Df == null) {
        puzzlePhase2Df = df.withColumn(
            K.REV_SCORE,
            col(K.FREQ + "_1").cast(DataTypes.LongType));
      } else {
        puzzlePhase2Df = puzzlePhase2Df
            .join(
                df,
                puzzlePhase2Df.col(K.DIFF + "_" + (ph2WordCount - 1))
                    .equalTo(df.col(K.ROOT + "_" + ph2WordCount)),
                "inner")
            .withColumn(
                K.REV_SCORE,
                expr(K.REV_SCORE + "*" + K.FREQ + "_" + ph2WordCount));
      }
      if (log.isTraceEnabled()) {
        puzzlePhase2Df.show();
      }
    }
    if (puzzlePhase2Df == null) {
      throw new JumbleException("Could not initialize phase storage");
    }
    puzzlePhase2Df = puzzlePhase2Df
        .filter(col(K.DIFF + "_" + ph2WordCount).isNotNull())
        .orderBy(K.REV_SCORE);
    if (log.isTraceEnabled()) {
      puzzlePhase2Df.show();
    }
    log.info("Solutions for final clue (phase 2):");
    prettyPrintFinal(puzzlePhase2Df, ph2WordCount);

    long t2 = System.currentTimeMillis();
    log.debug("Phase 2: took {} ms.", (t2 - t1));

    // Phase 3: Validating first part of brain teaser against final clue
    // ----
    puzzlePhase1Df = puzzlePhase1Df.join(
        puzzlePhase2Df,
        puzzlePhase1Df.col(K.FINAL_CLUE)
            .equalTo(puzzlePhase2Df.col(K.ROOT + "_1")),
        "leftsemi");
    if (log.isTraceEnabled()) {
      puzzlePhase1Df.show();
    }
    log.info("Solutions for phase 1:");
    prettyPrint(puzzlePhase1Df, ph1WordCount);
    long t3 = System.currentTimeMillis();
    log.debug("Phase 3: took {} ms.", (t3 - t2));

    log.info("Game played in {} ms.", (t3 - t0));
  }

  /**
   * Pretty print the output of the final clue result.
   * 
   * @param df
   * @param finalClueWordCount
   */
  private void prettyPrintFinal(Dataset<Row> df, int finalClueWordCount) {
    Column[] columns = new Column[finalClueWordCount];
    for (int i = 1; i <= finalClueWordCount; i++) {
      columns[i - 1] = col(K.WORD + "_" + i);
    }
    Dataset<Row> prettyPrinterDf = df
        .withColumn(K.FINAL_CLUE, concat_ws(" ", columns))
        .drop(df.columns());
    if (log.isTraceEnabled()) {
      prettyPrinterDf.show();
    }
    List<Row> rows = prettyPrinterDf.collectAsList();
    int count = 0;
    for (Row r : rows) {
      log.info("Solution #{}/{}: {}", ++count, rows.size(), r.getString(0));
      if (count >= K.MAX_SOLUTION_FINAL_CLUE) {
        log.info("Limiting out put to first {} solutions.",
            K.MAX_SOLUTION_FINAL_CLUE);
        break;
      }
    }
  }

  /**
   * Pretty print the result of phase 1 and phase 3: takes a dataframe and
   * displays all columns that matters in the order of priority.
   * 
   * @param df
   * @param wordCount
   */
  private void prettyPrint(Dataset<Row> df, int wordCount) {
    df = df.cache();
    log.trace("df contains {} rows", df.count());
    for (int i = 1; i <= wordCount; i++) {
      long t0 = System.currentTimeMillis();
      List<Row> rows = df
          .select(K.WORD + "_" + i)
          .distinct()
          .orderBy(col(K.FREQ + "_" + i).asc()).collectAsList();
      long t1 = System.currentTimeMillis();
      if (log.isInfoEnabled()) {
        log.info("Word #{}: {}", i, SparkUtils.prettyPrintListRows(rows));
        log.debug("Extraction took {} ms", (t1 - t0));
      }
    }

  }

  /**
   * Build anagrams for phase 1.
   * 
   * @param word
   * @param list
   * @param wordIndex
   * @return
   */
  private Dataset<Row> buildPhase1Anagrams(
      String word,
      List<Integer> list,
      int wordIndex) {
    String sql =
        "SELECT * FROM "
            + K.ALL_WORDS + " WHERE " + K.WORD + " IN ("
            + JumbleUtils.getPermutationsAsCommaSeparatedList(word)
            + ") ORDER BY freq ASC";
    return spark
        .sql(sql)
        .withColumn(K.CHARS_TO_EXTRACT, lit(list.toArray(new Integer[0])))
        .withColumn(
            K.CHARS,
            callUDF("extractChars", col(K.WORD), col(K.CHARS_TO_EXTRACT)))
        .drop(K.CHARS_TO_EXTRACT)
        .withColumnRenamed(K.WORD, K.WORD + "_" + wordIndex)
        .withColumnRenamed(K.FREQ, K.FREQ + "_" + wordIndex)
        .withColumnRenamed(K.CHARS, K.CHARS + "_" + wordIndex);
  }

  private Dataset<Row> buildPhase2Anagrams(String word, int wordLength,
      int wordIndex, int maxResults) {
    String sql =
        "SELECT * FROM "
            + K.ALL_WORDS + " WHERE "
            + K.WORD + " IN ("
            + JumbleUtils.getSubPermutationsAsCommaSeparatedList(
                word, wordLength)
            + ") ORDER BY " + K.FREQ + " ASC LIMIT " + maxResults;
    return spark
        .sql(sql)
        .withColumn(K.ROOT + "_" + wordIndex, lit(word))
        .withColumnRenamed(K.WORD, K.WORD + "_" + wordIndex)
        .withColumnRenamed(K.FREQ, K.FREQ + "_" + wordIndex)
        .withColumn(K.DIFF + "_" + wordIndex,
            callUDF(
                "subtractString",
                col(K.ROOT + "_" + wordIndex),
                col(K.WORD + "_" + wordIndex)));
  }
}
