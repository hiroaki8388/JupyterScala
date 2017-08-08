package clawlerSystemOfGit

import akka.actor.{Actor, Props}


/**
  * Created by hasegawahiroaki on 2017/08/08.
  */
//Fetcherに送るメッセージとFetcherを登録する責務をもつ
object Fetcher {

//  メッセージの種類を定義
  case class Fetch(login:String)

//  ファクトリーパターンの容量で、propsを作成
  def props(token:Option[String]):Props=
  Props(classOf[Fetch],token)

  def props():Props=Props(classOf[Fetch],None)

}


class Fetcher(token:Option[String])extends Actor {

}