package net.jgp.labs.spark.jumble;

import java.util.Collection;

import org.apache.spark.sql.api.java.UDF2;

import scala.collection.JavaConversions;
import scala.collection.mutable.WrappedArray;

/**
 * Spark User defined function to extract characters at various positions in
 * a string and return those characters as a combined string.
 * 
 * <bold>Note:</bold> Characters' position starts at 1 as the source is a
 * user-friendly YAML file, supposed to be filled by a non-IT person.
 * 
 * @author jgp
 *
 */
public class CharacterExtractorUdf
    implements UDF2<String, WrappedArray<Integer>, String> {

  private static final long serialVersionUID = -201966159201746851L;

  /**
   * Method called by the UDF to extract the data. See class-level Javadoc.
   */
  @Override
  public String call(String t1, WrappedArray<Integer> t2) throws Exception {
    Collection<Integer> collection = JavaConversions.asJavaCollection(t2);
    StringBuilder res = new StringBuilder();
    for (Integer i : collection) {
      res.append(t1.charAt(i - 1));
    }
    return res.toString();
  }
}
