package clawlerSystemOfGit

import akka.actor.ActorSystem
import clawlerSystemOfGit.Fetcher.Fetch
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * Created by hasegawahiroaki on 2017/08/08.
  */
object Main extends App{

//  メッセージ送信用のsystemを生成
  val system=ActorSystem("fetchers")
  //  curl -u 'hiroaki8388' -d '{"scopes":["repo"],"note":"Help example"}'  https://api.github.com/authorizations
  val token =  Some("d6a7c8525ffea43231809b424d0b09ab5ab137b1")

  //非同期処理で同時に4つのデータを取得するため、メッセージ送信用のActorを生成
  val fetchers=(0 to 4).map{_=>
    system.actorOf(Fetcher.props(token))
  }

//  メッセージを送信

  fetchers(0) ! Fetch("odersky")
  fetchers(1) ! Fetch("hiroaki8388")
  fetchers(2) ! Fetch("rkuhn")
  fetchers(3) ! Fetch("tototoshi")


  system.scheduler.scheduleOnce(5.seconds)(system.shutdown())
}



