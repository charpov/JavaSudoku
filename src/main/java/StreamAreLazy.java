import sudoku.util.SmallIntSet;

import java.util.List;

@SuppressWarnings("Convert2MethodRef")
public class StreamAreLazy {
  private static int verboseSquare(int n) {
    System.out.printf("squaring %d%n", n);
    return n * n;
  }

  static void usingStreams() {
    var nums = List.of(1, 3, 7, 9);
    var squares = nums.stream().map(num -> verboseSquare(num)); // nothing printed
    var largeSquares = squares.filter(square -> square > 20); // nothing printed
    var n = largeSquares.findFirst(); // prints squaring 1, squaring 3, squaring 7

    System.out.println(n);
  }

  static void usingSets() {
    int set = SmallIntSet.from(1, 3, 7, 9);
    var squares = SmallIntSet.stream(set).map(num -> verboseSquare(num));
    var largeSquares = squares.filter(square -> square > 20); // nothing printed
    var n = largeSquares.findFirst(); // prints squaring 1, squaring 3, squaring 7

    System.out.println(n);
  }

  public static void main(String[] args) {
    usingStreams();
    usingSets();
  }
}
