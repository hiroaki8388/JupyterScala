package ScalaForMachineLeaning






import ScalaForMachineLeaning.Constant.{DblF, DblArray}

import scala.util.{Random, Try}
import Constant._

object  Chapter3 extends App {


  val workflow= new Workflow[DblF,DblArray,DblArray,Int] with MixInSampling with MixInNormalizer with MixInAggregator

  val g=(x:Double)=>Math.log(x+1.0)+Random.nextDouble

  val out=workflow ||> g

  println(out)


  val workflowWithoutAgr=new WorkflowWithoutAgr[DblF,DblArray,DblArray] with MixInSampling2 with MixInNormalizer

  val out2 = workflowWithoutAgr ||> g
  println(out2)
  // と簡単に部品を付け替えして実装できる
}

trait ITransform[T, A] {
  // T:インプットする特徴量のデータ,A:アウトプットされるデータ
  self =>
  def |> : PartialFunction[T, Try[A]] // データのバリデーションを行うメソッド

  def map[B](f: A => B): ITransform[T, B] = new ITransform[T, B] {
    override def |> = new PartialFunction[T, Try[B]] {
      override def isDefinedAt(t: T) =
        self.|>.isDefinedAt(t)

      override def apply(t: T): Try[B] = self.|>(t).map(f)
    }
  }

  def flatMap[B](f: A => ITransform[T, B]): ITransform[T, B] = new ITransform[T, B] {
    override def |> = new PartialFunction[T, Try[B]] {
      override def isDefinedAt(t: T) =
        self.|>.isDefinedAt(t)

      override def apply(t: T): Try[B] = self.|>(t).flatMap(f(_).|>(t))
    }
  }

  def andThen[B](tr: ITransform[A, B]): ITransform[T, B] = new ITransform[T, B] {
    override def |> = new PartialFunction[T, Try[B]] {
      override def isDefinedAt(t: T) =
        self.|>.isDefinedAt(t) && tr.|>.isDefinedAt(self.|>(t).get)

      override def apply(t: T) = tr.|>(self.|>(t).get)
    }
  }
}

trait Config

case class ConfigInt(iParam: Int) extends Config

case class ConfigDouble(fParam: Double) extends Config

case class ConfigArrayDouble(fParams: Array[Double]) extends Config

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

//  trait  ETransform[T,A] extends ITransform[T,A] {
//    self =>
//    val config:Config
//  def |> :PartialFunction[T,Try[A]]
//
//   def eMap[B](f: A => B): ETransform[T,B] =
//      new ETransform[T,B] {
//        val config=self.config
//    override def |> : PartialFunction[T, Try[B]] =  super.|>
//      }
//
//   def eFlatMap[B](f: A => ETransform[T,B]): ETransform[T,B] =
//      new ETransform[T, B]{
//        val config=self.config
//        override def |> : PartialFunction[T, Try[B]] = super.|>
//      }
//
//  def eAndThen[B](tr: ETransform[A,B]): ETransform[T,B] =
//      new ETransform[T, B]{
//        val config=self.config
//        override def|> :PartialFunction[T, Try[B]] = super.|>
//      }
//  }

trait UsesSampling[T, A] {
  val sampler: Transform[T, A]
}

trait UsesNormalization[T, A] {
  val normalizer: Transform[T, A]
}

trait UsesAggregation[T, A] {
  val aggregator: Transform[T, A]
}

// ワークフローの流れを構築
trait Workflow[T, U, V, W] extends UsesSampling[T, U] with UsesNormalization[U, V] with UsesAggregation[V, W] {

  def ||>(t: T): Try[W] =
    for {
      u <- sampler |> t
      v <- normalizer |> u
      w <- aggregator |> v
    } yield w


  def |||>(t: T): Try[W] =
    sampler.|>(t).flatMap(u => normalizer.|>(u).flatMap(w => aggregator.|>(w)))
}




trait MixInSampling {
  val sampler: Transform[DblF, DblArray] = new Transform[DblF, DblArray](ConfigInt(samples)) {
    override def |> : PartialFunction[DblF, Try[DblArray]] = {
      case f: DblF => Try(Array.tabulate(samples)(n => f(1.0 * n / samples))) // 0,0.01,0.02,,,1.00までの値をfで変換した要素をもつベクトルを生成
    }
  }
}

case class MinMax[T ](values:Array[T])(implicit f: T => Double) {


  val range = (0.0, 0.0)

  // valuesのなかで最小な値をと最大な値を抽出する
  protected val minMax = values.foldLeft(range) { (mM, x) => {
    val min = mM._1
    val max = mM._2
    (if (x < min) x else min, if (x > max) x else max)
  }
  }

  val min = minMax._1
  val max = minMax._2

  //[lox,high]の間で正規化する yi-low=(xi-x_low/(x_hig-x_low)(high-low))
  def normalize(low: Double = 0.0, high: Double = 1.0): DblArray = {
    val ratio = (high - low) / (max - min)
    values.map(x => (x - min) * ratio + low)
  }
}


trait MixInNormalizer {
  val normalizer: Transform[DblArray, DblArray] = new Transform[DblArray, DblArray](ConfigDouble(normRatio)) {
    override def |> = {
      case x: DblArray if x.nonEmpty => Try(MinMax[Double](x).normalize())
    }
  }
}

trait MixInAggregator {
  val aggregator = new Transform[DblArray, Int](ConfigInt(splits)) {
    override def |> : PartialFunction[DblArray, Try[Int]] = {
      case x: DblArray if x.nonEmpty => Try(x.indices.find(x(_) == 1.0).getOrElse(-1))
    }
  }
}


// 例えば、サンプルのロジックを入れ替え、aggregationを外したい場合
// 入れ替えることも、使い回す事も、拡張する事も自由自在
// ワークフローの流れを構築
trait WorkflowWithoutAgr[T, U, V] extends UsesSampling[T, U] with UsesNormalization[U, V]{

  def ||>(t: T): Try[V] =
    for {
      u <- sampler |> t
      v <- normalizer |> u
    } yield v
}

trait MixInSampling2 {
  val sampler: Transform[DblF, DblArray] = new Transform[DblF, DblArray](ConfigInt(samples)) {
    override def |> : PartialFunction[DblF, Try[DblArray]] = {
      case f: DblF => Try(Array.tabulate(samples)(n => f(Math.exp(1.0 * n / samples))))
    }
  }
}
