package net.jgp.labs.spark.jumble.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bunch of utilities to help manipulate strings for the Jumble game. No
 * dependency on Spark.
 * 
 * @author jgp
 */
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

  /**
   * Builds a comma separated list of all the permutations of a word.
   * 
   * @param word
   * @return
   */
  public static String getPermutationsAsCommaSeparatedList(String word) {
    Set<String> permutations = getPermutations(word);
    return setToPrettyString(permutations);
  }

  /**
   * Builds all sub permutations.
   * 
   * @param word
   * @param length
   * @return
   */
  public static String getSubPermutationsAsCommaSeparatedList(
      String word,
      int length) {
    Set<String> permutations = getSubPermutations(word, length);
    return setToPrettyString(permutations);
  }

  /**
   * Builds a pretty string from a set.
   * 
   * @param arg0
   * @return
   */
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
      // Keep only the masks we want
      if (valid && count == length) {
        log.trace("{} = {}", i, Arrays.toString(bits));
        addCopyOfMaskToList(masks, bits);
      }
    }

    // Build the first level of permutations based on the mask
    Set<String> rootPermutations = new HashSet<>();
    for (boolean[] m : masks) {
      String res = "";
      for (int i = 0; i < m.length; i++) {
        if (m[i]) {
          res += word.charAt(i);
        }
      }
      log.trace("Root (might not be unique, will be filtered): {}", res);

      // Get all the permutations
      rootPermutations.add(res);
    }
    Set<String> subPermutations = new HashSet<>();
    for (String w : rootPermutations) {
      subPermutations.addAll(getPermutations(w));
    }
    return subPermutations;
  }

  /**
   * Private method to secure (copy) a mask in the array containing all
   * masks.
   * 
   * @param masks
   * @param mask
   */
  private static void addCopyOfMaskToList(
      List<boolean[]> masks,
      boolean[] mask) {
    masks.add(Arrays.copyOf(mask, mask.length));
  }

  /**
   * Subtract a string from another string at the character level, e.g.
   * "abcdef" - "abc" = "def".
   * 
   * @param minuend
   * @param subtrahend
   * @return
   */
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

  /**
   * Sorts the characters in a string.
   * 
   * @param arg0
   * @return
   */
  public static String sortString(String arg0) {
    char tempArray[] = arg0.toCharArray();
    Arrays.sort(tempArray);
    return new String(tempArray);
  }

}
