package sudoku

import scala.collection.immutable.{ ArraySeq, BitSet }
import scala.jdk.CollectionConverters.IteratorHasAsScala
import scala.jdk.OptionConverters.RichOptional

object JavaSudoku:
   /** The grid indices, once and for all. */
   private final val grid: Range = 0 until 81

   given MemoSudokuIsSudoku: Sudoku[MemoSudoku]:
      def parse(str: String): MemoSudoku = MemoSudoku.create(str)

      def valuesOf(sud: MemoSudoku): IndexedSeq[Int] = ArraySeq.unsafeWrapArray(sud.valuesArray)

      def updatedOf(sud: MemoSudoku, index: Int, value: Int): MemoSudoku =
         require(grid.contains(index))
         if value == 0 then require(sud.values(index) != 0)
         else require(sud.values(index) == 0 && sud.possibles(index).contains(value))
         sud.updated(index, value)

      def possiblesOf(sud: MemoSudoku, index: Int): Set[Int] =
         require(grid.contains(index))
         BitSet.fromBitMaskNoCopy(Array(sud.possibles()(index) & 0xFFFFFFFFL))

      def findBestEmptyOf(sud: MemoSudoku): Option[(Int, Set[Int])] =
         val i = sud.findBestEmpty
         Option.unless(i < 0)(i -> possiblesOf(sud, i))

      def solveOf(sud: MemoSudoku): Option[MemoSudoku] = sud.solve.toScala

      def allSolutionsOf(sud: MemoSudoku): Iterator[MemoSudoku] = sud.allSolutions.iterator.asScala

      def solutionIsUniqueOf(sud: MemoSudoku): Boolean = sud.solutionIsUnique

   given SimpleSudokuIsSudoku: Sudoku[SimpleSudoku]:
      def parse(str: String): SimpleSudoku = SimpleSudoku.create(str)

      def valuesOf(sud: SimpleSudoku): IndexedSeq[Int] = ArraySeq.unsafeWrapArray(sud.valuesArray)

      def updatedOf(sud: SimpleSudoku, index: Int, value: Int): SimpleSudoku =
         require(grid.contains(index))
         if value == 0 then require(sud.values(index) != 0)
         else require(sud.values(index) == 0 && sud.possibles(index).get(value))
         sud.updated(index, value)

      def possiblesOf(sud: SimpleSudoku, index: Int): Set[Int] =
         require(grid.contains(index))
         val set = sud.possibles(index)
         if set.isEmpty then Set.empty else BitSet.fromBitMaskNoCopy(Array(set.toLongArray()(0)))

      def findBestEmptyOf(sud: SimpleSudoku): Option[(Int, Set[Int])] =
         val i = sud.findBestEmpty
         Option.unless(i < 0)(i -> possiblesOf(sud, i))

      def solveOf(sud: SimpleSudoku): Option[SimpleSudoku] = sud.solve.toScala

      def allSolutionsOf(sud: SimpleSudoku): Iterator[SimpleSudoku] =
         sud.allSolutions.iterator.asScala

      def solutionIsUniqueOf(sud: SimpleSudoku): Boolean = sud.solutionIsUnique()
