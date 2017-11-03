import scala.language.higherKinds
import scala.io.StdIn._

/**
  * Created by hasegawahiroaki on 2017/10/29.
  */

object Main extends App {
  val a = Some(1)
  val b = Some(2)
  val c = Some(3)

  val abc: Option[Option[Option[Int]]] = a.map(x => b.map(y => c.map(z => x + y + z)))
  val data: Option[Option[Option[Done[Option, Int]]]] = abc.map(a => a.map(b => b.map(c => Done[Option, Int](c))))
  val data2: Option[Option[More[Option, Int]]] = abc.map(a => a.map(b => More[Option, Int](b.map(c => Done[Option, Int](c)))))
  val data3: Option[More[Option, Int]] = abc.map(a => More[Option, Int](a.map(b => More[Option, Int](b.map(c => Done[Option, Int](c))))))
  val data4: More[Option, Int] = More[Option, Int](abc.map(a => More[Option, Int](a.map(b => More[Option, Int](b.map(c => Done[Option, Int](c)))))))

  implicit val charIOFunctor: Functor[CharIO]
  = {
    new Functor[CharIO] {
      def map[A, B](m: CharIO[A])(f: A => B): CharIO[B] = m match {
        case Push(ch, a) => Push(ch, f(a))
        //　応用
        case Fetch(g) => Fetch(f compose g)
      }
    }
  }

  def pushChar(ch: Char): Free[CharIO, Unit] = More[CharIO, Unit](Push(ch, Done()))

  // 応用
  def fetchChar: Free[CharIO, Char] = More[CharIO, Char](Fetch(ch => Done(ch)))

  def foldChar[Func[+ _] : Functor, A](s: Seq[A], f: A => Free[Func, Unit]): Free[Func, Unit] = s.toList match {
    case x :: xs => xs.foldLeft(f(x))((y: Free[Func, Unit], z: A) => y.flatMap(unit => f(z)))
    case Nil => Done(())
  }


  /**
    * More(Push(f,More(Push(r,More(Push(e,More(Push(e,More(Push(e,More(Push(e,More(
    * Push(e,More(Push(e,More(Push(e,More(Push(e,More(Push(e,More(Push(e,More(Push(e,More(Push(e,More(Push(e,More(Push(e,Done(())))))))))))))))))))))))))))))))))
    */
  //  println(foldChar("freeeeeeeeeeeeee",pushChar))
  val flow: Free[CharIO, Unit] = foldChar("freeeeeeeeeeeeee", pushChar)

  def run(flow: Free[CharIO, Unit]): Unit = flow match {
    case More(Push(ch, res)) => println(ch); run(res)
    case Done(unit) => unit
    //      応用
    case More(Fetch(f)) => run(f('!'))
  }

  //  run(flow)

  val flow2: Free[CharIO, Unit] = for {
    _ <- foldChar("free", pushChar)
    eosMark <- fetchChar
    _ <- foldChar("monad" + eosMark, pushChar)
  } yield ()

  run(flow2)
}


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
  def flatMap[B](f: A => Free[F, B])(implicit func: Functor[F]): Free[F, B] = {
    val z = func.map(x) { y =>
      val a: Free[F, B] = y.flatMap(k => f(k))
      a
    }
    More[F, B](z)
  }

  // または
  def flatMap2[B](f: A => Free[F, B])(implicit func: Functor[F]): Free[F, B] =
  More[F, B](func.map(x)(_.flatMap(f)))
}

trait CharIO[+A]

case class Push[+A](ch: Char, a: A) extends CharIO[A]

// 応用
case class Fetch[+A](f: Char => A) extends CharIO[A]








