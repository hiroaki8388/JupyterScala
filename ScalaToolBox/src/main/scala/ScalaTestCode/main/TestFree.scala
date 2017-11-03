package ScalaTestCode.main

/**
  * Created by hasegawahiroaki on 2017/11/05.
  */
trait Functor[F[_]] {
  def map[A, B](m: F[A])(f: A => B): F[B]
}

trait Free[F[+ _], +A] {
  def flatMap[B](f: A => Free[F, B])(implicit func: Functor[F]): Free[F, B]

  // flatMapからmapを構築する
  def map[B](f: A => B)(implicit func: Functor[F]): Free[F, B] = flatMap(x => Done(f(x)))
}


case class Done[F[+ _], +A](x: A) extends Free[F, A] {
  def flatMap[B](f: A => Free[F, B])(implicit func: Functor[F]): Free[F, B] = f(x)
}

case class More[F[+ _], +A](x: F[Free[F, A]]) extends Free[F, A] {
  // xにF[Free]を入れると、Free型に畳み込める
  def flatMap[B](f: A => Free[F, B])(implicit func: Functor[F]): Free[F, B] =
  More[F, B](func.map(x)(_.flatMap(f)))
}


sealed trait Process[+A]

case class PreProcess[+A](command: String, x: A) extends Process[A]
case class Execute[+A](command: String) extends Process[A]
case class PostProcess[+A](command: String) extends Process[A]

object Process {

  def preProcess(command: String,f:String=>Free[Process,Unit]) =
    More[Process,Unit](PreProcess(command,f(command)))

  def execute(command: String,f:String=>Free[Process,String]) =
    More[Process,String](PreProcess(command,f(command)))

  def postProcess(command: String,f:String=>Free[Process,Unit]) =
    More[Process,Unit](PreProcess(command,f(command)))

  def foldProcess[](comands:Seq[String])
}









