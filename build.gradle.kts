plugins {
  java
  idea
  `java-library`
}

allprojects {
  group = "org.droneDSL"
  version = "2.0"
}

subprojects {
  apply {
    plugin("java")
    plugin("idea")
    plugin("java-library")
  }
  repositories { mavenCentral() }

  val javaVersion = 21
  java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
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

  tasks.withType<JavaExec>().configureEach { enableAssertions = true }
}