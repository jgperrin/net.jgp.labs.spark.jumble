package net.jgp.labs.spark.jumble.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.spark.sql.Row;

/**
 * Various Spark utilities.
 * 
 * @author jgp
 */
public abstract class SparkUtils {

  private SparkUtils() {
  }

  /**
   * Prepare for pretty print a list of values in the first column of each row. Must be accessible as a string.
   *  
   * @param rows
   * @return
   */
  public static String prettyPrintListRows(List<Row> rows) {
    boolean first = true;
    StringBuilder sb = new StringBuilder();
    for (Row r : rows) {
      if (!first) {
        sb.append(", ");
      }
      sb.append(r.getString(0));
      first = false;
    }
    return sb.toString();
  }

  /**
   * Builds a sorted set of sorted first element of column 0;
   * 
   * @param rows
   * @return
   */
  public static Set<String> getSortedStringSetFromRows(List<Row> rows) {
    Set<String> words = new HashSet<>();
    for (Row row : rows) {
      words.add(JumbleUtils.sortString(row.getString(0)));
    }
    return words;
  }

  /**
   * Builds a set of the first element of column 0;
   * 
   * @param rows
   * @return
   */
  public static Set<String> getStringSetFromRows(List<Row> rows) {
    Set<String> words = new HashSet<String>();
    for (Row row : rows) {
      words.add(row.getString(0));
    }
    return words;
  }
}
