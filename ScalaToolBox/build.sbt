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
  "org.scalikejdbc" %% "scalikejdbc-config" % "3.0.0",
"com.h2database" % "h2" % "1.4.195",
// Akka
//"org.json4s" %% "json4s-native" % "3.6.0",
"org.scalaj" %% "scalaj-http" % "2.3.0",
"com.typesafe.akka" %% "akka-actor" % "2.5.6"
)

