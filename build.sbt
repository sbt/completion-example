organization := "org.scala-sbt"

name := "Completion Demo"

version := "0.1"

scalaVersion := "2.9.2"

crossPaths := false

libraryDependencies += "org.scala-sbt" % "command" % "0.12.0-Beta2"

resolvers <+= sbtResolver

initialCommands := """import sbt.complete._
import DefaultParsers._
"""
