package ScalaHighPerformancePtorgaming

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._

import scala.annotation.switch



class PatternMatchState {
  @Param(Array("1", "5", "10"))
  var matchIndex: Int = 0
}

@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, timeUnit = TimeUnit.SECONDS)
@State(Scope.Benchmark)
class PatternMatchingBenchmarks {

  private val (a, b, c, d, e, f, g, h, i, j) = (1, 2, 3, 4, 5, 6, 7, 8, 9, 10)




  case class ExpensiveFoo(value: Int)

  @Benchmark
  def matchIntLiterals(i: PatternMatchState): Int =

    i.matchIndex match {

    case 1 => 1
    case 2 => 2
    case 3 => 3
    case 4 => 4
    case 5 => 5
    case 6 => 6
    case 7 => 7
    case 8 => 8
    case 9 => 9
    case 10 => 10
  }

  @Benchmark
  @switch
  def matchIntVariables(ii: PatternMatchState): Int = ii.matchIndex match {
    case `a` => 1
    case `b` => 2
    case `c` => 3
    case `d` => 4
    case `e` => 5
    case `f` => 6
    case `g` => 7
    case `h` => 8
    case `i` => 9
    case `j` => 10
  }

//  @Benchmark
//  def matchAnyVal(i: PatternMatchState): Int = CheapFoo(i.matchIndex) match {
//    case CheapFoo(1) => 1
//    case CheapFoo(2) => 2
//    case CheapFoo(3) => 3
//    case CheapFoo(4) => 4
//    case CheapFoo(5) => 5
//    case CheapFoo(6) => 6
//    case CheapFoo(7) => 7
//    case CheapFoo(8) => 8
//    case CheapFoo(9) => 9
//    case CheapFoo(10) => 10
//  }

  @Benchmark
  def matchCaseClass(i: PatternMatchState): Int =

    ExpensiveFoo(i.matchIndex) match {
      case ExpensiveFoo(1) => 1
      case ExpensiveFoo(2) => 2
      case ExpensiveFoo(3) => 3
      case ExpensiveFoo(4) => 4
      case ExpensiveFoo(5) => 5
      case ExpensiveFoo(6) => 6
      case ExpensiveFoo(7) => 7
      case ExpensiveFoo(8) => 8
      case ExpensiveFoo(9) => 9
      case ExpensiveFoo(10) => 10
    }



}

case class CheapFoo(value: Int) extends AnyVal