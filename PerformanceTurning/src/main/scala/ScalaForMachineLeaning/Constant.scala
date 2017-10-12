package ScalaForMachineLeaning

/**
  * Created by hasegawahiroaki on 2017/10/12.
  */
package object Constant {
  type DblF = Double => Double
  type DblVec = Vector[Double]
  type DbPair=(Double,Double)

  private val MINLOGARG = 1e-32
  private val MINLOGVALUE = -MINLOGARG
}
