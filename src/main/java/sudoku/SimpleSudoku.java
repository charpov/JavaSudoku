package sudoku;

import java.util.*;

import static sudoku.MemoSudoku.parse;

public class SimpleSudoku extends AbstractSudoku {
  /**
   * Builds a new Instance. Characters between `'1'` and `'9'` are interpreted as digits.
   * Whitespaces are ignored. Any other character represents an empty slot.
   *
   * @throws IllegalArgumentException if the length of the input (whitespace ignored) is not 81,
   *                                  or if the values would produce an inconsistent grid.
   */
  public static SimpleSudoku create(CharSequence str) {
    return new SimpleSudoku(parse(str));
  }

  private SimpleSudoku(int[] valuesArray) {
    super(valuesArray);
  }

  SimpleSudoku updated(int index, int value) {
    int[] newValues = valuesArray.clone();
    newValues[index] = value;
    return new SimpleSudoku(newValues);
  }

  BitSet possibles(int index) {
    BitSet possibles = new BitSet(10);
    possibles.set(1, 10); // 1 to 9
    for (int i = 0; i < 81; i++)
      if (valuesArray[i] > 0 && i != index && conflict(i, index))
        possibles.clear(valuesArray[i]);
    return possibles;
  }

  int findBestEmpty() {
    int minIndex = -1;
    int minSize = Integer.MAX_VALUE;
    for (int i = 0; i < 81; i++) {
      if (valuesArray[i] == 0) {
        int s = possibles(i).cardinality();
        if (s == 0) return i;
        if (s < minSize) {
          minIndex = i;
          minSize  = s;
        }
      }
    }
    return minIndex;
  }

  public Optional<SimpleSudoku> solve() {
    int index = findBestEmpty();
    if (index < 0) return Optional.of(this);
    BitSet possibles = possibles(index);
    for (int p = possibles.nextSetBit(0); p >= 0; p = possibles.nextSetBit(p + 1)) {
      var sol = updated(index, p).solve();
      if (sol.isPresent()) return sol;
    }
    return Optional.empty();
  }

  private List<SimpleSudoku> allSolutions(List<SimpleSudoku> solutions) {
    int index = findBestEmpty();
    if (index == -1) solutions.add(this);
    else {
      BitSet possibles = possibles(index);
      for (int p = possibles.nextSetBit(0); p >= 0; p = possibles.nextSetBit(p + 1))
           updated(index, p).allSolutions(solutions);
    }
    return solutions;
  }

  public Set<SimpleSudoku> allSolutions() {
    return new HashSet<>(allSolutions(new ArrayList<>()));
  }

  private List<SimpleSudoku> firstSolutions(List<SimpleSudoku> solutions) {
    int index = findBestEmpty();
    if (index == -1) solutions.add(this);
    else {
      BitSet possibles = possibles(index);
      for (int p = possibles.nextSetBit(0); p >= 0; p = possibles.nextSetBit(p + 1)) {
        updated(index, p).firstSolutions(solutions);
        if (solutions.size() > 1) return solutions;
      }
    }
    return solutions;
  }

  public boolean solutionIsUnique() {
    return firstSolutions(new ArrayList<>()).size() == 1;
  }

  // DON'T DO THIS!
  Optional<SimpleSudoku> solve_bad() {
    for (SimpleSudoku solution : allSolutions())
      return Optional.of(solution);
    return Optional.empty();
  }

  // DON'T DO THIS!
  boolean solutionIsUnique_bad() {
    return allSolutions().size() == 1;
  }
}
