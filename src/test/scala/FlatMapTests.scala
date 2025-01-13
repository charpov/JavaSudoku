import org.scalatest.funsuite.AnyFunSuite
import java.util
import scala.jdk.CollectionConverters.IteratorHasAsJava
import scala.jdk.CollectionConverters.IteratorHasAsScala
import sudoku.util.FlatMap.flatMap

class FlatMapTests extends AnyFunSuite:
   private def single[A](value: A) = util.List.of(value).iterator()

   test("empty iterator"):
      val i = flatMap(util.Collections.emptyIterator(), single)
      assert(!i.hasNext)
      assertThrows[NoSuchElementException](i.next())

   test("simple flatMap"):
      val i = flatMap(util.List.of("A", "BB", "", "CCC").iterator(), _.iterator.asJava)
      assert(i.asScala.sameElements("ABBCCC"))

   test("nested flatMap 1"):
      val a = util.List.of(10, 100, 1000)
      val b = util.List.of(2, 3, 4)
      val i = flatMap(a.iterator(), n => flatMap(b.iterator(), m => single(n + m)))
      assert(i.asScala.sameElements(Seq(12, 13, 14, 102, 103, 104, 1002, 1003, 1004)))

   test("nested flatMap 2"):
      val depth = 1000
      def f(n: Int) = if n == 0 then util.Collections.emptyIterator() else single(n)
      def combine(iterators: List[util.List[Int]]): util.Iterator[Int] =
         iterators.tail.foldLeft(iterators.head.iterator()): (i, l) =>
            flatMap(i, n => flatMap(l.iterator(), m => f(n * m)))

      val a = util.List.of(0, 1, 2, 3, 0)
      val b = util.List.of(0, 0, 1, 0, 0)
      val i = combine(a :: List.fill(depth)(b))
      assert(i.asScala.sameElements(Seq(1, 2, 3)))
end FlatMapTests
