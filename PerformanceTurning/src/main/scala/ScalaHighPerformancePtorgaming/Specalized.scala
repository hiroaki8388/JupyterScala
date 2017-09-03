package ScalaHighPerformancePtorgaming

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._

/**
  * Created by hasegawahiroaki on 2017/09/03.
  */
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, timeUnit = TimeUnit.SECONDS)
//@Fork(value = 1, warmups = 1, jvmArgs = Array("-Xms1G", "-Xmx1G"))
class SpecializationBenchmark {

  @Benchmark
  def specialized(): Double =
    SpecializationBenchmark.specializedExecution.shareCount.toDouble * SpecializationBenchmark.specializedExecution.price

  @Benchmark
  def boxed(): Double =
    SpecializationBenchmark.boxedExecution.shareCount.toDouble * SpecializationBenchmark.boxedExecution.price
}

object SpecializationBenchmark {
  class SpecializedExecution[@specialized(Int) T1, @specialized(Double) T2](val shareCount: Long, val price: Double)
  class BoxingExecution[T1, T2](val shareCount: T1, val price: T2)

  val specializedExecution: SpecializedExecution[Int, Double] =
    new SpecializedExecution(10l, 2d)
  val boxedExecution: BoxingExecution[Long, Double] = new BoxingExecution(10l, 2d)
}
