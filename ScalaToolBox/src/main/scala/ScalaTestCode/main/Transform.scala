package ScalaTestCode.main

import scala.util.Try

/**
  * Created by hasegawahiroaki on 2017/11/03.
  */

trait Config
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