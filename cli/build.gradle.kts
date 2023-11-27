plugins { application }
application.mainClass.set("org.steeleagle.Main")

dependencies {
  api(libs.picocli)
  implementation(libs.aya.tools)
  implementation(libs.aya.ipcore)
  implementation(libs.aya.ipwrapper)
  implementation(project(":preprocess"))
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
  outputDir = genDir.resolve("org/steeleagle/prelude")
}
listOf(tasks.sourcesJar, tasks.compileJava).forEach { it.configure { dependsOn(genVer, lexer) } }
