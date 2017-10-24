package ScalaTestCode

import ScalaForMachineLeaning.Constant.{CSV_DELM, Fields}
import ScalaForMachineLeaning.MinMax

import scala.io.Source
import scala.reflect.ClassTag
import scala.util.Try

/**
  * Created by hiroaki on 2017/10/24.
  */


trait Config

case class ConfigInt(iParam: Int) extends Config

case class ConfigDouble(fParam: Double) extends Config

case class ConfigArrayDouble(fParams: Array[Double]) extends Config

case class DataSourceConfig(
                             pathName: String,
                             normalize: Boolean,
                             reverseOrder: Boolean,
                             headerLines: Int = 1) extends Config

case class ExcelFileSourceConfig(pageOpt:Option[Int] =None) extends Config


case object ConfigNone extends Config



abstract class Transform[T, A](config: Config) {
  // T:インプットする特徴量のデータ,A:アウトプットされるデータ
  self =>
  def |> : PartialFunction[T, Try[A]] // データのバリデーションを行うメソッド

  def map[B](f: A => B): Transform[T, B] = new Transform[T, B](config) {
    override def |> = new PartialFunction[T, Try[B]] {
      override def isDefinedAt(t: T) =
        self.|>.isDefinedAt(t)

      override def apply(t: T): Try[B] = self.|>(t).map(f)
    }
  }

  def flatMap[B](f: A => Transform[T, B]): Transform[T, B] = new Transform[T, B](config) {
    override def |> = new PartialFunction[T, Try[B]] {
      override def isDefinedAt(t: T) =
        self.|>.isDefinedAt(t)

      override def apply(t: T): Try[B] = self.|>(t).flatMap(f(_).|>(t))
    }
  }

  def andThen[B](tr: Transform[A, B]): Transform[T, B] = new Transform[T, B](config) {
    override def |> = new PartialFunction[T, Try[B]] {
      override def isDefinedAt(t: T) =
        self.|>.isDefinedAt(t) && tr.|>.isDefinedAt(self.|>(t).get)

      override def apply(t: T) = tr.|>(self.|>(t).get)
    }
  }
}


case class ExcelFileSource(config: DataSourceConfig, srcFilter: Option[Fields => Boolean] = None)
extends Transform

case class DataSource(config: DataSourceConfig, srcFilter: Option[Fields => Boolean] = None)
  extends Transform[List[Fields => Double], Vector[Array[Double]]](config) {


  def loadConvert[T: ClassTag](implicit c: String => T): Try[List[Array[T]]] = Try {
    val src = Source.fromFile(config.pathName)
    val fields = src.getLines().map(_.split(CSV_DELM).map(c(_))).toList
    src.close

    fields
  }

  private def load: Try[(Fields, Array[Fields])] = Try {
    val src = Source.fromFile(config.pathName)
    val (head, rawFields) = src.getLines().map(_.split(CSV_DELM)).toArray match {
      case x => (x(config.headerLines), x)
    }

    val (fields) = if (srcFilter.isDefined) rawFields.filter(srcFilter.get) else rawFields
    val results = if (config.reverseOrder) fields.reverse else fields

    val textFields = (head, results)
    src.close()

    textFields
  }

  override def |> : PartialFunction[List[(Fields) => Double], Try[Vector[Array[Double]]]] = {
    case fields: List[(Fields) => Double] if fields.nonEmpty => load.map(data => {
      val convert: ((Fields) => Double) => Array[Double] = (f: Fields => Double) => data._2.map(f) // 行毎にfにそった変換（抽出）処理を行う

      if (config.normalize)
        fields.map(t => new MinMax[Double](convert(t)).normalize(0.0, 1.0)).toVector
      else
        fields.map(convert).toVector
    })
  }

}
