package net.jgp.labs.spark.jumble;

import static org.apache.spark.sql.functions.*;
import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.lit;
import static org.apache.spark.sql.functions.split;
import static org.apache.spark.sql.functions.trim;
import static org.apache.spark.sql.functions.when;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
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

/**
 * What does it do?
 * 
 * @author jgp
 */
public class JumbleSolverApp {
  private static Logger log =
      LoggerFactory.getLogger(JumbleSolverApp.class);

  private SparkSession spark = null;
  private Dataset<Row> df = null;
  private String cwd = null;

  /**
   * main() is your entry point to the application.
   * 
   * @param args
   */
  public static void main(String[] args) {
    JumbleSolverApp app = new JumbleSolverApp();
    try {
      app.play("puzzle1");
    } catch (JsonParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (JsonMappingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public JumbleSolverApp() {
    // Local initialization
    this.cwd = System.getProperty("user.dir");

    // Creates a session on a local master
    spark = SparkSession.builder()
        .appName("Jumble solver")
        .master("local[*]")
        .getOrCreate();

    spark.udf().register(
        "extractChars",
        new CharacterExtractorUdf(),
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

    df = spark.read().format("csv")
        .option("sep", ":")
        .option("ignoreLeadingWhiteSpace", true)
        .schema(schema)
        .load("data/freq_dict.json");// _puzzle6.json");

    df = df.filter(col("frequency").isNotNull());
    df = df.withColumn("freq_trim", trim(col("frequency")));
    df = df
        .withColumn(
            "raw_freq",
            split(col("freq_trim"), ",")
                .getItem(0)
                .cast(DataTypes.IntegerType))
        .drop("frequency")
        .drop("freq_trim");

    // If frequency is one, it is actually very rare, so to ease sorting,
    // let's assume it's more than the max, set to 9887.
    df = df
        .withColumn(
            "freq",
            when(col("raw_freq").equalTo(0), 10000)
                .otherwise(col("raw_freq")))
        .drop("raw_freq");
    df.createOrReplaceTempView("all_words");

    // Shows at most 20 rows from the dataframe
    df.show(20);
  }

  /**
   * Start a game!
   * 
   * @param title
   * @throws IOException
   * @throws JsonMappingException
   * @throws JsonParseException
   */
  private void play(String title)
      throws JsonParseException, JsonMappingException, IOException {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    Puzzle puzzle = mapper.readValue(
        new File(cwd + "/data/" + title + ".yaml"),
        Puzzle.class);
    log.info("Now playing: {}", puzzle.getTitle());
    Dataset<Row> puzzleResultDf = null;
    int wordCount = 0;
    for (Word w : puzzle.getWords()) {
      wordCount++;
      Dataset<Row> anagramsDf =
          buildAnagrams(w.getWord(), w.getCharToReport(), wordCount);
      if (puzzleResultDf == null) {
        puzzleResultDf = anagramsDf
            .withColumn(K.REV_SCORE, col(K.FREQ + "_1"));
      } else {
        puzzleResultDf = puzzleResultDf
            .crossJoin(anagramsDf)
            .withColumn(
                K.REV_SCORE,
                expr(K.REV_SCORE + "*" + K.FREQ + "_" + (wordCount - 1)))
            .withColumn(
                K.FINAL_CLUE,
                concat(col(K.CHARS)));
      }
    }
    puzzleResultDf.show();
  }

  private Dataset<Row> buildAnagrams(String word, List<Integer> list,
      int wordIndex) {
    String sql =
        "SELECT * FROM all_words WHERE word IN ("
            + JumbleUtils.getAnagramsAsString(word)
            + ") ORDER BY freq ASC";
    Dataset<Row> r = spark
        .sql(sql)
        .withColumn("charsToExtract", lit(list.toArray(new Integer[0])))
        .withColumn(
            K.CHARS,
            callUDF("extractChars", col(K.WORD), col("charsToExtract")))
        .drop("charsToExtract")
        .withColumnRenamed(K.WORD, K.WORD + "_" + wordIndex)
        .withColumnRenamed(K.FREQ, K.FREQ + "_" + wordIndex)
        .withColumnRenamed(K.CHARS, K.CHARS + "_" + wordIndex);
    r.show();
    return r;
  }
}
