//package Test
//
//import scalaz.{@@, Tag}
//
///**
//  * Created by hasegawahiroaki on 2017/09/18.
//  */
//sealed trait Opt
//
//object OptOps {
//
//  def some[@specialized A](x: A): A @@ Opt = Tag(x)
//  def nullCheckingSome[@specialized A](x: A): A @@ Opt =
//    if (x == null) sys.error("Null values disallowed") else Tag(x)
//  def none[A]: A @@ Opt = Tag(null.asInstanceOf[A])
//
//  def isSome[A](o: A @@ Opt): Boolean = o != null
//  def isEmpty[A](o: A @@ Opt): Boolean = !isSome(o)
//
//  def unsafeGet[A](o: A @@ Opt): A =
//    if (isSome(o)) o.asInstanceOf[A] else sys.error("Cannot get None")
//
//  def fold[A, B](o: A @@ Opt)(ifEmpty: => B)(f: A => B): B =
//    if (o == null) ifEmpty else f(o.asInstanceOf[A])
//}
