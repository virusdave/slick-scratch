import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "virusdave",
      scalaVersion := "2.12.6",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "Slick-Scratch",
    libraryDependencies ++= Seq(
      "com.github.tminglei" %% "slick-pg" % "0.16.3",
      "com.github.tminglei" %% "slick-pg_play-json" % "0.16.3",
      "com.typesafe.slick" %% "slick" % "3.2.3",
      "com.typesafe.slick" %% "slick-codegen" % "3.2.3",
      "com.typesafe.slick" %% "slick-hikaricp" % "3.2.3",
      "org.postgresql" % "postgresql" % "42.2.5",
      "org.slf4j" % "slf4j-nop" % "1.6.4",
      
      scalaTest % Test
    )
  )
