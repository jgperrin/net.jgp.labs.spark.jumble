package net.jgp.labs.spark.jumble.models;

import java.util.List;

public class Puzzle {

  private String date;
  private List<Integer> finalClue;
  private String title;
  private List<Word> words;

  /**
   * @return the date
   */
  public String getDate() {
    return date;
  }

  /**
   * @return the finalClue
   */
  public List<Integer> getFinalClue() {
    return finalClue;
  }

  /**
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * @return the words
   */
  public List<Word> getWords() {
    return words;
  }

  /**
   * @param date
   *          the date to set
   */
  public void setDate(String date) {
    this.date = date;
  }

  /**
   * @param finalClue
   *          the finalClue to set
   */
  public void setFinalClue(List<Integer> finalClue) {
    this.finalClue = finalClue;
  }

  /**
   * @param title
   *          the title to set
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * @param words
   *          the words to set
   */
  public void setWords(List<Word> words) {
    this.words = words;
  }

}
