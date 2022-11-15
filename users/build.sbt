name := "users"
version := "1.0.0"

scalaVersion := "2.12.3"
scalacOptions ++= Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-Ypartial-unification"
)

resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies ++= Seq(
  "com.softwaremill.quicklens" %% "quicklens" % "1.4.11",
  "org.typelevel"              %% "cats-core" % "1.0.0-MF",
  "com.typesafe.akka"          %% "akka-http" % "10.4.0",
  "com.typesafe.akka"          %% "akka-actor-typed" % "2.6.20",
  "io.circe"                   %% "circe-core"    % "0.14.1",
  compilerPlugin("org.spire-math" %% "kind-projector" % "0.9.4")
)
