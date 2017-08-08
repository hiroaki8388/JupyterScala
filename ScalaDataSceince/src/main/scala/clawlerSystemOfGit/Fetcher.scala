package clawlerSystemOfGit

import akka.actor.{Actor, ActorLogging, Props}


import scala.concurrent.Future
import scalaj.http.Http


//Fetcherに送るメッセージとFetcherを登録する責務をもつ
object Fetcher {

//  メッセージの種類を定義
  case class Fetch(login:String)

//  ファクトリーパターンの容量で、propsを作成
  def props(token:Option[String]):Props=
  Props(classOf[Fetcher],token)

  def props():Props=Props(classOf[Fetcher],None)

}


//GitのApIを叩き、データを取得する
class Fetcher(token:Option[String])extends Actor with ActorLogging{
  import context.dispatcher
  import clawlerSystemOfGit.Fetcher.Fetch


//  受け取ったキューに従い実行
  override def receive: Receive = {
    case Fetch(login)=> fetchUrl(login)
  }

  private def fetchUrl(login:String)={
    val unauthorizedRequest=Http(
      s"https://api.github.com/users/$login/followers")

//    headerにtokenを入れる
    val authorizedRequest=token.map{t=>
      unauthorizedRequest.header("Authorization", s"token $t")
    }

    val request =authorizedRequest.getOrElse(unauthorizedRequest)
//    APIの実行


    val response=Future{request.asString}

    response.onComplete{r=>
      log.info(s"Response from $login : $r")

    }
  }

}