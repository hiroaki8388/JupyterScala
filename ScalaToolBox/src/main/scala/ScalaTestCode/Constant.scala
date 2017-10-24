package ScalaTestCode

/**
  * Created by hiroaki on 2017/10/24.
  */
package object  Constant {
  final type DblF = Double => Double
  final type DblArray = Array[Double]
  final type DbPair=(Double,Double)
  final type IMatrix=Vector[Array[Int]]
  final type DbMatrix=Vector[Array[Double]]
  final type Fields=Array[String]
  final type LabeledVector[T] = Vector[(Array[T], Int)]

}
