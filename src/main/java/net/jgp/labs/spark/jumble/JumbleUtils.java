package net.jgp.labs.spark.jumble;

import java.util.HashSet;
import java.util.Set;

public abstract class JumbleUtils {

  private JumbleUtils() {
  }

  /**
   * Builds a list of unique permutations.
   * 
   * @param word
   * @return
   */
  public static Set<String> getPermutations(String word) {
    Set<String> permutations = new HashSet<>();
    getPermutations("", word.toLowerCase(), permutations);
    return permutations;
  }

  /**
   * Recursive part of the permutation builder.
   * 
   * @param prefix
   * @param word
   * @param permutations
   */
  private static void getPermutations(String prefix, String word, Set<String> permutations) {
    int n = word.length();
    if (n == 0) {
      // we have a word!
      permutations.add(prefix);
    }
    else {
      for (int i = 0; i < n; i++) {
        getPermutations(
            prefix + word.charAt(i),
            word.substring(0, i) + word.substring(i + 1, n),
            permutations);
      }
    }
  }
}
