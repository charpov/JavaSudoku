import org.scalatest.funsuite.AnyFunSuite
import sudoku.util.FlatMap.flatMap
import sudoku.util.Memoizer
import tinyscalautils.text.StringLetters.*

import java.util

class MemoizerTests extends AnyFunSuite:
   test("memoization"):
      val m      = Memoizer(util.List.of(A, B, C, D).iterator())
      val i1, i2 = m.iterator()
      assert(i1.next() == A)
      assert(i1.next() == B)
      assert(i2.next() == A)
      assert(i2.next() == B)
      assert(i2.next() == C)
      assert(i2.next() == D)
      assert(!i2.hasNext)
      assert(i1.next() == C)
      assert(i1.next() == D)
      assert(!i1.hasNext)

   test("memoization + flatMap"):
      val m = Memoizer(flatMap(util.List.of(1, 2, 3).iterator(), n => util.List.of(n, n, n).iterator()))
      val i1, i2 = m.iterator()
      assert(i1.next() == 1)
      assert(i2.next() == 1)
      assert(i1.next() == 1)
      assert(i1.next() == 1)
      assert(i1.next() == 2)
      assert(i2.next() == 1)
      assert(i2.next() == 1)
      assert(i2.next() == 2)
      assert(i2.next() == 2)
      assert(i2.next() == 2)
      assert(i1.next() == 2)
      assert(i1.next() == 2)
      assert(i1.next() == 3)
      assert(i2.next() == 3)
      assert(i2.next() == 3)
      assert(i2.next() == 3)
      assert(i1.next() == 3)
      assert(i1.next() == 3)
      assert(!i1.hasNext)
      assert(!i2.hasNext)
end MemoizerTests
