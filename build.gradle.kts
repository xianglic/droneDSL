import java.util.*

plugins {
  java
  idea
  `java-library`
}

var deps: Properties by rootProject.ext

deps = Properties()
file("gradle/deps.properties").reader().use(deps::load)

allprojects {
  group = "org.steeleagle"
  version = deps.getProperty("version.project")
}

subprojects {
  apply {
    plugin("java")
    plugin("idea")
    plugin("java-library")
  }
  repositories { mavenCentral() }

  val javaVersion = 17
  java {
    withSourcesJar()
    if (hasProperty("release")) withJavadocJar()
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    toolchain {
      languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }
  }

  idea.module {
    outputDir = file("out/production")
    testOutputDir = file("out/test")
  }

  tasks.withType<JavaCompile>().configureEach {
    modularity.inferModulePath.set(true)

    options.apply {
      encoding = "UTF-8"
      isDeprecation = true
      release.set(javaVersion)
      compilerArgs.addAll(listOf("-Xlint:unchecked"))
    }
  }

  tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    enableAssertions = true
  }

  tasks.withType<JavaExec>().configureEach { enableAssertions = true }
}
