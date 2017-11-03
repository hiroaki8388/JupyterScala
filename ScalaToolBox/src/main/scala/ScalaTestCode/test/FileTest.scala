package ScalaTestCode.test

import ScalaTestCode.Constant.Fields
import ScalaTestCode.main.{CSV, FileSource, FileSourceConfig}
import ScalaTestCode.main.FileSource._
/**
  * Created by hasegawahiroaki on 2017/11/04.
  */
object FileTest extends App{

  /**
    *<>のなかで定義してある記号で囲まれている値を取り出す
    */
  val get: (Fields) => Seq[String] =getColtoParseFunc(0, """<[^>]*>""")
  val s=Seq(("<aaaa><bbbbbbb>"))
// get(s).foreach(a=>a.foreach(println))

 val file=new FileSource[Seq[String]](config=FileSourceConfig(fileName = "/Users/hasegawahiroaki/ScalaJupyter/ScalaToolBox/src/main/scala/ScalaTestCode/test/test.csv",delim=CSV)
    ,rowSelectFOpt = None)

for {
  a<- file.|>(get:: Nil)
  b <- a
  c <- b
} println (c)








}
