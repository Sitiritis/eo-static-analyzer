package org.polystat.odin.parser.fastparse

import fastparse._
import org.polystat.odin.core.ast.astparams.EOExprOnly
import org.polystat.odin.core.ast._
import IgnoreEmptyLinesOrComments._
import org.polystat.odin.parser.fastparse.Metas.{packageMeta, rtMeta, aliasMeta}


/**
 * Contains the entrypoint for the parser
 *
 * @param indent          the spaces before the statements in the outermost block
 * @param indentationStep InnerBlockIndentation minus OuterBlockIndentation
 */
class Parser(
              override val indent: Int = 0,
              override val indentationStep: Int = 2
            ) extends RespectsIndentation {


  def metas[_: P]: P[EOMetas] = P(
    packageMeta.? ~/
      (rtMeta | aliasMeta)./.rep
  ).map {
    case (pkg, metas) => EOMetas(pkg, metas.toVector)
  }

  def program[_: P]: P[EOProg[EOExprOnly]] = P(
    Start ~/
      metas ~/
      `object`./.rep ~
      End
  ).map {
    case (metas, objs) => EOProg(
      metas = metas,
      bnds = objs.toVector
    )
  }

  def `object`[_: P]: P[EOBnd[EOExprOnly]] = P(
    new NamedObjects(indent = indent, indentationStep = indentationStep).namedObject |
      new AnonymousObjects(indent = indent, indentationStep = indentationStep).anonymousObject
  )

}


object Parser {
  def parse(
             code: String,
             indentationStep: Int = 2
           ): Parsed[EOProg[EOExprOnly]] = {
    fastparse.parse(code, new Parser(indentationStep = indentationStep).program(_))
  }
}