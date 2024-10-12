plugins {
    kotlin("jvm")
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.symbol.processing.api)
    implementation(project(":localProvider:annotations"))
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}
