package net.jgp.labs.spark.jumble;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import static org.apache.spark.sql.functions.*;

import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

/**
 * What does it do?
 * 
 * @author jgp
 */
public class JumbleSolverApp {

  private SparkSession spark = null;
  private Dataset<Row> df = null;

  /**
   * main() is your entry point to the application.
   * 
   * @param args
   */
  public static void main(String[] args) {
    JumbleSolverApp app = new JumbleSolverApp();
    app.start();
  }

  public JumbleSolverApp() {
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

    // Shows at most 20 rows from the dataframe
    df.show(20);
  }

  /**
   * The processing code.
   */
  private void start() {
    findAnagram("neeuv");
  }

  private void findAnagram(String word) {
    // TODO Auto-generated method stub
    
  }
}
