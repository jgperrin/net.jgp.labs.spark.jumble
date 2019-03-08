package net.jgp.labs.spark.jumble.models;

import java.util.List;

public class Word {
  private String word;
  private List<Integer> charToReport;

  /**
   * @return the word
   */
  public String getWord() {
    return word;
  }

  /**
   * @param word
   *          the word to set
   */
  public void setWord(String word) {
    this.word = word;
  }

  /**
   * @return the charToReport
   */
  public List<Integer> getCharToReport() {
    return charToReport;
  }

  /**
   * @param charToReport
   *          the charToReport to set
   */
  public void setCharToReport(List<Integer> charToReport) {
    this.charToReport = charToReport;
  }

}
