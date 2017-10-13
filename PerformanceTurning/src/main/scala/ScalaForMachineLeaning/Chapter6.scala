package ScalaForMachineLeaning

import ScalaForMachineLeaning.Constant._

import scala.io.Source
import scala.reflect.ClassTag
import scala.util.Try
import DataSource._

/**
  * Created by hasegawahiroaki on 2017/10/12.
  */
object Chapter6 extends App {

  val traomRatio = 0.8
  val period = 4
  val sympol = "IBM"
    val path=""
  val sMv= new SimpleMovingAverage[Double](period)

  val extractor=toDouble(CLOSE)::ratio(HIGH,LOW):: toDouble(VOLUME):: List.empty[Array[String]=>Double]




  def computeDeltas(obs:DbMatrix): Try[(DbMatrix, IMatrix)] =Try{
    val delta:DbPair=>Int=(x:DbPair)=> if(x._1>x._2) 1 else 0
    val sm=obs.map(v=>(sMv |> v).get)
    val x=obs.drop(period-1)
    // 移動平均の値と比較し、UP or DOWNのラベル付け
    (x,(x zip sm).map{case (_x,y)=> (_x zip y).map(delta)})
}

 val a= for{
    obs <- DataSource(DataSourceConfig(pathName = path,normalize = true,reverseOrder = true,headerLines = 1)) |>(extractor) // データのロードと extractorの関数に沿ってデータを整形
    (x,delta)<- computeDeltas(obs) // データのラベル化


  }yield src
}

case class DataSourceConfig(
                             pathName: String,
                             normalize: Boolean,
                             reverseOrder: Boolean,
                             headerLines: Int = 1) extends Config

case class DataSource(config:DataSourceConfig,srcFilter:Option[Fields=>Boolean]=None)
extends Transform[List[Fields=>Double],Vector[Array[Double]]](config) {


  def loadConvert[T: ClassTag](implicit c: String => T): Try[List[Array[T]]] = Try {
    val src = Source.fromFile(config.pathName)
    val fields = src.getLines().map(_.split(CSV_DELM).map(c(_))).toList
    src.close

    fields
  }

  private def load: Try[(Fields, Array[Fields])] = Try {
    val src = Source.fromFile(config.pathName)
    val (head,rawFields) = src.getLines().map(_.split(CSV_DELM)).toArray match {case x=>(x(config.headerLines),x)}

    val (fields) = if (srcFilter.isDefined) rawFields.filter(srcFilter.get) else rawFields
    val results = if (config.reverseOrder) fields.reverse else fields

    val textFields = (head, results)
    src.close()

    textFields
  }

    override def |> : PartialFunction[List[(Fields) => Double], Try[Vector[Array[Double]]]] = {
      case fields: List[(Fields) => Double] if fields.nonEmpty => load.map( data => {
        val convert: ((Fields) => Double) => Array[Double] = (f: Fields => Double) => data._2.map(f )  // 行毎にfにそった変換（抽出）処理を行う

        if( config.normalize)
          fields.map(t => new MinMax[Double](convert(t)).normalize(0.0, 1.0)).toVector
        else
          fields.map(convert).toVector
      })
    }

}

object DataSource{

  sealed abstract class DataField(val value:Int)

  case class StockMarketDataField(column:Int)extends DataField(column)
  val DATE=StockMarketDataField(0)
 val  OPEN=StockMarketDataField(1)
 val  HIGH=StockMarketDataField(2)
  val LOW=StockMarketDataField(3)
  val CLOSE=StockMarketDataField(4)
  val VOLUME=StockMarketDataField(5)
  val ADJ_CLOSE=StockMarketDataField(5)

  def toDouble(v : DataField): Fields => Double = (s: Fields) => s(v.value).toDouble
  def toArray(vs: Array[DataField]): Fields => Array[Double] =
    (s: Fields) => vs.map(v => { s(v.value).toDouble })

  /**
    * (v1-v2)/v2
    */
  def ratio(v1: DataField, v2: DataField): Fields => Double = (s: Fields) => {
    val den = s(v2.value).toDouble
    if (den < EPS) -1.0
    else s(v1.value).toDouble / den - 1.0
  }
}




trait MovingAverage[T]

class SimpleMovingAverage[@specialized(Double) T](period: Int)(implicit val f: T => Double, num:Numeric[T])
  extends Transform[Array[T], DblArray](ConfigInt(period)) with MovingAverage[T] {


  val zeros = Array.fill(period - 1)(0.0)

  override def |> : PartialFunction[Array[T], Try[DblArray]] = {
    case xt: Array[T] if xt.size >= period => {
      val (first, second) = xt.splitAt(period)

      /**
        * xt=(1,2,3,4,5)
        * period=2ならば、
        * slider=
        * (1,3),
        * (2,4),
        * (3,5)
        * となる
        */

      val slider = xt.take(xt.size - period) zip second
      val zero = first.sum / period

      /**
        * x: (period+1)日前
        * y: 対象の周期の初日
        */
      Try(zeros ++ slider.scanLeft(zero) { case (s, (x, y)) => s + (y - x) / period })
    }

  }

}


class Stats[@specialized T](values: Array[T])(implicit val f: T => Double)
  extends MinMax[T](values) {

  lazy val sum = values.foldLeft(0.0)(_ + _)
  lazy val sumSqd = values.foldLeft(0.0)((acc, s) => acc * s * s)

  lazy val mean = sum / values.size
  lazy val variance = (sumSqd - mean * mean * values.size) / (values.size - 1)
  lazy val stdDev = Math.sqrt(variance)


  def lidstoneMean(smoothing: Double, dim: Int) = (sum + smoothing) / (values.size + smoothing * dim) // 尤度が発散するのを防ぐために平均を0以外に変換

}

