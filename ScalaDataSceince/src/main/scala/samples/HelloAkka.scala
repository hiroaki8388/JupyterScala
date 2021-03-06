package samples

import akka.actor._


class EchoActor extends Actor with ActorLogging {
  def receive = {
    case msg:String =>
      Thread.sleep(500)
      log.info(s"Received '$msg'")
  }
}

object HelloAkka extends App {

  // We need an actor system before we can
  // instantiate actors
  val system = ActorSystem("HelloActors")

  // instantiate our two actors
  val echo1 = system.actorOf(Props[EchoActor], name="echo1")
  val echo2 = system.actorOf(Props[EchoActor], name="echo2")

  // Send them messages. We do this using the "!" operator
  echo1 ! "hello echo1"
  echo2 ! "hello echo2"
  echo1 ! "bye bye"

  // Give the actors time to process their messages,
  // then shut the system down to terminate the program
  Thread.sleep(500)
  system.shutdown


  val scores = Map(
    "ichiro" -> Map("math"->82, "english"->99),
    "jiro" -> Map("japanese"->100, "social"->88),
    "saburo" -> Map("math"->76, "english"->80),
    "shiro" -> Map("math" -> 99, "social" -> 81),
    "hanako" -> Map("math"->84, "english"->78, "social"->66))

  val a=scores.flatMap { case (name, score) =>
    score.get(“math”).flatMap { ma =>
    score.get(“english”).flatMap { en =>
    val avg = (ma + en) / 2
    if(avg >= 80) Some(name -> avg) else None
  }
  }
  }

  val b=scores.flatMap { case (name, score) =>
    score.get("math")}


}