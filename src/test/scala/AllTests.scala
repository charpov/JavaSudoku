import Resources.*
import org.scalatest.Suites
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.tagobjects.Slow
import sudoku.JavaSudoku.given
import sudoku.util.SmallIntSet
import sudoku.{ MemoSudoku, SimpleSudoku, Sudoku }
import tinyscalautils.control.times
import tinyscalautils.util.FastRandom

import java.util.stream.IntStream
import scala.collection.immutable.BitSet
import scala.jdk.CollectionConverters.IteratorHasAsScala
import scala.jdk.StreamConverters.{ IntStreamHasToScala, IterableHasSeqStream }
import scala.language.implicitConversions

class AllTests
    extends Suites(
      JavaSmallIntTests(),
      FlatMapTests(),
      SudokuTests[SimpleSudoku](),
      MemoSudokuTests[MemoSudoku]()
    )

class MemoSudokuTests[S: Sudoku] extends SudokuTests[S]:
   import ev.*

   test("memoization 1"):
      val p = parse(p1)
      10_000 times:
         assert(p.solve === s1)
         assert(p.solutionIsUnique)

   test("memoization 2"):
      val p = parse(p3)
      10_000 times:
         assert(p.allSolutions.size == 22)
end MemoSudokuTests

class JavaSmallIntTests extends AnyFunSuite: // don't polute code with a typeclass (yet)
   extension (set: Int)
      def isEmpty: Boolean  = SmallIntSet.isEmpty(set)
      def nonEmpty: Boolean = SmallIntSet.nonEmpty(set)
      def iterator: Iterator[Int] = SmallIntSet.iterator(set).asScala.map(_.intValue) // can boxing be avoided?
      def contains(x: Int): Boolean  = SmallIntSet.contains(set, x)
      def minus(x: Int): Int         = SmallIntSet.minus(set, x)
      def plus(x: Int): Int          = SmallIntSet.plus(set, x)
      def size: Int                  = SmallIntSet.size(set)
      def min: Int                   = SmallIntSet.min(set)
      def max: Int                   = SmallIntSet.max(set)
      def union(other: Int): Int     = SmallIntSet.union(set, other)
      def intersect(other: Int): Int = SmallIntSet.intersect(set, other)
      def diff(other: Int): Int      = SmallIntSet.diff(set, other)
      def toSet: Set[Int]            = SmallIntSet.toSet(set).stream().toScala(Set)
      def mkString: String           = SmallIntSet.mkString(set)

   def toStream(set: BitSet): IntStream = set.asJavaSeqStream
   def fromRange(range: Range): Int     = SmallIntSet.fromRange(range.min, range.length)

   test("sample"):
      val set1 = SmallIntSet.from(2, 3, 5, 9)
      val set2 = fromRange(6 to 8)
      assert(!set1.isEmpty)
      assert(set2.nonEmpty)
      assert(set1.iterator.sameElements(Seq(2, 3, 5, 9)))
      assert(set1.contains(3))
      assert(set1.size == 4)
      assert(set1.min == 2)
      assert(set1.max == 9)
      assert(set1.union(set2) == fromRange(2 to 9).minus(4))
      assert(set1.intersect(set2).isEmpty)
      assert(set1 == fromRange(2 to 9).minus(4).diff(set2))
      assert(set1.mkString == "2359")

   test("31"):
      val set = SmallIntSet.from(31)
      assert(set.nonEmpty)
      assert(set.size == 1)
      assert(set.contains(31))
      assert(set.min == 31)
      assert(set.max == 31)
      assert(set.toSet == Set(31))
      assert(set == fromRange(31 to 31))
      assert(set.mkString == "31")

   private def check(sets: Seq[BitSet]) =
      for set1 <- sets do
         val s1 = SmallIntSet.from(toStream(set1))
         assert(s1.toSet == set1)
         assert(s1.iterator.sameElements(set1.iterator))
         assert(s1.mkString == set1.mkString)
         assert(s1.size == set1.size)
         assert(s1.isEmpty == set1.isEmpty)
         assert(s1.nonEmpty == set1.nonEmpty)
         if s1.nonEmpty then
            assert(s1.min == set1.min)
            assert(s1.max == set1.max)
            if s1.max - s1.min + 1 == s1.size then
               assert(s1 == SmallIntSet.fromRange(s1.min, s1.max - s1.min + 1))
         for set2 <- sets do
            val s2 = SmallIntSet.from(toStream(set2))
            assert(set1.union(set2) == s1.union(s2).toSet)
            assert(set1.intersect(set2) == s1.intersect(s2).toSet)
            assert(set1.diff(set2) == s1.diff(s2).toSet)
            for x <- set2 do
               assert(s1.plus(x).toSet == set1.incl(x))
               assert(s1.minus(x).toSet == set1.excl(x))
               assert(s1.contains(x) == set1.contains(x))

   test("small sets"):
      val nums = BitSet.fromSpecific(1 to 9)
      check(nums.subsets().toSeq)

   test("more sets", Slow):
      val n    = 1000
      val nums = 0 to 31
      check(Seq.fill(n)(BitSet.fromSpecific(FastRandom.shuffle(nums).take(FastRandom.nextInt(32)))))
end JavaSmallIntTests
