package ScalaTestCode.main

/**
  * Created by hasegawahiroaki on 2017/11/05.
  */
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


  def runWithDB(command: String): Reader[UseDBClient, Unit] = {
    ask.map(_.dbClient.execute(command))
  }

  def runWithHTTP(command: String): Reader[UseHTTPClient, String] = {
    ask.map(_.httpClient.execute(command))
  }


}
//
//trait Client[A] {
//  def execute(commend: String): A
//}
//
//// TODO HTTPとDBのClientを統一できないか考える
//trait DBClient extends Client[Unit]
//
//trait UseDBClient {
//  val dbClient: DBClient
//}
//
//trait HTTPClient extends Client[String]
//
//trait UseHTTPClient {
//  val httpClient: HTTPClient
//}



