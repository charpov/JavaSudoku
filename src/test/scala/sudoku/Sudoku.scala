package sudoku

/** Sudoku type class (to better handle Java implementations). */
trait Sudoku[S]:
   def parse(str: String): S
   def valuesOf(sud: S): IndexedSeq[Int]
   def updatedOf(sud: S, index: Int, value: Int): S
   def possiblesOf(sud: S, index: Int): Set[Int]
   def findBestEmptyOf(sud: S): Option[(Int, Set[Int])]
   def solveOf(sud: S): Option[S]
   def allSolutionsOf(sud: S): Iterator[S]
   def solutionIsUniqueOf(sud: S): Boolean

   extension (sud: S)
      /** Grid values, as a 1-dimensional array. Empty squares are represented as 0. */
      def values: IndexedSeq[Int] = valuesOf(sud)

      /** True if the grid has no empty spot. */
      def isComplete: Boolean = !values.contains(0)

      /** The number of filled values in the grid. */
      def filledCount: Int = values.count(_ != 0)

      /** Sets an empty grid location with a number between 1 and 9.
        *
        * @throws IllegalArgumentException
        *   if the index is not valid, the location is not empty, the value is not between 1 and 9,
        *   or the resulting grid would become inconsistent.
        */
      def set(index: Int, value: Int): S =
         require(value != 0)
         updatedOf(sud, index, value)

      /** Possible values for a location. This is the set of numbers that can go in a location
        * without conflicts with the rest of the grid. This is independent of the current number at
        * that location, if any. If a possible set is empty, a puzzle has no solution. On a solved
        * grid, possible sets are singletons, i.e., `possibles(i)` is `{ values(i) }`.
        *
        * @throws IllegalArgumentException
        *   if the index is not valid
        */
      def possibles(index: Int): Set[Int] = possiblesOf(sud, index)

      /** Clears a non-empty grid location.
        *
        * @throws IllegalArgumentException
        *   if the index is not valid or the locatuion is already empty.
        */
      def cleared(index: Int): S = updatedOf(sud, index, 0)

      /** An empty grid location alongside the set of possible values for that location. Returns
        * `None` if the grid is full. The set is guaranteed to have minimal size, i.e., no other
        * location has a smaller set of possible values.
        */
      def findBestEmpty: Option[(Int, Set[Int])] = findBestEmptyOf(sud)

      /** Finds a solution. This method is guaranteed to return a solution if there is one. It
        * returns `None` on puzzles with no solution.
        */
      def solve: Option[S] = solveOf(sud)

      /** All the solutions. This method finds all the solutions to a problem. It is in general
        * slower than `solve`.
        */
      def allSolutions: Iterator[S] = allSolutionsOf(sud)

      /** Tells if a solution exists and is unique. It is in general slower than `solve` but faster
        * than `solveAll` because it calculates at most 2 solutions.
        */
      def solutionIsUnique: Boolean = solutionIsUniqueOf(sud)
end Sudoku

object Sudoku:
   def apply[S: Sudoku]: Sudoku[S] = summon

   /** Equality. */
   // defining a === extension conflicts too much with Scalatest
   def equals[A: Sudoku, B: Sudoku](x: A, y: B ): Boolean =
      val fast = x.isInstanceOf[AnyRef] && y.isInstanceOf[AnyRef]
         && (x.asInstanceOf[AnyRef] eq y.asInstanceOf[AnyRef])
      fast || x.values == y.values
end Sudoku
