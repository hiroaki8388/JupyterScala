case class Boxed[T](value: T)
case class UnBoxed[@specialized(Long)  T](value: T) 


object Test{
def createBoxed(l: Long): Boxed[Long] = Boxed(l) 

def createUnBoxed(l: Long): UnBoxed[Long] = UnBoxed(l)

val boxed=createBoxed(1)
val unboxed=createUnBoxed(1)
}
