ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "2.13.5"

lazy val catsEffectVersion = "3.3.11"
lazy val fs2Version = "3.2.4"
lazy val betterMonadicForVersion = "0.3.1"
lazy val munitCatsEffectVersion = "1.0.7"

lazy val root = (project in file(".")).settings(
  name := "rover",
  libraryDependencies ++= Seq(
    // "core" module - IO, IOApp, schedulers
    // This pulls in the kernel and std modules automatically.
    "org.typelevel" %% "cats-effect" % catsEffectVersion,

    // concurrency abstractions and primitives (Concurrent, Sync, Async etc.)
    "org.typelevel" %% "cats-effect-kernel" % catsEffectVersion,

    // standard "effect" library (Queues, Console, Random etc.)
    "org.typelevel" %% "cats-effect-std" % catsEffectVersion,

    // Effectful streams
    "co.fs2" %% "fs2-core" % fs2Version,
    "co.fs2" %% "fs2-io" % fs2Version,

    // better monadic for compiler plugin as suggested by documentation
    compilerPlugin(
      "com.olegpy" %% "better-monadic-for" % betterMonadicForVersion
    ),
    "org.typelevel" %% "munit-cats-effect-3" % munitCatsEffectVersion % Test
  )
)
