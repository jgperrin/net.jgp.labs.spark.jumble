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
import java.util.Set;

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
      app.play("puzzle6");
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

    // If frequency is 0, it is actually very rare, so to ease sorting,
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
   */
  private void play(String title) throws IOException {
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
    puzzleResultDf.show();

    Dataset<Row> r=null;
    String w0 = "veetpdwyhaa";
    for (Integer charInWord : puzzle.getFinalClue()) {
      // Set<String> s = JumbleUtils.getSubPermutations("veetpdwyhaa",
      // charInWord);
      // log.debug(JumbleUtils.setToPrettyString(s));

//      String sql =
//          "SELECT * FROM all_words WHERE "
//              + K.WORD + " IN ("
//              + JumbleUtils.getSubPermutationsAsCommaSeparatedList(
//                  w0, 5)
//              + ") ORDER BY freq ASC";
//      Dataset<Row> r1 = spark.sql(sql);
//      r1.show();
//      List<Row> list = r1.collectAsList();
//      String w1 = list.get(0).getString(0);
//      String w2 = JumbleUtils.subtract(w0, w1);
//      log.debug("{} - {} = {}", w0, w1, w2);
//
//      sql =
//          "SELECT * FROM all_words WHERE "
//              + K.WORD + " IN ("
//              + JumbleUtils.getSubPermutationsAsCommaSeparatedList(
//                  w2, 3)
//              + ") ORDER BY freq ASC";
//      Dataset<Row> r2 = spark.sql(sql);
//      r2.show();
//      
//       list = r2.collectAsList();
//      String w3 = list.get(0).getString(0);
//      String w4 = JumbleUtils.subtract(w2, w3);
//      log.debug("{} - {} = {}", w2, w3, w4);
//
//      sql =
//          "SELECT * FROM all_words WHERE "
//              + K.WORD + " IN ("
//              + JumbleUtils.getSubPermutationsAsCommaSeparatedList(
//                  w4, 3)
//              + ") ORDER BY freq ASC";
//      Dataset<Row> r3 = spark.sql(sql);
//      r3.show();
//      
//      Dataset<Row> r = r1.crossJoin(r2).crossJoin(r3);
//      r.show();
      String sql =
        "SELECT * FROM all_words WHERE "
            + K.WORD + " IN ("
            + JumbleUtils.getSubPermutationsAsCommaSeparatedList(
                w0, 5)
            + ") ORDER BY freq ASC";
      Dataset<Row> r1 = spark.sql(sql);
      if (r==null) {
        r=r1;
      }else
      {
        r = r.crossJoin(r1);
      }
    }
    r.show();
  }

  private Dataset<Row> buildAnagrams(
      String word,
      List<Integer> list,
      int wordIndex) {
    String sql =
        "SELECT * FROM all_words WHERE " + K.WORD + " IN ("
            + JumbleUtils.getPermutationsAsCommaSeparatedList(word)
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
