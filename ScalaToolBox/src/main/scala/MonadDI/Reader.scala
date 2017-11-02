package MonadDI

/**
  * Created by hasegawahiroaki on 2017/10/29.
  */
object main extends App {
  def update(data:String):Reader[UseDBClient,Unit]=
    Reader.reader(env=>env.dbClient.update(data))

  def isConnectOnDB:Reader[UseDBClient,Boolean]=
    Reader.ask.map(_.dbClient.isConnect)

  (for {
    isCon <- isConnectOnDB
    _ <- Reader.local(
      (env:UseDBClient)=>
        if (isCon) env else DBEnvironment.nosqlEnvironment
      ,update("data"))
  }yield ()
    ).run(DBEnvironment.mysqlEnvironment)

  update("data1").flatMap(a => update("data2").map(a => ())).run(DBEnvironment.mysqlEnvironment)
}

// find系のメソッドを定義する
trait DBClient{
  def isConnect:Boolean

  def update(data:String)
}

trait UseDBClient{
  val dbClient:DBClient
}

class MysqlClient extends DBClient{
  def isConnect:Boolean={println("MySql con not Connect!!") ;false} // mock。必ず偽を返す
  def update(data:String)=println(s"updated $data InMysql!!")
}
class NosqlClient extends DBClient {
  def isConnect: Boolean = {
    println("NoSql con  Connect!!");
    true
  }

  // mock。必ず真を返す
  def update(data: String) = println(s"updated $data InNosql!!")
}

  object DBEnvironment {
    private val mysqlClient = new MysqlClient

    val mysqlEnvironment = new UseDBClient {
      val dbClient = mysqlClient
    }

    private val nosqlClient = new NosqlClient

    val nosqlEnvironment = new UseDBClient {
      val dbClient = nosqlClient
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

