package sudoku.util;

import java.util.BitSet;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Small integer sets implemented as bits in a single integer.
 * Sets can only be subsets of [0..31], but all argument checking is omitted for performance.
 */
public class SmallIntSet {
  private SmallIntSet() {
    throw new AssertionError("This class cannot be instantiated");
  }

  /**
   * True if a set is empty.
   */
  public static boolean isEmpty(int set) {
    return set == 0;
  }

  /**
   * True if a set is not empty.
   */
  public static boolean nonEmpty(int set) {
    return set != 0;
  }

  /**
   * Adds a number to a set and returns a new set.
   */
  public static int plus(int set, int n) {
    return set | 1 << n;
  }

  /**
   * Removes a number from a set and returns a new set.
   */
  public static int minus(int set, int n) {
    return set & ~(1 << n);
  }

  /**
   * True if the set contains the number.
   */
  public static boolean contains(int set, int n) {
    return (set & 1 << n) != 0;
  }

  /**
   * Size (cardinality) of the set.
   */
  public static int size(int set) {
    return Integer.bitCount(set);
  }

  /**
   * Union of two sets.
   */
  public static int union(int set, int other) {
    return set | other;
  }

  /**
   * Intersection of two sets.
   */
  public static int intersect(int set, int other) {
    return set & other;
  }

  /**
   * Set difference (asymmetric).
   */
  public static int diff(int set, int other) {
    return set & ~other;
  }

  /**
   * Smallest number in the set. Undefined if the set is empty.
   */
  public static int min(int set) {
    return Integer.numberOfTrailingZeros(set);
  }

  /**
   * Largest number in the set. Undefined if the set is empty.
   */
  public static int max(int set) {
    return Integer.numberOfTrailingZeros(Integer.highestOneBit(set));
  }

  /**
   * Converts a small set to a Java BitSet.
   */
  public static BitSet toSet(int set) {
    return BitSet.valueOf(new long[]{set & 0xFFFFFFFFL});
  }

  /**
   * Converts a small set to a Java IntStream.
   */
  public static IntStream stream(int set) {
    var sb = IntStream.builder();
    int rem = set;
    while (rem != 0) {
      sb.accept(Integer.numberOfTrailingZeros(rem)); // lowest bit
      rem &= rem - 1; // clears lowest bit
    }
    return sb.build();
  }

  /**
   * An iterator over all the set values, in order.
   */
  public static PrimitiveIterator.OfInt iterator(int set) {
    return new PrimitiveIterator.OfInt() {
      private int rem = set;

      public boolean hasNext() {
        return rem != 0;
      }

      public int nextInt() {
        if (rem == 0) throw new NoSuchElementException();
        int next = Integer.numberOfTrailingZeros(rem); // lowest bit
        rem &= rem - 1; // clears lowest bit
        return next;
      }
    };
  }

  /**
   * String representation.
   */
  public static String mkString(int set, String start, String sep, String end) {
    return stream(set)
        .mapToObj(String::valueOf)
        .collect(Collectors.joining(sep, start, end));
  }

  /**
   * String representation. Equivalent to {@code mkString(set, "", sep, "")}.
   */
  public static String mkString(int set, String sep) {
    return mkString(set, "", sep, "");
  }

  /**
   * String representation. Equivalent to {@code mkString(set, "")}.
   */
  public static String mkString(int set) {
    return mkString(set, "");
  }

  /**
   * The empty set.
   */
  public static final int empty = 0;

  /**
   * Set from a stream.
   */
  public static int from(IntStream nums) {
    return nums.reduce(empty, SmallIntSet::plus);
  }

  /**
   * Set from a range.
   */
  public static int fromRange(int min, int length) {
    return (1 << length) - 1 << min;
  }

  /**
   * Set from an argument list.
   */
  public static int from(int num, int... nums) {
    return from(IntStream.concat(IntStream.of(num), IntStream.of(nums)));
  }
}
