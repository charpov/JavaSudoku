package sudoku;

import java.util.Arrays;
import java.util.stream.IntStream;

abstract class AbstractSudoku {
  final int[] valuesArray;

  AbstractSudoku(int[] valuesArray) {
    this.valuesArray = valuesArray;
  }

  /**
   * Equality. A Sudoku never equals a non-Sudoku and two Sudoku objects are equal if their
   * sequences of values are equal.
   */
  @Override
  public boolean equals(Object obj) {
    return obj instanceof AbstractSudoku that
        && (this == that || Arrays.equals(this.valuesArray, that.valuesArray));
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(valuesArray);
  }

  /**
   * String representation. A typical Sudoku grid looks like this:
   * {@snippet :
   *     9 . .    . 8 3    . 1 .
   *     2 . .    . 6 .    . . .
   *     . 7 .    . . .    4 . .
   *
   *     . . 7    . . 6    . 5 .
   *     . . 1    . 4 .    3 . .
   *     . 6 .    9 . .    7 . .
   *
   *     . . 9    . . .    . 8 .
   *     . . .    . 1 .    . . 4
   *     . 4 .    5 2 .    . . 6
   *}
   * Empty spots are represented as dots. Note the newlines between rows 3-4 and 6-7 and the four
   * spaces between columns 3-4 and 6-7, that help emphasize the 3x3 sub-grids. The string is
   * trimmed (it has no extra space at either end). In particular, it does not end with a newline.
   */
  @Override
  public String toString() {
    // Streams don't have a grouped method and implementing one is annoying
    var sb = new StringBuilder(217);
    int i = 0;
    for (int row = 0; row < 9; row++) {
      for (int col = 0; col < 9; col++) {
        int v = valuesArray[i++];
        sb.append(v == 0 ? "." : String.valueOf(v))
          .append(col == 2 || col == 5 ? "    " : col < 8 ? " " : "");
      }
      sb.append(row == 2 || row == 5 ? "\n\n" : row < 8 ? "\n" : "");
    }
    return sb.toString();
  }

  /**
   * Grid values, as a stream.
   */
  public IntStream values() {
    return IntStream.of(valuesArray);
  }

  /**
   * True is the grid is full (no empty squares).
   */
  public boolean isComplete() {
    return values().noneMatch(v -> v == 0);
  }

  /**
   * Number of filled squares in the grid.
   */
  public int filledCount() {
    return (int) values().filter(v -> v != 0).count();
  }

  /**
   * True if two locations conflict: same column, same row, or same 3x3 square.
   *
   * @throws IllegalArgumentException if `i` and `j` are the same location or if either is not a valid location.
   */
  protected static boolean conflict(int i, int j) {
    require(i != j && 0 <= i && 0 <= j && i < 81 && j < 81, "invalid index");
    int ic = i % 9;
    int jc = j % 9;
    return ic == jc                              // same column
        || i / 9 == j / 9                        // same row
        || i / 27 == j / 27 && ic / 3 == jc / 3; // same 3x3 square
  }

  /**
   * Utility for precondition validation.
   */
  static void require(boolean condition, String message, Object... args) {
    if (!condition) throw new IllegalArgumentException(message.formatted(args));
  }
}
