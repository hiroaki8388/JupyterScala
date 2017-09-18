
object TupleAndClass{

def tuple: (Int, Int) = (1, 2)
def tuple2: (Int, HogeInt) = (1, HogeInt(2.0)) 
case class HogeInt(int:Double) extends AnyVal

def useClass=Class(1, HogeInt(1.0))
case class Class(int:Int,hogeInt:HogeInt) 
}
