package net.jgp.labs.spark.jumble.udf;

import org.apache.spark.sql.api.java.UDF1;

import net.jgp.labs.spark.jumble.utils.JumbleUtils;

/**
 * 
 * @author jgp
 *
 */
public class SortStringUdf
    implements UDF1<String, String> {

  private static final long serialVersionUID = -201969851L;

  /**
   * See class-level Javadoc.
   */
  @Override
  public String call(String stringToSort) throws Exception {
    return JumbleUtils.sortString(stringToSort);
  }
}
