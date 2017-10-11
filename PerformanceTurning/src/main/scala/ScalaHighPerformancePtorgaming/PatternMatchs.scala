package ScalaHighPerformancePtorgaming

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._

import scalaz.xml.pp.Config

//アウトプットの単位
@OutputTimeUnit(TimeUnit.SECONDS)
// ウォームアップの設定
@Warmup(iterations = 3, timeUnit = TimeUnit.SECONDS)
// 計測回数の設定
@Measurement(iterations = 3, timeUnit = TimeUnit.SECONDS)
// 全体の計測回数（-Xms1Gは使用するメモリ）
@Fork(value = 1, jvmArgs = Array("-Xms1G", "-Xmx1G"))
// 使用するスレッド数
@Threads(value = 1)
@State(Scope.Benchmark)
class SpeedChallenge {

  val unavailableStrings: Seq[String] = Seq("\\\\", "¥", "⓪", "①", "②", "③", "④", "⑤", "⑥", "⑦", "⑧", "⑨",
    "⑩", "⑪", "⑫", "⑬", "⑭", "⑮", "⑯", "⑰", "⑱", "⑲",
    "⑳", "㉑", "㉒", "㉓", "㉔", "㉕", "㉖", "㉗", "㉘", "㉙",
    "㉚", "㉛", "㉜", "㉝", "㉞", "㉟", "㊱", "㊲", "㊳", "㊴",
    "㊵", "㊶", "㊷", "㊸", "㊹", "㊺", "㊻", "㊼", "㊽", "㊾",
    "㊿", "⑴", "⑵", "⑶", "⑷", "⑸", "⑹", "⑺", "⑻", "⑼",
    "⑽", "⑾", "⑿", "⒀", "⒁", "⒂", "⒃", "⒄", "⒅", "⒆",
    "⒇", "❶", "❷", "❸", "❹", "❺", "❻", "❼", "❽", "❾", "➓",
    "⒈", "⒉", "⒊", "⒋", "⒌", "⒍", "⒎", "⒏", "⒐", "⒑",
    "⒒", "⒓", "⒔", "⒕", "⒖", "⒗", "⒘", "⒙", "⒚", "⒛",
    "Ⅰ", "Ⅱ", "Ⅲ", "Ⅳ", "Ⅴ", "Ⅵ", "Ⅶ", "Ⅷ", "Ⅸ", "Ⅹ", "Ⅺ",
    "Ⅻ", "ⅰ", "ⅱ", "ⅲ", "ⅳ", "ⅴ", "ⅵ", "ⅶ", "ⅷ", "ⅸ", "ⅹ",
    "ⅺ", "ⅻ", "№", "㏍", "℡", "㊤", "㊥", "㊦", "㊧", "㊨", "㈱",
    "㈲", "㈹", "㍾", "㍽", "㍼", "㍻", "㎜", "㎝", "㎞", "㎎", "㎏",
    "㏄", "㍉", "㌔", "㌢", "㍍", "㌘", "㌧", "㌃", "㌶", "㍑", "㍗",
    "㌍", "㌦", "㌣", "㌫", "㍊", "㌻", "｡", "｢", "｣", "､", "･", "ｦ",
    "ｧ", "ｨ", "ｩ", "ｪ", "ｫ", "ｬ", "ｭ", "ｮ", "ｯ", "ｰ", "ｱ", "ｲ", "ｳ",
    "ｴ", "ｵ", "ｶ", "ｷ", "ｸ", "ｹ", "ｺ", "ｻ", "ｼ", "ｽ", "ｾ", "ｿ", "ﾀ",
    "ﾁ", "ﾂ", "ﾃ", "ﾄ", "ﾅ", "ﾆ", "ﾇ", "ﾈ", "ﾉ", "ﾊ", "ﾋ", "ﾌ", "ﾍ",
    "ﾎ", "ﾏ", "ﾐ", "ﾑ", "ﾒ", "ﾓ", "ﾔ", "ﾕ", "ﾖ", "ﾗ", "ﾘ", "ﾙ", "ﾚ",
    "ﾛ", "ﾜ", "ﾝ", "ﾞ", "ﾟ")

  val targetString = Some("あいうえおかきくけこさしすせそ" * 100000)

