case class Price (value:Long) 

case class Price2 (value:Long) extends AnyVal


object AnyVal{
val price=Price(1)
val price2=Price2(1)
}
