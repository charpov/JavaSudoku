package sudoku;

import sudoku.util.Memoizer;
import sudoku.util.SmallIntSet;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static sudoku.util.FlatMap.flatMap;

public class MemoSudoku extends AbstractSudoku {
  /**
   * Builds a new Instance. Characters between `'1'` and `'9'` are interpreted as digits.
   * Whitespaces are ignored. Any other character represents an empty slot.
   *
   * @throws IllegalArgumentException if the length of the input (whitespace ignored) is not 81,
   *                                  or if the values would produce an inconsistent grid.
   */
  public static MemoSudoku create(CharSequence str) {
    return new MemoSudoku(parse(str));
  }

  private static final int digits = SmallIntSet.fromRange(1, 9); // set {1,2,3,4,5,6,7,8,9}

  private static int[] generatePossibles(int[] valuesArray) {
    int[] possibles = new int[81]; // array of small sets
    Arrays.setAll(possibles, i ->
        valuesArray[i] != 0 ? 0 // no set needed for filled squares
            : Arrays.stream(conflictWith[i])
                    .reduce(digits, (set, j) -> SmallIntSet.minus(set, valuesArray[j])));
    return possibles;
  }

  private static final int[][] conflictWith;

  static {
    conflictWith = new int[81][];
    Arrays.setAll(conflictWith, i ->
        IntStream.range(0, 81).filter(j -> i != j && conflict(i, j)).toArray());
  }

  /**
   * True if the values would produce a grid is without conflicts. This is independent from the
   * grid being complete, i.e., both complete and partial grids can be consistent or inconsistent.
   */
  private static boolean isConsistent(int[] values) {
    return IntStream.range(0, 81)
                    .allMatch(i ->
                                  values[i] == 0 || Arrays.stream(conflictWith[i])
                                                          .allMatch(j -> values[j] != values[i]));
  }

  /**
   * Parses a textual source into a Su Doku puzzle. Characters between `'1'` and `'9'` are
   * interpreted as digits. Whitespaces are ignored. Any other character represents an empty slot.
   *
   * @throws IllegalArgumentException if the length of the input (whitespace ignored) is not 81, or if the values would produce
   *                                  an inconsistent grid.
   */
  static int[] parse(CharSequence str) {
    int[] values = str.chars().filter(c -> !Character.isWhitespace(c)).map(c -> ('1' <= c && c <= '9') ? c - '0' : 0).toArray();
    require(values.length == 81, "found %d values, needs 81", values.length);
    require(isConsistent(values), "values are not consistent");
    return values;
  }

  private MemoSudoku(int[] valuesArray, int[] knownPossibles) {
    super(valuesArray);
    this.knownPossibles = knownPossibles;
  }

  private MemoSudoku(int[] valuesArray) {
    this(valuesArray, null);
  }

  private final int[] knownPossibles;

  private int[] _possibles;

  int[] possibles() { // returns an array of sets
    if (_possibles == null) // delayed initialization
      // use knownPossibles if not null, otherwise compute
      _possibles = Objects.requireNonNullElseGet(knownPossibles, () -> generatePossibles(valuesArray));
    return _possibles;
  }

  MemoSudoku updated(int index, int value) {
    int[] newValues = valuesArray.clone();
    int[] newPossibles = possibles().clone();
    newValues[index] = value;
    for (int j : conflictWith[index])
      newPossibles[j] = SmallIntSet.minus(newPossibles[j], value);
    return new MemoSudoku(newValues, newPossibles);
  }

  int findBestEmpty() {
    int minIndex = -1;
    int minSize = Integer.MAX_VALUE;
    for (int i = 0; i < 81; i++) {
      if (valuesArray[i] == 0) {
        int s = SmallIntSet.size(possibles()[i]);
        if (s == 0) return i;
        if (s < minSize) {
          minIndex = i;
          minSize  = s;
        }
      }
    }
    return minIndex;
  }

  private Memoizer<MemoSudoku> _allSolutionsCached;

  private Memoizer<MemoSudoku> allSolutionsCached() {
    if (_allSolutionsCached == null) _allSolutionsCached = new Memoizer<>(solveIterator());
    return _allSolutionsCached;
  }

  // This doesn't work because stream iterators are not lazy enough
  private Stream<MemoSudoku> solveStream() {
    int index = findBestEmpty();
    if (index < 0) return Stream.of(this);
    return SmallIntSet.stream(possibles()[index]).boxed()
                      .flatMap(p -> updated(index, p).solveStream());
  }

  private Iterator<MemoSudoku> solveIterator() {
    int index = findBestEmpty();
    if (index < 0) return List.of(this).iterator();
    return flatMap(SmallIntSet.iterator(possibles()[index]),
                   p -> updated(index, p).solveIterator());
  }

  public Optional<MemoSudoku> solve() {
    var iterator = allSolutionsCached().iterator();
    return iterator.hasNext() ? Optional.of(iterator.next()) : Optional.empty();
  }

  public Set<MemoSudoku> allSolutions() {
    var solutions = new HashSet<MemoSudoku>();
    allSolutionsCached().iterator().forEachRemaining(solutions::add);
    return solutions;
  }

  public boolean solutionIsUnique() {
    var iterator = allSolutionsCached().iterator();
    if (!iterator.hasNext()) return false;
    iterator.next();
    return !iterator.hasNext();
  }
}
