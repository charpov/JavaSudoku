import Resources.*
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.tagobjects.Slow
import sudoku.Sudoku
import tinyscalautils.control.interruptibly
import tinyscalautils.test.assertions.assertExpr
import tinyscalautils.text.cleanCRLF
import tinyscalautils.util.FastRandom

import scala.annotation.tailrec
import scala.language.implicitConversions

open class SudokuTests[S: Sudoku] extends AnyFunSuite with Checking:
   protected val ev = Sudoku[S]
   import ev.*

   test("equality"):
      val p = parse(p1)
      assert(p.## == parse(p1).##)
      assert(p == parse(p1))
      assert(p != parse(p2))

   test("parsing (1)"):
      assert(parse("    " + p1 + "\n\n\n\n") === p1)

   test("parsing (2)"):
      assert(parse(p1.replace(".", " X ")) === p1)

   test("parsing (3)"):
      for str <- Seq(p1.tail, p1 + "0", p1.updated(0, '7')) do
         assertThrows[IllegalArgumentException](parse(str))

   test("toString on p1"):
      assertExpr(parse(p1).toString):
         """|9 . .    . 8 3    . 1 .
            |2 . .    . 6 .    . . .
            |. 7 .    . . .    4 . .
            |
            |. . 7    . . 6    . 5 .
            |. . 1    . 4 .    3 . .
            |. 6 .    9 . .    7 . .
            |
            |. . 9    . . .    . 8 .
            |. . .    . 1 .    . . 4
            |. 4 .    5 2 .    . . 6""".stripMargin.cleanCRLF

   test("toString on p2"):
      assertExpr(parse(p2).toString):
         """|. . 8    1 . .    7 . .
            |2 4 7    . . 8    . . .
            |. . .    6 . .    3 . 8
            |
            |. . .    . . 2    5 . .
            |. . .    . 3 .    . . .
            |. . 2    7 . .    . . .
            |
            |9 . 6    . . 7    . . .
            |. . .    9 . .    2 1 6
            |. . 5    . . 1    4 . .""".stripMargin.cleanCRLF

   test("toString on s2"):
      assertExpr(parse(s2).toString):
         """|6 3 8    1 2 5    7 9 4
            |2 4 7    3 9 8    6 5 1
            |1 5 9    6 7 4    3 2 8
            |
            |8 9 3    4 1 2    5 6 7
            |4 7 1    5 3 6    9 8 2
            |5 6 2    7 8 9    1 4 3
            |
            |9 1 6    2 4 7    8 3 5
            |7 8 4    9 5 3    2 1 6
            |3 2 5    8 6 1    4 7 9""".stripMargin.cleanCRLF

   test("values p1"):
      assert(p1.values(19) == 7)
      assert(p1.values(12) == 0)

   test("invalid set"):
      assertThrows[IllegalArgumentException](p1.set(81, 1))
      assertThrows[IllegalArgumentException](p1.set(-1, 1))
      assertThrows[IllegalArgumentException](p1.set(2, 10))
      assertThrows[IllegalArgumentException](p1.set(2, 7))
      assertThrows[IllegalArgumentException](p1.set(2, 0))
      assertThrows[IllegalArgumentException](p1.set(0, 9))

   test("set on p2"):
      val s = p2.set(19, 5)
      assert(s.values(19) == 5)
      assert(p2.values(19) == 0)

   test("filledCount on p1"):
      assert(p1.filledCount == 25)

   test("filledCount on s1"):
      assert(s1.filledCount == 81)

   test("invalid possibles"):
      assertThrows[IllegalArgumentException](p1.possibles(81))
      assertThrows[IllegalArgumentException](p1.possibles(-1))

   test("possibles on p1 (1)"):
      assert(p1.possibles(1) == Set(5))
      assert(p1.possibles(2) == Set(4, 5, 6))
      assert(p1.set(2, 5).possibles(1).isEmpty)

   test("best empty on p1"):
      assert(p1.findBestEmpty.exists(_._2.size == 1))
      assert(p1.set(2, 5).findBestEmpty.exists(_._2.isEmpty))

   test("best empty on s1"):
      assert(s1.findBestEmpty.isEmpty)

   test("solve (1)"):
      assert(p1.solve === s1)

   test("solve (2)"):
      assert(p2.solve === s2)

   test("allSolutions on p1"):
      assert(p1.allSolutions.toSeq == p1.solve.toSeq)

   test("allSolutions on p2"):
      assert(p2.allSolutions.toSeq == p2.solve.toSeq)

   test("allSolutions on p3"):
      val sols = p3.allSolutions.toSet
      assert(sols.size == 22)
      assert(sols.contains(p3.solve.get))
      for sol <- sols do assert(sol.isSolutionOf(p3))

   test("solutionIsUnique on p1"):
      assert(p1.solutionIsUnique)

   test("solutionIsUnique on p3"):
      assert(!p3.solutionIsUnique)

   test("empty grid"):
      assert(!blank.isComplete)
      assert(blank.solve.nonEmpty)
      assert(!blank.solutionIsUnique)

   test("updated on p2"):
      val s1 = p2.set(49, 5)
      assert(s1.values(49) == 5)
      assert(p2.values(49) == 0)

   test("best empty on p2"):
      assert(p2.findBestEmpty.exists((i, s) => s.size == 1 && p2.possibles(i) == s))

   test("filledCount on p2"):
      assert(p2.filledCount == 25)

   test("filledCount on empty grid"):
      assert(blank.filledCount == 0)

   test("isComplete on p2 and s2"):
      assert(!p2.isComplete)
      assert(s2.isComplete)

   test("possibles on p1"):
      assert(p1.possibles(1) == Set(5))

   for p <- Seq(p1, p2, p3, blank) do
      test(s"best empty on $p"):
         @tailrec def loop(sud: S): Unit = sud.findBestEmpty match
            case Some((i, s)) if s.nonEmpty =>
               assert(sud.values(i) == 0 && sud.possibles(i) == s)
               for j <- sud.values.indices if sud.values(j) == 0 do
                  assert(sud.possibles(j).size >= s.size)
               loop(sud.set(i, s.head))
            case _ => ()

         loop(p2)

   test("solve"):
      assert(p3.solve.exists(_.isComplete))

   test("allSolutions on p1 is s1"):
      val iterator = p1.allSolutions
      assert(iterator.next() === s1)
      assert(iterator.isEmpty)

   test("allSolutions on p2 is s2"):
      val iterator = p2.allSolutions
      assert(iterator.next() === s2)
      assert(iterator.isEmpty)

   test("solutionIsUnique on p2"):
      assert(p2.solutionIsUnique)

   test("solutionIsUnique on mostly empty grid"):
      val i = FastRandom.nextInt(81)
      assert(!blank.updated(i, 1).solutionIsUnique)

   for (seq, n) <- Seq(
        nonUniqueProblems.view.take(13),
        nonUniqueProblems.view.slice(13, 23),
        nonUniqueProblems.view.drop(23)
      ).zipWithIndex
   do
      test(s"non-unique problems (${n + 1})", Slow):
         for (p, s) <- seq do
            val sudoku = parse(p)
            assert(!sudoku.solutionIsUnique)
            val all = sudoku.allSolutions.toSet
            assert(all.size == s)
            assert(all.contains(sudoku.solve.get))

   test("challenge 1: 100 easy problems [2pts]", Slow):
      for (p, s) <- allProblems.slice(10000, 10100) do interruptibly(assert(p.solve === s))

   test("challenge 2: 100 more difficult problems [2pts]", Slow):
      for (p, s) <- allProblems.slice(2000, 2100) do interruptibly(assert(p.solve === s))

   test("challenge 3: 5 very difficult problems [2pts]", Slow):
      for (p, s) <- allProblems.slice(100, 105) do interruptibly(assert(p.solve === s))

   test("challenge 4: 1 most difficult problem", Slow):
      val (p, s) = allProblems(2)
      assert(p.solve === s)

   test("challenge 5: 1 most difficult problem", Slow):
      val (p, s) = allProblems(1)
      assert(p.solve === s)

   test("challenge 6: 1 most difficult problem", Slow):
      val (p, s) = allProblems(0)
      assert(p.solve === s)

   test("challenge 7: all remaining problems", Slow):
      for (p, s) <- allProblems do interruptibly(assert(p.solve === s))
end SudokuTests
