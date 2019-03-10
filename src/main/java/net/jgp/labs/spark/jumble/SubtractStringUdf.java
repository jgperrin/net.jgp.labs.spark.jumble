package net.jgp.labs.spark.jumble;

import org.apache.spark.sql.api.java.UDF2;

/**
 * 
 * @author jgp
 *
 */
public class SubtractStringUdf
    implements UDF2<String, String, String> {

  private static final long serialVersionUID = -201966159851L;

  /**
   * See class-level Javadoc.
   */
  @Override
  public String call(String minuend, String subtrahend) throws Exception {
    return JumbleUtils.subtract(minuend, subtrahend);
  }
}
