name := "mushroom2"

version := "0.1"

organization := "com.zavakid.mushroom2"

scalaVersion := "2.10.2"

resolvers ++= Seq(
    "snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
    "releases"  at "http://oss.sonatype.org/content/repositories/releases"
    )

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "1.9.2" % "test"
  )

EclipseKeys.withSource := true
