import org.scalactic.Equality
import org.scalatest.Assertions
import sudoku.Sudoku
import tinyscalautils.assertions.implies

import scala.compiletime.asMatchable

trait Checking extends Assertions:
   given [S: Sudoku] => Equality[S]:
      def areEqual(a: S, b: Any): Boolean =
         b.asMatchable match
            case s: String => a.values.mkString == s.replaceAll("""\D""", "0")
            case _         => a == b

   given [S: Sudoku] => Equality[Option[S]]:
      def areEqual(a: Option[S], b: Any): Boolean = a.nonEmpty && a.get === b

   given [S: Sudoku] => Conversion[String, S] = Sudoku[S].parse

   extension [A: Sudoku](sud: A)
      def isSolutionOf[B: Sudoku](other: B): Boolean =
         sud.values.zip(other.values).forall((x, y) => x != 0 && (y != 0 implies x == y))

      def isSolutionOf(other: String): Boolean = isSolutionOf(Sudoku[A].parse(other))
