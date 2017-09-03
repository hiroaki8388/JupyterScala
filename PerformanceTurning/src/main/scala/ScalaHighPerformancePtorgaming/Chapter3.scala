package ScalaHighPerformancePtorgaming

/**
  * Created by hasegawahiroaki on 2017/09/02.
  */


case class Price2(num: Long) extends AnyVal {

    def isLowerThan(price: Price2) = num < price.num
  }



case class ShareCount[T](value: T)

case class ShareCount2[T](@specialized value: T)




