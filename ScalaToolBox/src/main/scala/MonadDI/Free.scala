package MonadDI

/**
  * Created by hasegawahiroaki on 2017/10/29.
  */

object Main extends App{
  scala.io.StdIn

}

 trait Functor[F[_]]{
  def map[A,B](a:F[_])(f:A=>B):F[B]
}

case class Done[F[_]:Functor,A](a:A) extends Free[F,A] // Pureと名付ける事もあるが、わかりやすいのでDoneとする
case class More[F[_]]

 abstract class Free[F[_],A](implicit F:Functor[F]){
  def flatMap[B](f:A=>Free[F,B])

}

