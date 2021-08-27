package eo.sandbox

import cats.effect.{ ExitCode, IO, IOApp, Resource }
import cats.implicits._
import eo.analysis.mutualrec.naive.mutualrec.{ findMutualRecursionInTopLevelObjects, resolveMethodsReferencesForEOProgram }
//import eo.backend.eolang.ToEO.instances._
//import eo.backend.eolang.ToEO.ops._
//import eo.backend.eolang.inlineorlines.ops._
import eo.parser.{ LexerError, Parser, ParserError }
//import eo.sandbox.programs.mutualRecursionExample

import scala.io.Source
//import scala.util.chaining._

object Sandbox extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = for {
    exitCode <- IO.pure(ExitCode.Success)
    //fileName = "sandbox/src/main/resources/mutual_rec_example.eo"
    //fileName = "sandbox/src/main/resources/mutual_rec_non_term_long_chain.eo"
    //fileName = "sandbox/src/main/resources/mutual_recursion_non_term_derived_again.eo"
    //fileName = "sandbox/src/main/resources/mutual_recursion_non_term_derived_again.eo"
    fileName = "sandbox/src/main/resources/mutual_rec_false_positive_type_check.eo"
    //fileName = "sandbox/src/main/resources/mutual_rec_non_term_factory.eo"
//    fileName = "sandbox/src/main/resources/mutual_rec_non_term_2_derived_explicit_decoration.eo" // TODO: add this to presentation
    fileSourceResource = Resource.make(IO(Source.fromFile(fileName)))(src => IO(src.close()))
    fileContents <- fileSourceResource.use(src => IO(src.getLines().toVector.mkString("\n")))
    program <- IO.fromEither(Parser(fileContents).leftMap {
      case LexerError(msg) => new IllegalArgumentException(msg)
      case ParserError(msg) => new IllegalArgumentException(msg)
    })
//    programText = program.toEO.allLinesToString
//    _ <- IO(programText.tap(println))

    topLevelObjects <- resolveMethodsReferencesForEOProgram[IO](program)

    mutualRec <- findMutualRecursionInTopLevelObjects(topLevelObjects)
    mutualRecFiltered = mutualRec.filter(_.nonEmpty)

    _ <- IO.delay(println())
    _ <- IO.delay(
      for {
        mutualRecDep <- mutualRecFiltered
        (method, depChains) <- mutualRecDep.toVector
        depChain <- depChains.toVector
      } yield for {
        mutualRecMeth <- depChain.lastOption
      } yield {
        val mutualRecString =
          s"Method `${method.parentObject.objName}.${method.name}` " ++
            s"is mutually recursive with method " ++
            s"`${mutualRecMeth.parentObject.objName}.${mutualRecMeth.name}`"

        val dependencyChainString = depChain.append(method).map(m => s"${m.parentObject.objName}.${m.name}").mkString_(" -> ")

        println(mutualRecString ++ " through the following possible code path:\n" ++ dependencyChainString)
      }
    )
  } yield exitCode
}
