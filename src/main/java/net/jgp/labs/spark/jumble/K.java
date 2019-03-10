package net.jgp.labs.spark.jumble;

/**
 * (K)onstants - K is used to identify constants, and is voluntarily
 * reduced to a single letter class to increase readability in the
 * application.
 * 
 * @author jgp
 */
public abstract class K {
  private K() {
  }

  public static final String WORD = "word";
  public static final String FREQ = "freq";
  public static final String CHARS = "chars";
  public static final String REV_SCORE = "reverse_score";
  public static final String FINAL_CLUE = "final_clue";
  public static final String ROOT = "root";
  public static final String DIFF = "diff";
  public static final String ALL_WORDS = "all_words";
  public static final String CHARS_TO_EXTRACT = "chars_to_extract";

}
