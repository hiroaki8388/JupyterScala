package ScalaForMachineLeaning

import ScalaForMachineLeaning.Constant.DbPair


/**
  * Created by hasegawahiroaki on 2017/10/12.
  */
object Chapter6  extends App{

}

class Likelihood[@specialized(Double) T<: AnyVal](label:Int,muSigma:Vector[DbPair],prior:Double){


  def score(obs:Array[T]):Double={
    (obs,muSigma).zipped // zipped (T[U])=>(U,T[U])

  }

}
