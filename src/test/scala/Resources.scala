import tinyscalautils.collection.allDistinct
import tinyscalautils.io.{ InputStreamIsInput, findResourceAsStream, readAll }

import java.net.URI

object Resources:
   def blank = "................................................................................."

   def p1 = "9...83.1.2...6.....7....4....7..6.5...1.4.3...6.9..7....9....8.....1...4.4.52...6"
   def s1 = "954283617213467895876195423497836251581742369362951748139674582625318974748529136"

   def p2 = "008100700247008000000600308000002500000030000002700000906007000000900216005001400"
   def s2 = "638125794247398651159674328893412567471536982562789143916247835784953216325861479"

   def p3 = "000050000026000107040000020000030008000007601007901402604093000000005000809000004"

   private val remoteResources = URI.create("https://cs.unh.edu/~cs761/sudoku/")

   /* Lines in a file are as follows:
    * index sudoku filled_count solution
    * index and filled_count are ignored here
    */
   lazy val allProblems: IndexedSeq[(String, String)] =
      def parse(line: String) =
         line.trim.split(' ') match
            case Array(_, p, _, s) => Some((p, s))
            case _                 => None

      readAll(IndexedSeq)(this.findResourceAsStream(remoteResources)("/sudokus.txt"), parse)
         .ensuring(allDistinct)
   end allProblems

   lazy val nonUniqueProblems: IndexedSeq[(String, Int)] =
      def parse(line: String) =
         line.trim.split(' ') match
            case Array(s, p) => s.toIntOption.map(n => p -> n)
            case _           => None

      readAll(IndexedSeq)(this.findResourceAsStream(remoteResources)("/non-unique.txt"), parse)
         .ensuring(allDistinct)
   end nonUniqueProblems
end Resources
