// Quality tooling the CI workflows depend on. Apply from the app module's
// build.gradle.kts with:
//
//     apply(from = rootProject.file("gradle/quality.gradle.kts"))
//
// or fold these blocks into the module directly. The plugins must also be
// declared in the version catalog and the root build file.
//
// Required version catalog entries:
//   spotless = { id = "com.diffplug.spotless", version = "6.25.0" }
//   detekt   = { id = "io.gitlab.arturbosch.detekt", version = "1.23.7" }

spotless {
    kotlin {
        target("src/**/*.kt")
        ktlint()
        trimTrailingWhitespace()
        endWithNewline()
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint()
    }
    format("xml") {
        target("src/**/res/**/*.xml")
        trimTrailingWhitespace()
        endWithNewline()
    }
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    // A baseline records the debt that exists today so it does not block, while
    // any NEW finding fails the build. Generate it once on an existing codebase:
    //     ./gradlew detektBaseline
    // Do not regenerate it to silence a new finding -- that defeats the point.
    baseline = file("detekt-baseline.xml")
    parallel = true
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    reports {
        xml.required.set(true)
        html.required.set(true)
        sarif.required.set(false)
        md.required.set(false)
    }
}

android {
    lint {
        // Fail CI on lint errors; warnings are reported but do not block.
        abortOnError = true
        warningsAsErrors = false
        checkDependencies = true
        // Missing translations are a shipping defect, not a warning.
        error += listOf("MissingTranslation", "ExtraTranslation")
        htmlReport = true
        xmlReport = true
        // Never disable the Accessibility category.
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}
