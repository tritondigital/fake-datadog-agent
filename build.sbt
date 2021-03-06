name := "fake-datadog-agent"

version := "1.3.1-SNAPSHOT"

scalaVersion := "2.12.4"

organization := "com.tritondigital"

organizationHomepage := Some(new URL("http://www.tritondigital.com"))

description := "A fake Datadog agent useful for testing"

homepage := Some(new URL("https://github.com/tritondigital/fake-datadog-agent"))

startYear := Some(2015)

licenses := Seq("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))

crossScalaVersions := Seq("2.10.7", "2.11.12", "2.12.4")

publishMavenStyle := true

sonatypeProfileName := "com.tritondigital"

ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

libraryDependencies ++= Seq(
  "com.datadoghq" % "java-dogstatsd-client" % "2.3" % "test",
  "org.hamcrest" % "hamcrest-all" % "1.3" % "compile, optional",
  "org.scalatest" %% "scalatest" % "3.0.4" % "compile, optional"
)

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

credentials += Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", System.getenv("SONATYPE_USERNAME"), System.getenv("SONATYPE_PASSWORD"))

pomIncludeRepository := { _ => false }

pomExtra :=
  <scm>
    <url>git@github.com:tritondigital/fake-datadog-agent.git</url>
    <connection>scm:git:git@github.com:tritondigital/fake-datadog-agent.git</connection>
  </scm>
  <developers>
    <developer>
      <id>plveilleux</id>
      <name>Pierre-Luc Veilleux</name>
    </developer>
    <developer>
      <id>mmclean</id>
      <name>Mike McLean</name>
    </developer>
  </developers>
