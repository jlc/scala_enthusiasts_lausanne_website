import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "scala_enthusiasts_lausanne"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
    "com.shorrockin" %% "cascal" % "1.3-SNAPSHOT" changing(),
    "net.sf.uadetector" % "uadetector-resources" % "2013.02"
    // Alternate cassandra scala client: cassie (not yet scala 2.10 compatible)
    /*
    "com.twitter" %% "finagle-core" % "6.2.0",
    "com.twitter" % "cassie" % "0.20.0"
      exclude("javax.jms", "jms")
      exclude("com.sun.jdmk", "jmxtools")
      exclude("com.sun.jmx", "jmxri")
     */
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    resolvers += (
      "Local Maven Repository" at "file:///"+Path.userHome+"/.m2/repository"
    )
 
  )

}
