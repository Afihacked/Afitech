plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.afitech"
    compileSdk {
        version = release(36)
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    val appVersionName = "0.0.16"

    defaultConfig {
        applicationId = "com.afitech"
        minSdk = 27
        targetSdk = 36

        versionName = appVersionName

        versionCode = appVersionName
            .substringAfterLast(".")
            .toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    signingConfigs {

        create("release") {

            storeFile = file(
                project.properties["STORE_FILE"]?.toString()
                    ?: ""
            )

            storePassword =
                project.properties["STORE_PASSWORD"]
                    ?.toString()
                    ?: ""

            keyAlias =
                project.properties["KEY_ALIAS"]
                    ?.toString()
                    ?: ""

            keyPassword =
                project.properties["KEY_PASSWORD"]
                    ?.toString()
                    ?: ""
        }
    }
    buildTypes {

        release {

            isMinifyEnabled = true

            isShrinkResources = true

            signingConfig =
                signingConfigs.getByName(
                    "release"
                )

            proguardFiles(

                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),

                "proguard-rules.pro"
            )
        }

        applicationVariants.all {

            val appName =
                "AfiTech"

            val vName =
                versionName

            outputs.all {

                (
                        this as
                                com.android.build.gradle.internal.api.BaseVariantOutputImpl
                        )

                    .outputFileName =

                    "${appName}_v${vName}.apk"
            }
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
        kotlinOptions {
            jvmTarget = "11"
        }
    }

    dependencies {
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.appcompat)
        implementation(libs.material)
        implementation(libs.androidx.activity)
        implementation(libs.androidx.constraintlayout)
        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)

        implementation(libs.room.runtime)
        implementation(libs.room.ktx)

        ksp(libs.room.compiler)

        implementation("androidx.navigation:navigation-fragment-ktx:2.9.0")
        implementation("androidx.navigation:navigation-ui-ktx:2.9.0")
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.1")
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.1")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
        implementation("com.github.bumptech.glide:glide:4.16.0")
        implementation(
            "androidx.swiperefreshlayout:swiperefreshlayout:1.1.0"
        )
        implementation("androidx.media3:media3-exoplayer:1.8.0")
        implementation("androidx.media3:media3-ui:1.8.0")
        implementation("io.coil-kt:coil:2.7.0")
    }
}
dependencies {
    implementation(libs.androidx.documentfile)
}
