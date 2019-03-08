package net.jgp.labs.spark.jumble;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JumbleUtilsTest {
  private static Logger log =
      LoggerFactory.getLogger(JumbleUtilsTest.class);

  @Test
  public void testGetPermutations0() {
    List<String> expected = Arrays.asList("ab", "ba");
    Set<String> res = JumbleUtils.getPermutations(expected.get(0));
    debugPrettyPrint(res);
    assertUnsortedArrayEquals("!ok", expected.toArray(), res.toArray());
  }

  @Test
  public void testGetPermutations1() {
    String[] expectedArray = { "abc", "bca", "acb", "cba", "bac", "cab" };
    Set<String> res = JumbleUtils.getPermutations(expectedArray[0]);
    debugPrettyPrint(res);
    assertUnsortedArrayEquals("!ok", expectedArray, res.toArray());
  }

  @Test
  public void testGetPermutations2() {
    List<String> expected = Arrays.asList("aba", "aab", "baa");
    Set<String> res = JumbleUtils.getPermutations(expected.get(0));
    debugPrettyPrint(res);
    assertUnsortedArrayEquals("!ok", expected.toArray(), res.toArray());
  }

  @Test
  public void testGetPermutations3() {
    List<String> expected = Arrays.asList("aaa");
    Set<String> res = JumbleUtils.getPermutations(expected.get(0));
    debugPrettyPrint(res);
    assertUnsortedArrayEquals("!ok", expected.toArray(), res.toArray());
  }

  /**
   * Quick pretty printer to display a Set<String> nicely. Only works when
   * debug is on.
   * 
   * @param res
   *          Set to print.
   */
  private void debugPrettyPrint(Set<String> res) {
    if (!log.isDebugEnabled()) {
      return;
    }
    log.debug("{} elements", res.size());
    log.debug("Content: {}", JumbleUtils.setToPrettyString(res));
  }

  /**
   * Additional asserter to validate arrays in an unsorted way.
   * 
   * @param message
   * @param expecteds
   * @param actuals
   */
  private static void assertUnsortedArrayEquals(
      String message,
      Object[] expecteds,
      Object[] actuals) {
    Arrays.sort(expecteds);
    Arrays.sort(actuals);
    assertArrayEquals(message, expecteds, actuals);
  }

}