  //禁止文字が文字列に含まれているかどうかを判定
  def baseValidate(value: Option[String]): Boolean = value match {
    case Some(str) => {
      def go(index: Int, length: Int): Boolean = {
        val unavailableString = unavailableStrings(index)
        if (index == length - 1) true
        else {
          if (str.contains(unavailableString)) false
          else go(index + 1, length)
        }
      }
      go(0, unavailableStrings.size)
    }
    case None => false
  }

  //とてもとても速いvalidate
  def validate(value: Option[String]): Boolean = ???

  @Benchmark
  def measurement = {
    baseValidate(targetString)
  }
}

import scala
.util.Try

trait ITransform[T,A]{ // T:インプットする特徴量のデータ,A:アウトプットされるデータ
  self=>
  def |> :PartialFunction[T,Try[A]] // データのバリデーションを行うメソッド
  def map[B](f: A => B): ITransform[T,B] = new ITransform[T,B] {
    override def |> : PartialFunction[T, Try[B]] = //3
      new PartialFunction[T, Try[B]] {     //4
      override def isDefinedAt(t: T): Boolean =  //5
      self.|>.isDefinedAt(t)
        override def apply(t: T): Try[B] = self.|>(t).map(f) //6
      }}

  def flatMap[B](
                  f: A => ITransform[T, B] //7
                ): ITransform[T, B] = new ITransform[T, B] { //8

    override def |> : PartialFunction[T, Try[B]] =
      new PartialFunction[T, Try[B]] { //9
      override def isDefinedAt(t: T): Boolean =
      self.|>.isDefinedAt(t)
        override def apply(t: T): Try[B] =
          self.|>(t).flatMap(f(_).|>(t))
      }}

    def andThen[B](
                    tr: ITransform[A, B]
    ): ITransform[T, B] = new ITransform[T, B] {

      override def |> : PartialFunction[T, Try[B]] =
        new PartialFunction[T, Try[B]] {
          override def isDefinedAt(t: T): Boolean =
            self.|>.isDefinedAt(t) &&
              tr.|>.isDefinedAt(self.|>(t).get)
          override def apply(t: T):Try[B] = tr.|>(self.|>(t).get)
        }

}}

//abstract class ETransform[T,A](config:Config) extends ITransform[T,A]{
//  self =>
//  override def map[B](f:A=>B):ETransform[T,B]=new ETransform[T,B](config){
//    override def |> = super.|>
//  }
//
//  def flatMap[B](f:A=>ETransform[T,B]):ETransform[T,B]=new ETransform[T,B](config){
//    override def |>  = super.|>
//  }
//
//  override  def andThen[B](tr:ITransform[A,B]):ETransform[T,B]= new ETransform[T,B](config){
//    override def |>  = super.|>
//  }
//}


//abstract class ETransform[T,A]( //11
//                                config: Config//12
//                              ) extends ITransform[T,A] {  //13
//  self =>
//  override def map[B](f: A => B): ETransform[T,B] =
//    new ETransform[T,B](config) {
//      override def |> : PartialFunction[T, Try[B]] = |>
//    }
//
//  override  def flatMap[B](f: A => ITransform[T,B]): ETransform[T,B] =
//    new ETransform[T, B](config) {
//      override def |> : PartialFunction[T, Try[B]] = super.|>
//    }
//
//  def andThen[B](tr: ETransform[A,B]): ETransform[T,B] =
//    new ETransform[T, B](config) {
//      override def|> :PartialFunction[T, Try[B]] = super.|>
//    }
//}


trait ETransform[T,A] extends ITransform[T,A] {
  self =>
  val config:Config

  override def map[B](f: A => B): ETransform[T,B] =
    new ETransform[T,B]{
      val config:Config=config
      abstract  override def |> : PartialFunction[T, Try[B]] = super.|>
    }

  override  def flatMap[B](f: A => ITransform[T,B]): ETransform[T,B] =
    new ETransform[T, B] {
      val config:Config=config
      abstract  override def |> : PartialFunction[T, Try[B]] =  super.|>
    }

  def andThen[B](tr: ETransform[A,B]): ETransform[T,B] =
    new ETransform[T, B]{
      val config:Config=config
      abstract  override def |> :PartialFunction[T, Try[B]] =  super.|>
    }
}