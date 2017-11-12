package ScalaTestCode.main

import ScalaTestCode.Constant._

import scala.io.Source
import scala.util.Try

/**
  * Created by hasegawahiroaki on 2017/11/03.
  */


/**
  * ファイルの型を定義
  */
sealed abstract class DELIM(val value: String)

case object CSV extends DELIM(",")

case object TSV extends DELIM("\t")

/**
  * ファイルパス、ファイルの種類.ヘッダー位置を設定情報として読みこむ
  */
case class FileSourceConfig(fileName: String, delim: DELIM,
                            headerLine: Int = 0) extends Config


/**
  * srcFilter: 特定の行を除去するフィルター
  */
class FileSource[A](config: FileSourceConfig, rowSelectFOpt: Option[Fields => Boolean] = None)
  extends Transform[List[Fields => A], Seq[A]](config) {


  /**
    * ヘッダーと、ファイルの内容(Table)をもつ
    */
  private lazy val loadedData: Try[(Header, Table)] = Try {
    // ファイルの読み込み
    val file = Source.fromFile(config.fileName)

    val (header, rawData) = file.getLines().map(_.split(config.delim.value).toSeq).toSeq
    match {
      case data => (data(config.headerLine), data)
    }

    /**
      * 条件に引っかかった行は除去する
      */
    val resultData: Seq[Seq[String]] = rowSelectFOpt.fold(rawData)(f => rawData.filter(f))
    //    file.close()


    (header, resultData)
  }

  /**
    * 列毎に異なる処理で整形する
    */
  override def |> : PartialFunction[List[(Fields) => A], Try[Seq[A]]] = {
    case prepro: List[Fields => A] if (prepro.nonEmpty) =>
      loadedData.map { data =>
        val convert =
          (f: Fields => A) => data._2.map(f)

        prepro.flatMap(convert)
      }
  }

}

object FileSource {

  // n番目のカラムをDoubleに変換して取得する関数
  def getColtoDoubleFunc(coln: Int): Fields => Double
  = (fields: Fields) => fields(coln).toDouble

  // TODO Parserモナドを組み立てる
  // n 番目のカラムにたいして、正規表現にマッチした文字列をとりだす
  def getColtoParseFunc(coln: Int, regStr: String): (Fields) => Seq[String] = {
    val reg = regStr.r
    (fields: Fields) =>
      for {
        filtered <- reg.findAllMatchIn(fields(coln)).toSeq
      } yield filtered.toString
  }

}




