package eo.parser


//import com.github.tarao.nonempty.collection.NonEmpty
import eo.core.ast.astparams.EOExprOnly
import eo.core.ast._
//import higherkindness.droste.data.Fix
import org.scalatest.Inspectors.forAll
import org.scalatest.funspec.AnyFunSpec

import scala.reflect.ClassTag
import java.io.File

import eo.parser.ASTs._

class ParserTests extends AnyFunSpec {

  type ParserResult = Either[CompilationError, EOProg[EOExprOnly]]

  private def produces[A <: CompilationError : ClassTag](result: ParserResult): Boolean = {
    result match {
      case Left(_: A) => true
      case _ => false
    }
  }

  private def assertCodeProducesAST(code: String, ast: Vector[EOBnd[EOExprOnly]]) = {
    assert(Parser(code) == Right(EOProg(EOMetas(None, Vector()), ast)))
  }


  private def readCodeFrom(fileName: String): String = {
    val code = io.Source.fromFile(fileName)
    try code.mkString finally code.close()
  }

  private def getListOfFiles(dir: String): List[String] = {
    val file = new File(dir)
    file.listFiles().map(_.getPath).toList
  }


  describe("Parser") {
    describe("produces correct AST for correct programs") {
      it("mutual recursion example") {
        assert(Parser(MutualRecExample.code) == Right(MutualRecExample.ast))
      }

      it("single line application examples") {
        assertCodeProducesAST(
          code = SingleLine1.code,
          ast = SingleLine1.ast
        )
        assertCodeProducesAST(
          code = SingleLine2.code,
          ast = SingleLine2.ast
        )
        assertCodeProducesAST(
          code = SingleLine3.code,
          ast = SingleLine3.ast
        )
        assertCodeProducesAST(
          code = SingleLine4.code,
          ast = SingleLine4.ast
        )
        assertCodeProducesAST(
          code = SingleLine5.code,
          ast = SingleLine5.ast
        )
      }

      forAll(getListOfFiles("core/src/test/resources/eo")) {
        (src: String) =>
          it(s"$src") {
            val ast = Parser(readCodeFrom(src))
            assert(ast.isRight)
          }
      }
    }


    describe("produces errors for incorrect programs") {

      it("misplaced exclamation marks") {
        assert(
          produces[ParserError](
            Parser(FailingCode.misplacedExclamationMark)
          )
        )
      }

      it("invalid tokens") {
        assert(
          produces[LexerError](
            Parser(FailingCode.invalidTokens)
          )
        )
      }
    }
  }

  describe("produces") {
    it("should return true if there is an error") {
      assert(produces[ParserError](Left(ParserError(""))))
      assert(produces[LexerError](Left(LexerError(""))))
    }

    it("should return false if the error is different") {
      assert(!produces[LexerError](Left(ParserError(""))))
      assert(!produces[ParserError](Left(LexerError(""))))
    }

    it("should return false if there is no error") {
      assert(!produces[LexerError](Right(EOProg(EOMetas(None, Vector()), Vector()))))
      assert(!produces[ParserError](Right(EOProg(EOMetas(None, Vector()), Vector()))))
    }
  }

}
