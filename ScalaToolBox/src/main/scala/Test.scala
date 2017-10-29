//// パッケージ名がないとjvhがエラーを吐くので、とりあえず入れてる
//package perfomanceturning
//
//import java.util.concurrent.TimeUnit
//import org.openjdk.jmh.annotations._
//
////アウトプットの単位
//@OutputTimeUnit(TimeUnit.SECONDS)
//// ウォームアップの設定
//@Warmup(iterations = 3, timeUnit = TimeUnit.SECONDS)
//// 計測回数の設定
//@Measurement(iterations = 3, timeUnit = TimeUnit.SECONDS)
//// 全体の計測回数（-Xms1Gは使用するメモリ）
//@Fork(value = 1, jvmArgs = Array("-Xms1G", "-Xmx1G"))
//// 使用するスレッド数
//@Threads(value = 1)
//class Test {
//
//  @Benchmark
//  def testList() = List.fill(100000)(2) map (i => Nil :+ Data(i))
//
//  @Benchmark
//  def testList2() = List.fill(100000)(2) map (i => Data(i) :: Nil)
//
//  @Benchmark
//  def testList3() = List.fill(100000)(2) map (i => Data2(i) :: Nil)
//
//  @Benchmark
//  def testList4() = List.fill(100000)(2) map (i => Data2(i) :: Nil)
//
//}
//
////普通のオブジェクト
//case class Data(num: Long)
//
////Value Classとして定義する事で、プリミティブ型と同等に扱われ、メモリ効率が良くなる
//case class Data2(num: Long) extends AnyVal
//
////boxingされなくなる
//case class Data3(@specialized num: Long)
//
//////アウトプットの単位
////@OutputTimeUnit(TimeUnit.SECONDS)
////// ウォームアップの設定
////@Warmup(iterations = 3, timeUnit = TimeUnit.SECONDS)
////// 計測回数の設定
////@Measurement(iterations = 3, timeUnit = TimeUnit.SECONDS)
////// 全体の計測回数（-Xms1Gは使用するメモリ）
////@Fork(value = 1, jvmArgs = Array("-Xms1G", "-Xmx1G"))
////// 使用するスレッド
////@Threads(value = 1)
////// スレッドのスコープの設定
////@State(Scope.Thread)
////class Test2{
////
////  @Setup
////  val num= (1 to 1000)
////
////  @Benchmark
////  def createData(num:Range)=num.toList.map(Data(_)).foldLeft(0L)((x,data2)=>x+data2.num)
////
////  @Benchmark
////  def createData2(num:Range)=num.toList.map(Data2(_)).foldLeft(0L)((x,data2)=>x+data2.num)
////}
////
////
////
////
////case class Data(num:Long)
////
////case  class Data2(num:Long) extends AnyVal
////
//////アウトプットの単位
////@OutputTimeUnit(TimeUnit.SECONDS)
////// ウォームアップの設定
////@Warmup(iterations = 3, timeUnit = TimeUnit.SECONDS)
////// 計測回数の設定
////@Measurement(iterations = 3, timeUnit = TimeUnit.SECONDS)
////// 全体の計測回数（-Xms1Gは使用するメモリ）
////@Fork(value = 1, jvmArgs = Array("-Xms1G", "-Xmx1G"))
////// 使用するスレッド数
////@Threads(value = 1)
////class Test3 {
////}