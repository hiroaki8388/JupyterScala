package ScalaTestCode.main

import ScalaTestCode.Constant._
import akka.actor.ActorSystem
import scalikejdbc.config.DBs
import scalikejdbc.{DB, SQL}

/**
  * Created by hasegawahiroaki on 2017/11/11.
  */


object Main extends App {


  val file = new FileSource[Seq[String]](config = FileSourceConfig(fileName =
    "/Users/hasegawahiroaki/ScalaJupyter/ScalaToolBox/src/main/scala/ScalaTestCode/test/test.csv",
    delim = CSV)
    , rowSelectFOpt = None)

  trait DSL extends UsesMysql with UsesAero with UsesHttp {

    /*事前、実行、事後処理を行うメソッド*/
    def executeFunc(coln: Int = 0, regStr: String): Fields => Seq[String] = {
      val reg = regStr.r
      (fields: Fields) =>
        reg.findAllMatchIn(fields(coln)).map { a =>
          val b = a.toString.split(":").head
          /*TODO リファクタ*/
          val c = a.toString.split(":").last
          if (b.contains("mysql")) mysqlClient.update(c)
          else if (b.contains("aero")) aeroClient.update(c)
          else if (b.contains("http")) httpClient.execute(c)
          else "Do Nothing!!"
        }.toSeq
    }

    val dsl =
      for {
        a <- file |> executeFunc(coln = 0, regStr = """<[^>]*>""") ::
          executeFunc(coln = 1, regStr = """<[^>]*>""") ::
          executeFunc(coln = 2, regStr ="""<[^>]*>""") :: Nil
      } yield ()


  }

}

trait UsesMysql {
  val mysqlClient: MysqlClient
}

trait UsesAero {
  val aeroClient: AeroClient
}

trait UsesHttp {
  val httpClient: HttpClient
}

class MysqlClient {
  def update(command: String): String = {
    DBs.setup()
    val isSuccess = DB.autoCommit { implicit session =>
      SQL(
        command
      ).execute().apply()


    }
    s"mysqlUpdate is $isSuccess"
  }
}

class AeroClient {
  val system=ActorSystem()


  def update(command: String): String = ???
}


class HttpClient {
  def execute(command: String): String = ???
}


//object Executions {


//  case class Mysql(command: String) extends Execution
//
//  case class Aero(command: String) extends Execution
//
//  case class Http(command: String) extends Execution

//  どのタイミングで、なにを行うか
// regStrでmysql,aero,httpのどれかを判定する

// regStrでmysql,aero,httpのどれかを判定する

//
//  def getColtoClientFunc[A <: Execution](coln: Int = 0, rank: Int = 1, regStr: String): (Fields) => Map[Int, Seq[Reader[A, String]]] = {
//    val reg = regStr.r
//    (fields: Fields) =>
//      Map(rank ->
//        reg.findAllMatchIn(fields(coln)).toSeq.map(filtered =>
//          Reader.reader[A, String](env => env.client.execute(filtered.toString)))) // TODO リファクタ
//  }
//
//  def getColtoMysqlFunc(coln: Int = 0, rank: Int = 1, regStr: String): (Fields) => Map[Int, Seq[Reader[UseMysql, String]]] = {
//    val reg = regStr.r
//    (fields: Fields) =>
//      Map(rank ->
//        reg.findAllMatchIn(fields(coln)).toSeq.map(filtered =>
//          Reader.reader[UseMysql, String](env => env.client.execute(filtered.toString)))) // TODO リファクタ
//  }
//
//  // regStrでmysql,aero,httpのどれかを判定する
//  def getColtoAeroFunc(coln: Int = 2, rank: Int = 2, regStr: String): (Fields) => Map[Int, Seq[Reader[UseAero, String]]] = {
//    val reg = regStr.r
//    (fields: Fields) =>
//      Map(rank ->
//        reg.findAllMatchIn(fields(coln)).toSeq.map(filtered =>
//          Reader.reader[UseAero, String](env => env.client.execute(filtered.toString)))) // TODO リファクタ
//  }
//
//  // regStrでmysql,aero,httpのどれかを判定する
//  def getColtoHttpFunc(coln: Int = 3, rank: Int = 3, regStr: String): Fields => Map[Int, Seq[Reader[UseHttp, String]]] = {
//    val reg = regStr.r
//    (fields: Fields) =>
//      Map(rank ->
//        reg.findAllMatchIn(fields(coln)).toSeq.map(filtered =>
//          Reader.reader[UseHttp, String](env => env.client.execute(filtered.toString)))) // TODO リファクタ
//  }
//}

//trait UseMysql{
//  val mysql:Myql
////}
//class Executions extends Transform[Seq[Map[Int, Seq[Exception]]], String](ConfigNone) {
//
//  override def |> : PartialFunction[Seq[Map[Int, Seq[Reader[Exception, String]]]], Try[Seq[Reader[Exception, String]]]] = {
//
//    case executions: Map[Int, Seq[Exception]] if executions.nonEmpty => Try {
//
//      val sorted: Seq[Reader[Exception, String]] = executions.sortBy(_.keys.head).flatMap(_.values.head) // TODO リファクタ
//
//      sorted
//    }
//
//
//  }
//}
//
//case class Reader[E, A](g: E => A) {
//  def apply(e: E) = g(e)
//
//  def run: E => A = apply
//
//  def map[B](f: A => B): Reader[E, B] = Reader(e => f(g(e)))
//
//  def flatMap[B](f: A => Reader[E, B]): Reader[E, B] = Reader(e => f(g(e))(e))
//}
//
//object Reader {
//  def pure[E, A](a: A): Reader[E, A] = Reader(e => a)
//
//  def ask[E]: Reader[E, E] = Reader(identity)
//
//  def local[E, A](f: E => E, c: Reader[E, A]): Reader[E, A] = Reader(e => c.apply(f(e)))
//
//  def reader[E, A](f: E => A): Reader[E, A] = Reader(f)
//}



