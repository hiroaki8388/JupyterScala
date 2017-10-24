name := "PerformanceTurning"

version := "1.0"

scalaVersion := "2.12.2"

enablePlugins(JmhPlugin)


val scalazVersion="7.1.0"
libraryDependencies++=Seq(
"org.scalaz" %% "scalaz-core" % scalazVersion,
"org.scalaz" %% "scalaz-effect" % scalazVersion,
"org.scalaz" %% "scalaz-typelevel" % scalazVersion,
"org.scalaz" %% "scalaz-scalacheck-binding" % scalazVersion)

