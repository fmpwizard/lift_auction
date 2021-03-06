import sbt._

class LiftProject(info: ProjectInfo) extends DefaultWebProject(info) {
  val liftVersion = property[Version]

  // uncomment the following if you want to use the snapshot repo
  //  val scalatoolsSnapshot = ScalaToolsSnapshots

  // If you're using JRebel for Lift development, uncomment
  // this line
  //override def scanDirectories = Nil

  lazy val JavaNet = "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"

  override def libraryDependencies = Set(
    "fmpwizard" %% "lift-named-comet" % "0.1" % "compile",
    "net.liftweb" %% "lift-webkit" % liftVersion.value.toString % "compile",
    "net.liftweb" %% "lift-mapper" % liftVersion.value.toString % "compile",
    "org.mortbay.jetty" % "jetty" % "6.1.26" % "test",
    "ch.qos.logback" % "logback-classic" % "0.9.26",
    "com.h2database" % "h2" % "1.2.147"
  ) ++ super.libraryDependencies
}
