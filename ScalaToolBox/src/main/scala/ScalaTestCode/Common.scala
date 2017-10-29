//package ScalaTestCode
//
//import java.io.{File, IOException}
//
//import ScalaTestCode.Constant.{Fields, Header, Order, Table}
//import org.apache.poi.hssf.usermodel.HSSFWorkbook
//import org.apache.poi.sl.usermodel.Sheet
//import org.apache.poi.ss.usermodel.{Sheet, Workbook, WorkbookFactory}
//
//import scala.util.Try
//
///**
//  * Created by hiroaki on 2017/10/24.
//  */
//
//
//trait Config
//
//case class CsvFileSourceConfig(pathName:String,
//                               headerLine:Int=1) extends Config
//
//case object ConfigNone extends Config
//
//
//
//abstract class Transform[T, A](config: Config) {
//  // T:インプットする特徴量のデータ,A:アウトプットされるデータ
//  self =>
//  def |> : PartialFunction[T, Try[A]] // データのバリデーションを行うメソッド
//
//  def map[B](f: A => B): Transform[T, B] = new Transform[T, B](config) {
//    override def |> = new PartialFunction[T, Try[B]] {
//      override def isDefinedAt(t: T) =
//        self.|>.isDefinedAt(t)
//
//      override def apply(t: T): Try[B] = self.|>(t).map(f)
//    }
//  }
//
//  def flatMap[B](f: A => Transform[T, B]): Transform[T, B] = new Transform[T, B](config) {
//    override def |> = new PartialFunction[T, Try[B]] {
//      override def isDefinedAt(t: T) =
//        self.|>.isDefinedAt(t)
//
//      override def apply(t: T): Try[B] = self.|>(t).flatMap(f(_).|>(t))
//    }
//  }
//
//  def andThen[B](tr: Transform[A, B]): Transform[T, B] = new Transform[T, B](config) {
//    override def |> = new PartialFunction[T, Try[B]] {
//      override def isDefinedAt(t: T) =
//        self.|>.isDefinedAt(t) && tr.|>.isDefinedAt(self.|>(t).get)
//
//      override def apply(t: T) = tr.|>(self.|>(t).get)
//    }
//  }
//}
//
//trait FileSource {
//
// protected def load:Try[(Header,Table)]
//}
//
//class CsvFileSource(config: CsvFileSourceConfig, srcFilter: Option[Fields => Boolean] = None)
//extends Transform[List[Fields=>String],Map[String,String]](config) with FileSource{
//
//
//  override protected def load: Try[(Header, Table)] = ???
//
//  override def |> : PartialFunction[List[(Fields) => Order], Try[Process]] = {
//
//  }
//}
//
//object CsvFileSource {
//
//}
//
//  sealed trait Process{ val value:Int,val orders:Seq[Order]}
//
//  // 事前準備
//  case class PreOperation(value:Int =0,orders: Seq[Order]) extends Process
//
//  // チェックする要素
//  case class CheckPoint(value:Int =1,orders :Seq[Order]) extends Process
//
//  // 事後処理
//  case class PostOperation(value:Int =2,orders:Seq[Order]) extends Process
//
//
//
//
//
//
//
//
