// パッケージ名がないとjvhがエラーを吐くので、とりあえず入れてる
package perfomanceturning
import java.util.concurrent.TimeUnit
import org.openjdk.jmh.annotations._

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
class Test {

  @Benchmark
  def testList() = List(1 to 1000) map (i => i ++ Nil)

  @Benchmark
  def testList2() = List(1 to 1000) map (i => i :: Nil)
}
