package net.jgp.labs.spark.jumble.utils;

/**
 * (K)onstants
 * 
 * K is used to identify constants, and is voluntarily reduced to a single
 * letter class to increase readability in the application.
 * 
 * Etymology: I use K because it is short (so it does not complexify the
 * code) and has a kind of sweet German side to it.
 * 
 * @author jgp
 */
public abstract class K {

  /**
   * Limits the number of results per query on anagrams, as those can become
   * really crazy. Queries are sorted by popularity anyway.
   */
  public static final int MAX_RESULT = 30;

  /**
   * Limits the number of solutions for the final clue.
   */
  public static final int MAX_SOLUTION_FINAL_CLUE = 15;

  public static final String ALL_WORDS = "all_words";
  public static final String CHARS = "chars";
  public static final String CHARS_TO_EXTRACT = "chars_to_extract";
  public static final String DIFF = "diff";
  public static final String FINAL_CLUE = "final_clue";
  public static final String FREQ = "freq";
  public static final String FREQ_TRIM = "freq_trim";
  public static final String FREQUENCY = "frequency";
  public static final String RAW_FREQ = "raw_freq";
  public static final String REV_SCORE = "reverse_score";
  public static final String ROOT = "root";
  public static final String WORD = "word";

  private K() {
  }

}
