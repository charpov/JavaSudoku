import sudoku.JavaSudoku.given
import sudoku.{ MemoSudoku, SimpleSudoku, Sudoku }
import tinyscalautils.text.timeString
import tinyscalautils.timing.timeIt

/** A simple command-line application. It solves the Sudoku problem specified on the command-line
  * and checks if the solution is unique. Uses the simple implementation.
  */
@main def SimpleRun(str: String, more: String*): Unit = run(SimpleSudoku.create(str))

/** A simple command-line application. It solves the Sudoku problem specified on the command-line
 * and checks if the solution is unique. Uses the memoized implementation.
 */
@main def MemoRun(str: String, more: String*): Unit = run(MemoSudoku.create(str))

private def run[S: Sudoku](sudoku: S) =
   printf("%s\n\n", sudoku)
   val (solved, time1) = timeIt(sudoku.solve)
   printf("%s\n\n", solved.map(_.toString).getOrElse("no solution"))
   val (unique, time2) = timeIt(sudoku.solutionIsUnique)
   val (more, time3)   = timeIt(sudoku.allSolutions.take(101).toSeq)
   if unique then
      println("solution is unique")
      assert(more.sameElements(solved))
   else if more.length == 101 then println("there are more then 100 solutions")
   else if more.nonEmpty then printf(s"there are ${more.length} solutions")
   println()
   println(s"solving time:     ${timeString(time1)}")
   println(s"unicity checking: ${timeString(time2)}")
   println(s"more solutions:   ${timeString(time3)}")
