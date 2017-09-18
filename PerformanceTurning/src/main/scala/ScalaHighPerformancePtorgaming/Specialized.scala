package ScalaHighPerformancePtorgaming

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._

/**
  * Created by hasegawahiroaki on 2017/09/18.
  */
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
class Specialized {

  @Benchmark
  def testList() = List.fill(100000)(2) map (i => Data(i) :: Nil)

  @Benchmark
  def testList2() = List.fill(100000)(2) map (i => Data(i) :: Nil)

}
//Value Classとして定義する事で、プリミティブ型と同等に扱われる
case class Data(num: Long) extends AnyVal

//boxingされなくなる
// https://ja.wikipedia.org/wiki/%E3%82%AA%E3%83%BC%E3%83%88%E3%83%9C%E3%82%AF%E3%82%B7%E3%83%B3%E3%82%B0
case class Data2(@specialized num: Long)