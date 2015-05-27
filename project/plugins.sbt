// ensime-sbt is needed for the integration tests
addSbtPlugin("org.ensime" % "ensime-sbt" % "0.1.6")

// not working on Windows https://github.com/sbt/sbt/issues/1952
//addMavenResolverPlugin

// https://github.com/sbt/sbt-scalariform/issues/20
// the version of org.scalariform will be bumped by ensime-sbt
addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.4.0")

addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "1.0.4")

// scapegoat can be installed per-user: recommended for dev
// addSbtPlugin("com.sksamuel.scapegoat" %% "sbt-scapegoat" % "0.94.5")

addSbtPlugin("org.scoverage" %% "sbt-coveralls" % "1.0.0.BETA1")

scalacOptions in Compile ++= Seq("-feature", "-deprecation")

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.5.0")
