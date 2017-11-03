package ScalaTestCode.main

import ScalaTestCode.Constant._
import ScalaTestCode.main.FileSource._

/**
  * Created by hasegawahiroaki on 2017/11/05.
  */
class DSL {


  val file = new FileSource[Seq[String]](config = FileSourceConfig(fileName =
    "/Users/hasegawahiroaki/ScalaJupyter/ScalaToolBox/src/main/scala/ScalaTestCode/test/test.csv",
    delim = CSV)
    , rowSelectFOpt = None)


  val get0: (Fields) => Seq[String] = getColtoParseFunc(0, """<[^>]*>""")
  val get1: (Fields) => Seq[String] = getColtoParseFunc(1, """<[^>]*>""")
  val get2: (Fields) => Seq[String] = getColtoParseFunc(2, """<[^>]*>""")



  val dsl = for {
    res <- file |> get0 :: get1 :: get2 :: Nil

  }
}

yield

