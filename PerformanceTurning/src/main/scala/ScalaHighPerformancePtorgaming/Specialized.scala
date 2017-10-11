package ScalaHighPerformancePtorgaming



import java.util.concurrent.TimeUnit

import ScalaHighPerformancePtorgaming.OptionCreationBenchmarks.ShareCount
import org.openjdk.jmh.annotations._

import scalaz.{@@, Tag}

sealed trait Opt

object OptOps {

  def some[@specialized A](x: A): A @@ Opt = Tag(x)
  def nullCheckingSome[@specialized A](x: A): A @@ Opt =
    if (x == null) sys.error("Null values disallowed") else Tag(x)
  def none[A]: A @@ Opt = Tag(null.asInstanceOf[A])

  def isSome[A](o: A @@ Opt): Boolean = o != null
  def isEmpty[A](o: A @@ Opt): Boolean = !isSome(o)

  def unsafeGet[A](o: A @@ Opt): A =
    if (isSome(o)) o.asInstanceOf[A] else sys.error("Cannot get None")

  def fold[A, B](o: A @@ Opt)(ifEmpty: => B)(f: A => B): B =
    if (o == null) ifEmpty else f(o.asInstanceOf[A])

  Math.exp
}

@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, timeUnit = TimeUnit.SECONDS)
class OptionCreationBenchmarks {

  @Benchmark
  def scalaSome(): Option[ShareCount] = Some(ShareCount(1))

  @Benchmark
  def scalaNone(): Option[ShareCount] = None

  @Benchmark
  def optSome(): ShareCount @@ Opt = OptOps.some(ShareCount(1))

  @Benchmark
  def optSomeWithNullChecking(): ShareCount @@ Opt =
    OptOps.nullCheckingSome(ShareCount(1))

  @Benchmark
  def optNone(): ShareCount @@ Opt = OptOps.none

  //  @Benchmark
  //  def optNoneReuse(): ShareCount @@ Opt = noShares
}

object OptionCreationBenchmarks {
  case class ShareCount(value: Long) extends AnyVal
  val noShares: ShareCount @@ Opt = OptOps.none
}
