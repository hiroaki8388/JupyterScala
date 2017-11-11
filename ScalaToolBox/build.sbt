name := "ScalaToolBox"

version := "1.0"

scalaVersion := "2.12.2"
//enablePlugins(JmhPlugin)


val scalazVersion="7.2.16"

libraryDependencies++=Seq(
"org.scalaz" %% "scalaz-core" % scalazVersion,
"org.scalaz" %% "scalaz-effect" % scalazVersion,
//"org.scalaz" %% "scalaz-typelevel" % scalazVersion,
"org.scalaz" %% "scalaz-scalacheck-binding" % scalazVersion,
  // ScaalikeJDBC
"org.scalikejdbc" %% "scalikejdbc" % "3.0.0",
"ch.qos.logback" % "logback-classic" % "1.2.3",
"com.h2database" % "h2" % "1.4.195")

