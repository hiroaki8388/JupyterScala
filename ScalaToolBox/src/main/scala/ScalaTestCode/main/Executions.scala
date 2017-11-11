package ScalaTestCode.main

import MonadDI.Reader
import ScalaTestCode.Constant._
import ScalaTestCode.main.Executions.Execution

import scala.util.Try

/**
  * Created by hasegawahiroaki on 2017/11/11.
  */
object Executions {

  trait Execution {
    def execute(command: String): String
  }

  trait UseMysql{
    val client:Execution
  }

  trait UseAero{
    val client:Execution
  }

  trait UseHttp{
    val client:Execution
  }


  //  case class Mysql(command: String) extends Execution
  //
  //  case class Aero(command: String) extends Execution
  //
  //  case class Http(command: String) extends Execution

  //  どのタイミングで、なにを行うか
  // regStrでmysql,aero,httpのどれかを判定する
  def getColtoMysqlFunc(coln: Int = 0, rank: Int, regStr: String): (Fields) => Map[Int, Seq[Reader[UseMysql, String]]] = {
    val reg = regStr.r
    (fields: Fields) =>
      Map(rank, for {
        filtered <- reg.findAllMatchIn(fields(coln)).toSeq
      } yield Reader.reader[UseMysql, String](env => env.client.execute(filtered.toString)))
  }

  // regStrでmysql,aero,httpのどれかを判定する
  def getColtoAeroFunc(coln: Int = 2, rank: Int, regStr: String): (Fields) => Map[Int, Seq[Reader[UseAero, String]]] = {
    val reg = regStr.r
    (fields: Fields) =>
      Map(rank, for {
        filtered <- reg.findAllMatchIn(fields(coln)).toSeq
      } yield  Reader.reader[UseAero, String](env => env.client.execute(filtered.toString)))
  }

  // regStrでmysql,aero,httpのどれかを判定する
  def getColtoHttpFunc(coln: Int = 3, rank: Int, regStr: String)= {
    val reg = regStr.r
    Map(rank, (fields: Fields) =>
      for {
        filtered <- reg.findAllMatchIn(fields(coln)).toSeq
      } yield Reader.reader[UseHttp, String](env => env.client.execute(filtered.toString)))
  }
}

//trait UseMysql{
//  val mysql:Myql
//}
class Executions extends Transform[Map[Int, Seq[Execution]], String](ConfigNone) {

  override def |> : PartialFunction[Map[Int, Seq[Execution]], Try[String]] = {

    case executions: Map[Int, Seq[Execution]] if executions.nonEmpty => {

      val sorted = executions.foldLeft(Map.empty)((a, b) => a)
    }


  }
}

case class Reader[E, A](g: E => A) {
  def apply(e: E) = g(e)

  def run: E => A = apply

  def map[B](f: A => B): Reader[E, B] = Reader(e => f(g(e)))

  def flatMap[B](f: A => Reader[E, B]): Reader[E, B] = Reader(e => f(g(e))(e))
}

object Reader {
  def pure[E, A](a: A): Reader[E, A] = Reader(e => a)

  def ask[E]: Reader[E, E] = Reader(identity)

  def local[E, A](f: E => E, c: Reader[E, A]): Reader[E, A] = Reader(e => c.apply(f(e)))

  def reader[E, A](f: E => A): Reader[E, A] = Reader(f)
}



