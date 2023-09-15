plugins { application }
application.mainClass.set("org.steeleagle.Main")

dependencies {
  val deps: java.util.Properties by rootProject.ext
  api("info.picocli", "picocli", version = deps.getProperty("version.picocli"))
  implementation("org.aya-prover", "tools", version = deps.getProperty("version.aya"))
  implementation("org.aya-prover.upstream", "ij-parsing-core", version = deps.getProperty("version.build-util"))
  implementation("org.aya-prover.upstream", "ij-parsing-wrapper", version = deps.getProperty("version.build-util"))
}

val genDir = file("src/main/gen")
sourceSets["main"].java.srcDir(genDir)
idea.module {
  sourceDirs.add(genDir)
}

val lexer = tasks.register<JFlexTask>("lexer") {
  outputDir = genDir.resolve("org/steeleagle/parser")
  jflex = file("src/main/grammar/BotPsiLexer.flex")
}

val genVer = tasks.register<GenerateVersionTask>("genVer") {
  basePackage = "org.steeleagle"
  outputDir = file(genDir).resolve("org/steeleagle/prelude")
}
listOf(tasks.sourcesJar, tasks.compileJava).forEach { it.configure { dependsOn(genVer, lexer) } }
