package net.jgp.labs.spark.jumble;

import static org.apache.spark.sql.functions.*;
import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.lit;
import static org.apache.spark.sql.functions.split;
import static org.apache.spark.sql.functions.trim;
import static org.apache.spark.sql.functions.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import net.jgp.labs.spark.jumble.models.Puzzle;
import net.jgp.labs.spark.jumble.models.Word;
import net.jgp.labs.spark.jumble.udf.CharacterExtractorUdf;
import net.jgp.labs.spark.jumble.udf.SortStringUdf;
import net.jgp.labs.spark.jumble.udf.SubtractStringUdf;
import net.jgp.labs.spark.jumble.utils.JumbleUtils;
import net.jgp.labs.spark.jumble.utils.K;
import net.jgp.labs.spark.jumble.utils.SparkUtils;

/**
 * What does it do?
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
    String game = "puzzle6";
    try {
      app.play(game);
    } catch (IOException e) {
      log.error(
          "An IO error happened while trying to load game {}: {}",
          game, e.getMessage(), e);
      ;
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
        .master("local")
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
            "frequency",
            DataTypes.StringType,
            false) });

    dictionaryDf = spark.read().format("csv")
        .option("sep", ":")
        .option("ignoreLeadingWhiteSpace", true)
        .schema(schema)
         .load("data/freq_dict.json");
        //.load("data/freq_dict_puzzle6.json");

    dictionaryDf = dictionaryDf.filter(col("frequency").isNotNull());
    dictionaryDf =
        dictionaryDf.withColumn("freq_trim", trim(col("frequency")));
    dictionaryDf = dictionaryDf
        .withColumn(
            "raw_freq",
            split(col("freq_trim"), ",")
                .getItem(0)
                .cast(DataTypes.IntegerType))
        .drop("frequency")
        .drop("freq_trim");

    // If frequency is 0, it is actually very rare, so to ease sorting,
    // let's assume it's more than the max, set to 9887.
    dictionaryDf = dictionaryDf
        .withColumn(
            "freq",
            when(col("raw_freq").equalTo(0), 10000)
                .otherwise(col("raw_freq")))
        .drop("raw_freq");
    dictionaryDf = dictionaryDf.cache();
    dictionaryDf.createOrReplaceTempView(K.ALL_WORDS);
  }

  /**
   * Start a game!
   * 
   * @param title
   * @throws IOException
   */
  private void play(String title) throws IOException {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    Puzzle puzzle = mapper.readValue(
        new File(cwd + "/data/" + title + ".yaml"),
        Puzzle.class);

    log.info("Now playing: {}", puzzle.getTitle());
    long t0 = System.currentTimeMillis();

    // Phase 1: Pre-solving 1st part of the brain teaser
    // ----
    Dataset<Row> puzzleResultDf = null;
    int wordCount = 0;
    for (Word w : puzzle.getWords()) {
      wordCount++;
      Dataset<Row> anagramsDf =
          buildAnagrams(w.getWord(), w.getCharToReport(), wordCount);
      if (puzzleResultDf == null) {
        puzzleResultDf = anagramsDf
            .withColumn(
                K.REV_SCORE,
                col(K.FREQ + "_1").cast(DataTypes.LongType))
            .withColumn(K.FINAL_CLUE, col(K.CHARS + "_1"));
      } else {
        puzzleResultDf = puzzleResultDf
            .crossJoin(anagramsDf)
            .withColumn(
                K.REV_SCORE,
                expr(K.REV_SCORE + "*" + K.FREQ + "_" + wordCount))
            .withColumn(
                K.FINAL_CLUE,
                concat(
                    col(K.FINAL_CLUE),
                    col(K.CHARS + "_" + wordCount)));
      }
    }
    puzzleResultDf = puzzleResultDf.withColumn(K.FINAL_CLUE,
        callUDF("sortString", col(K.FINAL_CLUE)));
    puzzleResultDf = puzzleResultDf.cache();
    if (log.isTraceEnabled()) {
      puzzleResultDf.show();
    }
    prettyPrint(puzzleResultDf, wordCount);

    long t1 = System.currentTimeMillis();
    log.debug("Phase 1: took {} ms.", (t1 - t0));

    // Phase 2: Solving second part of the teaser
    // ----
    List<Row> rootsTmp = puzzleResultDf
        .select(K.FINAL_CLUE)
        .as(K.DIFF)
        .distinct()
        .collectAsList();
    Set<String> roots = SparkUtils.getStringSetFromRows(rootsTmp);
    Dataset<Row> lastClueDf = null;
    int wordIndex = 0;
    for (int charInWord : puzzle.getFinalClue()) {
      wordIndex++;
      log.debug(
          "Final clue: word #{}/{}.", wordIndex,
          puzzle.getFinalClue().size());
      Dataset<Row> df = null;
      int rootIndex = 0;
      for (String root : roots) {
        String rootStr = root;
        log.debug(
            "Looking for all permutations of: {} #{}/{}.",
            rootStr, ++rootIndex, roots.size());
        String sql =
            "SELECT * FROM "
                + K.ALL_WORDS + " WHERE "
                + K.WORD + " IN ("
                + JumbleUtils.getSubPermutationsAsCommaSeparatedList(
                    rootStr, charInWord)
                + ") ORDER BY freq ASC";
        Dataset<Row> df0 = spark
            .sql(sql)
            .withColumn(K.ROOT + "_" + wordIndex, lit(rootStr))
            .withColumnRenamed(K.WORD, K.WORD + "_" + wordIndex)
            .withColumnRenamed(K.FREQ, K.FREQ + "_" + wordIndex)
            .withColumn(K.DIFF + "_" + wordIndex,
                callUDF(
                    "subtractString",
                    col(K.ROOT + "_" + wordIndex),
                    col(K.WORD + "_" + wordIndex)));
        if (df == null) {
          df = df0;
        } else {
          df = df.unionByName(df0);
        }
      }
      Dataset<Row> tmpDf = df
          .select(K.DIFF + "_" + wordIndex)
          .distinct();
      roots = SparkUtils.getSortedStringSetFromRows(tmpDf.collectAsList());

      if (lastClueDf == null) {
        lastClueDf = df.withColumn(
            K.REV_SCORE,
            col(K.FREQ + "_1").cast(DataTypes.LongType));
      } else {
        lastClueDf = lastClueDf
            .join(
                df,
                lastClueDf.col(K.DIFF + "_" + (wordIndex - 1))
                    .equalTo(df.col(K.ROOT + "_" + wordIndex)),
                "inner")
            .withColumn(
                K.REV_SCORE,
                expr(K.REV_SCORE + "*" + K.FREQ + "_" + wordIndex));
      }
      if (log.isTraceEnabled()) {
        lastClueDf.show();
      }
    }
    lastClueDf = lastClueDf
        .filter(col(K.DIFF + "_" + wordIndex).isNotNull())
        .orderBy(K.REV_SCORE);
    if (log.isTraceEnabled()) {
      lastClueDf.show();
    }
    prettyPrintFinal(lastClueDf, wordIndex);

    long t2 = System.currentTimeMillis();
    log.debug("Phase 2: took {} ms.", (t2 - t1));

    // Phase 3: Validating first part of brain teaser against final clue
    // ----
    puzzleResultDf = puzzleResultDf.join(
        lastClueDf,
        puzzleResultDf.col(K.FINAL_CLUE)
            .equalTo(lastClueDf.col(K.ROOT + "_1")),
        "leftsemi");
    if (log.isTraceEnabled()) {
      puzzleResultDf.show();
    }
    log.info("Final possible words for phase 1");
    prettyPrint(puzzleResultDf, wordCount);
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
          .select(K.WORD + "_" + i, K.FREQ + "_" + i)
          .distinct()
          .orderBy(col(K.FREQ + "_" + i).asc()).collectAsList();
      long t1 = System.currentTimeMillis();
      log.info("Word #{}: {}", i, SparkUtils.prettyPrintListRows(rows));
      log.trace("Extraction took {} ms", (t1 - t0));
    }

  }

  private Dataset<Row> buildAnagrams(
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
}
