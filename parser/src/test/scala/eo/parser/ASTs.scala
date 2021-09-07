package eo.parser

import com.github.tarao.nonempty.collection.NonEmpty
import eo.core.ast.{EOAliasMeta, EOAnonExpr, EOAnyName, EOBnd, EOBndExpr, EOCopy, EODecoration, EODot, EOExpr, EOMetas, EOObj, EOProg, EOSimpleApp, LazyName}
import eo.core.ast.astparams.EOExprOnly
import higherkindness.droste.data.Fix

object ASTs {

  object MutualRecExample {
    val ast: EOProg[EOExprOnly] = EOProg(
      EOMetas(
        pack = Some("sandbox"),
        metas = Vector(
          EOAliasMeta("stdout", "org.eolang.io.stdout"),
          EOAliasMeta("sprintf", "org.eolang.txt.sprintf"),
        )
      ),
      Vector(
        EOBndExpr(
          EOAnyName(LazyName("base")),
          Fix[EOExpr](
            EOObj(
              freeAttrs = Vector(),
              varargAttr = None,
              bndAttrs = Vector(
                EOBndExpr(
                  EOAnyName(LazyName("x")),
                  Fix[EOExpr](EOSimpleApp("memory"))
                ),
                EOBndExpr(
                  EOAnyName(LazyName("f")),
                  Fix[EOExpr](
                    EOObj(
                      freeAttrs = Vector(LazyName("self"), LazyName("v")),
                      varargAttr = None,
                      bndAttrs = Vector(
                        EOBndExpr(
                          EODecoration(),
                          Fix[EOExpr](
                            EOCopy(
                              Fix[EOExpr](EODot(Fix[EOExpr](EOSimpleApp("x")), "write")),
                              NonEmpty[Vector[EOBnd[EOExprOnly]]](
                                EOAnonExpr(Fix[EOExpr](EOSimpleApp("v")))
                              )
                            )
                          )
                        )
                      )
                    )
                  )
                ),
                EOBndExpr(
                  EOAnyName(LazyName("g")),
                  Fix[EOExpr](
                    EOObj(
                      freeAttrs = Vector(LazyName("self"), LazyName("v")),
                      varargAttr = None,
                      bndAttrs = Vector(
                        EOBndExpr(
                          EODecoration(),
                          Fix[EOExpr](
                            EOCopy(
                              Fix[EOExpr](EODot(Fix[EOExpr](EOSimpleApp("self")), "f")),
                              NonEmpty[Vector[EOBnd[EOExprOnly]]](
                                EOAnonExpr(Fix[EOExpr](EOSimpleApp("self"))),
                                EOAnonExpr(Fix[EOExpr](EOSimpleApp("v")))
                              )
                            )
                          )
                        )
                      )
                    )
                  )
                )
              )
            )
          )
        ),


        EOBndExpr(
          EOAnyName(LazyName("derived")),
          Fix[EOExpr](
            EOObj(
              freeAttrs = Vector(),
              varargAttr = None,
              bndAttrs = Vector(
                EOBndExpr(EODecoration(), Fix[EOExpr](EOSimpleApp("base"))),
                EOBndExpr(
                  EOAnyName(LazyName("f")),
                  Fix[EOExpr](
                    EOObj(
                      freeAttrs = Vector(LazyName("self"), LazyName("v")),
                      varargAttr = None,
                      bndAttrs = Vector(
                        EOBndExpr(
                          EODecoration(),
                          Fix[EOExpr](
                            EOCopy(
                              Fix[EOExpr](EODot(Fix[EOExpr](EOSimpleApp("self")), "g")),
                              NonEmpty[Vector[EOBnd[EOExprOnly]]](
                                EOAnonExpr(Fix[EOExpr](EOSimpleApp("self"))),
                                EOAnonExpr(Fix[EOExpr](EOSimpleApp("v")))
                              )
                            )
                          )
                        )
                      ),
                    )
                  )
                )
              )
            )
          )
        ),
      )
    )
    val code: String = readCodeFrom("core/src/test/resources/eo/mutual_rec_example.eo")
  }

  object SingleLine1 {
    val ast = Vector(
      EOAnonExpr(Fix[EOExpr](EOSimpleApp("a")))
    )
    val code: String = readCodeFrom("parser/src/test/scala/resources/single_line1.eo")
  }

  object SingleLine2 {
    val ast = Vector(
      EOBndExpr(
        EOAnyName(LazyName("namedA")),
        Fix[EOExpr](EOSimpleApp("a"))
      )
    )
    val code: String = readCodeFrom("parser/src/test/scala/resources/single_line2.eo")
  }

  object SingleLine3 {
    val ast = Vector(
      EOBndExpr(
        EOAnyName(LazyName("aAppliedToBCandD")),
        Fix[EOExpr](EOCopy(
          Fix[EOExpr](EOSimpleApp("a")),
          NonEmpty[Vector[EOBnd[EOExprOnly]]](
            EOAnonExpr(Fix[EOExpr](EOSimpleApp("b"))),
            EOAnonExpr(Fix[EOExpr](EOSimpleApp("c"))),
            EOAnonExpr(Fix[EOExpr](EOSimpleApp("d")))
          )
        )
        )
      )
    )
    val code: String = readCodeFrom("parser/src/test/scala/resources/single_line3.eo")
  }

  object SingleLine4 {
    val ast = Vector(EOBndExpr(
      EOAnyName(LazyName("rightAssociative")),
      Fix[EOExpr](EOCopy(
        Fix[EOExpr](EOSimpleApp("a")),
        NonEmpty[Vector[EOBnd[EOExprOnly]]](
          EOAnonExpr(Fix[EOExpr](EOCopy(Fix[EOExpr](EOSimpleApp("b")),
            NonEmpty[Vector[EOBnd[EOExprOnly]]](
              EOAnonExpr(Fix[EOExpr](EOCopy(
                Fix[EOExpr](EOSimpleApp("c")),
                NonEmpty[Vector[EOBnd[EOExprOnly]]](
                  EOAnonExpr(Fix[EOExpr](EOSimpleApp("d")))))))))))))))
    )
    val code: String = readCodeFrom("parser/src/test/scala/resources/single_line4.eo")
  }

  object SingleLine5 {
    val ast = Vector(
      EOBndExpr(
        EOAnyName(LazyName("leftAssociative")),
        Fix[EOExpr](EOCopy(
          Fix[EOExpr](EOCopy(
            Fix[EOExpr](EOCopy(
              Fix[EOExpr](EOSimpleApp("a")),
              NonEmpty[Vector[EOBnd[EOExprOnly]]](EOAnonExpr(Fix[EOExpr](EOSimpleApp("b")))))),
            NonEmpty[Vector[EOBnd[EOExprOnly]]](EOAnonExpr(Fix[EOExpr](EOSimpleApp("c")))))),
          NonEmpty[Vector[EOBnd[EOExprOnly]]](EOAnonExpr(Fix[EOExpr](EOSimpleApp("d"))))
        ))
      )
    )
    val code: String = readCodeFrom("parser/src/test/scala/resources/single_line5.eo")
  }

  object FailingCode {
    val misplacedExclamationMark: String =
      """
        |this
        |  is > wrooooong!!!!!!
        |""".stripMargin

    val invalidTokens: String =
      """
        |&~
        |""".stripMargin
  }


  private def readCodeFrom(fileName: String): String = {
    val code = io.Source.fromFile(fileName)
    try code.mkString finally code.close()
  }
}
