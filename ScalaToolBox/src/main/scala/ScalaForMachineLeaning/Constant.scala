package ScalaForMachineLeaning

/**
  * Created by hasegawahiroaki on 2017/10/12.
  */
package object Constant {
  final type DblF = Double => Double
  final type DblArray = Array[Double]
  final type DbPair=(Double,Double)
  final type IMatrix=Vector[Array[Int]]
  final type DbMatrix=Vector[Array[Double]]
  final type Fields=Array[String]
  final type LabeledVector[T] = Vector[(Array[T], Int)]

  final val CSV_DELM=","
  final val MINLOGARG = 1e-32
  final val MINLOGVALUE = -MINLOGARG
  final val LOG_2PI = -Math.log(2.0*Math.PI)
  final val EPS = 1e-6



  val (samples, normRatio,splits) = (100, 10, 4)
}
