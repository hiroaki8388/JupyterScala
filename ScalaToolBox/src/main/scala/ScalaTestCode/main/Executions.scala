package ScalaTestCode.main

import ScalaTestCode.Constant._
import ScalaTestCode.main.Executions.{Execution, Http, Mysql, Aero}

import scala.util.Try

/**
  * Created by hasegawahiroaki on 2017/11/11.
  */
object Executions {

 trait Execution

  case class Mysql(command: String) extends Execution

  case class Aero(command: String) extends Execution

  case class Http(command: String) extends Execution

//  どのタイミングで、なにを行うか
  // regStrでmysql,aero,httpのどれかを判定する
  def getColtoPreFunc(coln: Int=0, regStr: String): (Fields) =>Map[Int, Seq[Mysql]] = {
    val reg = regStr.r
  (fields: Fields) =>
    Map(0,   for {
        filtered <- reg.findAllMatchIn(fields(coln)).toSeq
      } yield Mysql(filtered.toString))
  }

  // regStrでmysql,aero,httpのどれかを判定する
  def getColtoTestFunc(coln: Int=2, regStr: String): (Fields) =>  Map[Int, Seq[Aero]] = {
    val reg = regStr.r
    (fields: Fields) =>
      Map(1,   for {
        filtered <- reg.findAllMatchIn(fields(coln)).toSeq
      } yield Aero(filtered.toString))
  }

  // regStrでmysql,aero,httpのどれかを判定する
  def getColtoPostFunc(coln: Int=3, regStr: String): (Fields) =>  Map[Int, Seq[Http]] = {
    val reg = regStr.r
   Map(2, (fields: Fields) =>
      for {
        filtered <- reg.findAllMatchIn(fields(coln)).toSeq
      } yield Http(filtered.toString))

  }
}

//trait UseMysql{
//  val mysql:Myql
//}
class Executions extends Transform[Seq[Execution],String](ConfigNone){

  override def |> : PartialFunction[Seq[Execution], Try[String]] = {

    case executions:Seq[Execution]=>{

      val sortedExes=executions partition(_.isInstanceOf[Mysql])
     val pre: Mysql = executions.filter(_.isInstanceOf[Mysql]).asInstanceOf[Mysql]
      val test: Aero = executions.filter(_.isInstanceOf[Aero]).asInstanceOf[Aero]
      val post: Http = executions.filter(_.isInstanceOf[Http]).asInstanceOf[Http]


      }
    }
  }
}




