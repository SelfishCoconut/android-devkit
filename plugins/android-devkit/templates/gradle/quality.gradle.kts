// Quality tooling the CI workflows depend on.
//
// COPY THESE BLOCKS INTO THE APP MODULE'S build.gradle.kts. Do NOT wire this
// file up with `apply(from = ...)`: the Kotlin DSL cannot resolve typed
// accessors (`spotless { }`, `detekt { }`) in an applied script, so it fails
// to compile. This file is a reference to copy from, not a script to apply.
//
// Also required:
//   - version catalog entries and `apply false` aliases in the root build file
//       spotless = { id = "com.diffplug.spotless", version = "6.25.0" }
//       detekt   = { id = "io.gitlab.arturbosch.detekt", version = "1.23.7" }
//   - the plugin aliases applied in the app module's `plugins { }` block
//
// NOTE on ktlint config: Spotless does NOT read .editorconfig for its embedded
// ktlint step. Pass rules via .editorConfigOverride(...) as below, or they are
// silently ignored. Keep .editorconfig in sync for the IDE's benefit.

val ktlintRules = mapOf(
    // Composables are PascalCase by Compose API convention, not a violation.
    // Without this, every @Composable fails ktlint.
    "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
    // ktlint cannot auto-correct line length, so leaving it enabled makes
    // spotlessApply *fail* rather than format. Let detekt own this rule:
    // its baseline records existing long lines, so new ones still fail CI.
    "ktlint_standard_max-line-length" to "disabled",
    // Misreads Compose slot APIs (trailing content lambda after a modifier).
    "ktlint_standard_function-signature" to "disabled",
)

spotless {
    kotlin {
        target("src/**/*.kt")
        ktlint("1.3.1").editorConfigOverride(ktlintRules)
        trimTrailingWhitespace()
        endWithNewline()
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint("1.3.1").editorConfigOverride(ktlintRules)
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
        // Same contract as the detekt baseline. Note that the run which CREATES
        // a lint baseline always fails by design -- re-run once and it passes.
        baseline = file("lint-baseline.xml")
        // Never disable the Accessibility category.
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}
