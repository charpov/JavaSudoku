import java.util.List;

public class StreamDemo {
  public static void main(String[] args) {
    var nums = List.of(1, 3, 7, 9);
    var squares = nums.stream().map(num -> num * num).toList(); // [1, 9, 49, 81]

    var strings = List.of("", "X", "\n", "Y");
    var nonBlank = strings.stream().filter(str -> !str.isBlank()).toList(); // [X, Y]

    List<String> lines = List.of("X", "ABC", "\n");
    int shortest = lines.stream()
                        .filter(str -> !str.isBlank())
                        .mapToInt(String::length)
                        .min()
                        .orElse(0);
    System.out.println(squares);
    System.out.println(nonBlank);
    System.out.println(shortest);
  }
}
