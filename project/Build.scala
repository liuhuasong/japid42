import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "japid42"
  val appVersion      = "0.8.1"

  val appDependencies = Seq(
    javaCore
    ,"org.apache.commons" % "commons-email" % "1.2"
    ,"commons-lang" % "commons-lang" % "2.6"
    ,"org.eclipse.tycho" % "org.eclipse.jdt.core" % "3.8.2.v20120814-155456"
    ,"com.google.code.javaparser" % "javaparser" % "1.0.8"
  )

  val main = play.Project(
    appName, appVersion, appDependencies
  )


  /*
  publishTo := Some(Resolver.file("file",  new File( "~bran/projects/branaway.github.com/releases" )) )
  publishTo <<= version { (v: String) =>
	  val nexus = "https://oss.sonatype.org/"
	  if (v.trim.endsWith("SNAPSHOT")) 
	    Some("snapshots" at nexus + "content/repositories/snapshots")
	  else                             
	    Some("releases" at nexus + "service/local/staging/deploy/maven2")
  }
  
  organization := "com.github.branaway"

	publishMavenStyle := true
	
	publishArtifact in Test := false
	
	pomIncludeRepository := { x => false }
	
	pomExtra := (
		  <url>http://branaway.github.com/japid42.hrml</url>
		  <licenses>
    <license>
      <name>BSD-style</name>
      <url>http://www.opensource.org/licenses/bsd-license.php</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
		  <scm>
    <url>git@github.com:branaway/japid42.git</url>
    <connection>scm:git:git@github.com:branaway/japid42.git</connection>
  </scm>
		  <developers>
    <developer>
      <id>branaway</id>
      <name>Bing Ran</name>
      <url>http://iclass.com</url>
    </developer>
  </developers>
	)
*/
}
