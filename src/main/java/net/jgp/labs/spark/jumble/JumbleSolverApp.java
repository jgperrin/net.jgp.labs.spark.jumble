package net.jgp.labs.spark.jumble;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import static org.apache.spark.sql.functions.*;

import java.io.File;
import java.io.IOException;

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
      app.start("puzzle6");
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
    StructType schema = DataTypes.createStructType(new StructField[] {
        DataTypes.createStructField(
            "word",
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
        .load("data/freq_dict_puzzle6.json");

    df = df.filter(col("frequency").isNotNull());
    df = df.withColumn("freq_trim", trim(col("frequency")));
    df = df
        .withColumn(
            "freq",
            split(col("freq_trim"), ",")
                .getItem(0)
                .cast(DataTypes.IntegerType))
        .drop("frequency")
        .drop("freq_trim");
    df.createOrReplaceTempView("all_words");

    // Shows at most 20 rows from the dataframe
    df.show(20);
  }

  /**
   * The processing code.
   * 
   * @param title
   * @throws IOException 
   * @throws JsonMappingException 
   * @throws JsonParseException 
   */
  private void start(String title) throws JsonParseException, JsonMappingException, IOException {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    Puzzle puzzle = mapper.readValue(
        new File(cwd + "/data/" + title + ".yaml"),
        Puzzle.class);
    log.info("Now playing: {}", puzzle.getTitle());
    for (Word w: puzzle.getWords()) {
      findAnagram(w.getWord());      
    }
  }

  private void findAnagram(String word) {
    String sql =
        "SELECT * FROM all_words WHERE word IN ("
            + JumbleUtils.getAnagramsAsString(word)
            + ") ORDER BY freq DESC";
    Dataset<Row> r = spark.sql(sql);
    r.show();
  }
}
