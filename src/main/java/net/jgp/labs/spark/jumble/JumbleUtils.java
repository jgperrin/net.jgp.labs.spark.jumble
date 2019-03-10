package net.jgp.labs.spark.jumble;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JumbleUtils {
  private static Logger log = LoggerFactory.getLogger(JumbleUtils.class);

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
  private static void getPermutations(
      String prefix,
      String word,
      Set<String> permutations) {
    int n = word.length();
    if (n == 0) {
      // we have a word!
      permutations.add(prefix);
    } else {
      for (int i = 0; i < n; i++) {
        getPermutations(
            prefix + word.charAt(i),
            word.substring(0, i) + word.substring(i + 1, n),
            permutations);
      }
    }
  }

  public static String getPermutationsAsCommaSeparatedList(String word) {
    Set<String> permutations = getPermutations(word);
    return setToPrettyString(permutations);
  }

  public static String getSubPermutationsAsCommaSeparatedList(
      String word,
      int length) {
    Set<String> permutations = getSubPermutations(word, length);
    return setToPrettyString(permutations);
  }

  public static String setToPrettyString(Set<String> arg0) {
    boolean first = true;
    StringBuilder output = new StringBuilder();
    for (String r : arg0) {
      if (!first) {
        output.append(", ");
      }
      output.append('"');
      output.append(r);
      output.append('"');
      first = false;
    }
    return output.toString();
  }

  /**
   * Builds a list of all sub permutations of a word using the specify
   * length of the desired word.
   * 
   * Example: if the word is "abc" and the length is 2, then the result
   * contains 6 elements, which are: "ab", "bc", "ac", "ca", "ba", "cb"
   * 
   * @param word
   * @param length
   * @return
   */
  public static Set<String> getSubPermutations(String word, int length) {
    Set<String> subPermutations = new HashSet<>();
    List<boolean[]> masks = new ArrayList<>();

    int l = word.length();
    long upperBound = Math.round(Math.pow(2, l));
    for (long i = 0; i < upperBound; i++) {
      boolean[] bits = new boolean[l];
      // Creating a mask
      for (int j = l - 1; j >= 0; j--) {
        bits[j] = (i & (1 << j)) != 0;
      }
      // Is the mask valid?
      boolean valid = true;
      int count = 0;
      for (int j = 0; j < l; j++) {
        if (bits[j]) {
          count++;
          if (count > length) {
            valid = false;
            break;
          }
        }
      }
      if (valid && count == length) {
        log.trace("{} = {}", i, Arrays.toString(bits));
        addCopyOfMaskToList(masks, bits);
      }
    }

    for (boolean[] m : masks) {
      String res = "";
      for (int i = 0; i < m.length; i++) {
        if (m[i]) {
          res += word.charAt(i);
        }
      }
      log.trace("Root: {}", res);

      subPermutations.addAll(getPermutations(res));
    }

    return subPermutations;
  }

  private static void addCopyOfMaskToList(
      List<boolean[]> masks,
      boolean[] mask) {
    masks.add(Arrays.copyOf(mask, mask.length));
  }

  public static String subtract(String minuend, String subtrahend) {
    int subtrahendLength = subtrahend.length();
    char[] minuendCharacterArray = minuend.toCharArray();
    for (int i = 0; i < subtrahendLength; i++) {
      for (int j = 0; j < minuendCharacterArray.length; j++) {
        if (minuendCharacterArray[j] == subtrahend.charAt(i)) {
          minuendCharacterArray[j] = '*';
          break;
        }
      }
    }
    StringBuilder output = new StringBuilder();
    for (int j = 0; j < minuendCharacterArray.length; j++) {
      if (minuendCharacterArray[j] != '*') {
        output.append(minuendCharacterArray[j]);
      }
    }
    return output.toString();
  }

}
