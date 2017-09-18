//package ScalaHighPerformancePtorgaming
//
///**
//  * Created by hasegawahiroaki on 2017/09/02.
//  */
//
//
//case class Price2(num: Long) extends AnyVal {
//
//    def isLowerThan(price: Price2) = num < price.num
//}
//
//
//
//
//case class ShareCount(value: Int) extends AnyVal
//case class ExecutionCount(value: Int)
//
//
//class Container2[@specialized X, @specialized Y](x: X, y: Y)
//
//trait Hoge[@specialized X]
//
//class HogeClass[@specialized X](num:X) extends Hoge[X]
//
//class Test {
////  ボクシングされる
//  def shareCount = new Container2(ShareCount(1), 1)
//
////  ボクシングされる
//  def executionCount = new Container2(ExecutionCount(1), 1)
//
////  ボクシングされない
//  def ints = new Container2(1, 1)
//}
//
//