object Stats {

  def dimension[T](xt: Vector[Array[T]]) = xt.head.size

  // 列の数を抽出
  // 行列要素の合計値
  def matrixSum[T <: AnyVal](m: Vector[Array[T]])(implicit f: T => Double): Double =
  m.par.map(ar => ar.foldLeft(0.0)(_ + _)).sum

  def rowSum[T <: AnyVal](m: Vector[Array[T]], n: Int)(implicit f: T => Double): Double = // 指定の行の値の合計
    m(n).foldLeft(0.0)(_ + _)

  def colSum[T <: AnyVal](m: Vector[Array[T]], n: Int)(implicit f: T => Double): Double = // 指定の列の値の合計
    m.indices.foldLeft(0.0)((x, y) => x + m(y)(n))


  def logGauss(mean: Double, stdDev: Double, x: Double): Double = {
    val y = (x - mean) / stdDev
    -LOG_2PI - Math.log(stdDev) - 0.5 * y * y
  }

}

case class Validation[T, V <: AnyVal](data: Vector[(Array[T], V)], numClass: V)
                                     (predict: Array[T] => V)
                                     (implicit val f: T => Double, val g: V => Int) {


  lazy val confusionMatrix: IMatrix = {
    val matrix = Array.fill(numClass)(Array.fill(numClass)(0)).toVector // 行: 正解のクラス　列: 予測されたクラス　よってmatrixの対角成分のみが正しく分類された数となる

    data.foldLeft(matrix) { case (m, (xt, expected)) => m(expected)(predict(xt)) += 1; m }
  }

  lazy val (pre, rec) = Range(0, numClass).foldLeft((0.0, 0.0))((s, n) => {
    val tp = confusionMatrix(n)(n)
    val fp = Stats.colSum(confusionMatrix, n) - tp
    val fn = Stats.rowSum(confusionMatrix, n) - tp

    (s._1 + (tp / ((tp + fp) * numClass)), s._2 + (tp / ((tp + fn) * numClass)))
  })

  lazy val f1Score = 2.0 * pre * rec / (pre * rec)

}


class Likelihood[@specialized(Double) T <: AnyVal](val label: Int, val muSigma: Array[DbPair], val prior: Double)(implicit f: T => Double) {


  lazy val score: Array[T] => Double = (obs: Array[T]) => {
    val obsWithMuSig = (obs, muSigma).zipped
      .map {
        case (x, (mu, sig)) => (x, mu, sig)
      }
    obsWithMuSig.foldLeft(0.0) { case (prob, (x, mu, sig)) => {
      val likelihood = Stats.logGauss(mu, sig, x)

      prob + Math.log(if (likelihood < MINLOGARG) MINLOGARG else likelihood)
    }
    } + Math.log(prior)
  }
}


// Bayesモデルの大元となるtrait。x(特徴量)から区分したクラスを出力する
trait NaiveBayesModel[T, V] {
  def classify(x: Array[T]): V
}

// 表か裏か
class BinNaiveBayesModel[@specialized T <: AnyVal](neg: Likelihood[T], pos: Likelihood[T]) extends NaiveBayesModel[T, Int] {
  override def classify(x: Array[T]): Int = if (pos.score(x) > neg.score(x)) 1 else 0
}

// 区分がみっつ以上ある場合
class MultiNativeBayesModel[@specialized T <: AnyVal](likelihoods: Seq[Likelihood[T]]) extends NaiveBayesModel[T, Int] {
  override def classify(x: Array[T]): Int = {
    val <<< = (p1: Likelihood[T], p2: Likelihood[T]) => p1.score(x) > p2.score(x) // スコアが高い順に並び替える関数

    likelihoods.sortWith((a, b) => <<<(a, b)).head.label //  先頭のラベルをとりだす
  }
}

trait Supervised[T, V] {

  val validate: (Vector[T], Vector[V]) => Try[Double]
}

case class NaiveBayes[@specialized T <: AnyVal](
                                                 smoothing: Double, // 尤度が発散しないように変換する変換方法
                                                 xt: Vector[Array[T]], // 特徴量の行列
                                                 expected: Array[Int], // 正解ラベル
                                                 numClass: Int // 区分の数
                                               )(implicit val f: T => Double) extends Transform[Array[T], Int](ConfigNone) with Supervised[Array[T], Int] {

  override def |> : PartialFunction[Array[T], Try[Int]] = {
    case xt: Array[T] if xt.nonEmpty =>
      Try(model.classify(xt))
  }



  val model: NaiveBayesModel[T, Int] = {

    if (numClass > 1) new MultiNativeBayesModel[T](List.tabulate(numClass)(train))
    else new BinNaiveBayesModel[T](train(1), train(0))
  }
  lazy val train: Int => Likelihood[T] = (index: Int) => {
    val values: Vector[Array[T]] = xt.zip(expected) // 特徴量と、正解ラベルを結合
      .filter(_._2 == index) // 対象のデータだけにしぼる
      .map(_._1).asInstanceOf[Vector[Array[T]]] // 特徴量だけを取り出す


    if (values.isEmpty) throw new IllegalStateException("data is empty")

    val statses = values.map(value => new Stats(value))
    val meanStds: Array[(Double, Double)] =
      statses.map(stats => (stats.lidstoneMean(smoothing, Stats.dimension(xt)), stats.stdDev)).toArray

    new Likelihood[T](index, meanStds, values.size / xt.size)
  }

  lazy val validate: (Vector[Array[T]], Vector[Int]) => Try[Double] = (xt: Vector[Array[T]], expected: Vector[Int]) => {
    val predict: (Array[T]) => Int = model.classify(_: Array[T])

    Try(Validation[T, Int](xt zip expected, numClass)(predict).f1Score)
  }

}
