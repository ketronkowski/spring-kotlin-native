import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.0.0-RC1"
	id("io.spring.dependency-management") version "1.1.0"
	id("org.graalvm.buildtools.native") version "0.9.16"
	kotlin("jvm") version "1.7.20"
	kotlin("plugin.spring") version "1.7.20"
	kotlin("plugin.serialization") version "1.7.20"
}

group = "org.test"
version = "0.0.1-SNAPSHOT"

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	runtimeOnly("com.h2database:h2")
	runtimeOnly("io.r2dbc:r2dbc-h2")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}


java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(17))
		vendor.set(JvmVendorSpec.GRAAL_VM)
	}
}


tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootBuildImage> {
	environment.put("BP_NATIVE_IMAGE_BUILD_ARGUMENTS","--initialize-at-build-time=ch.qos.logback")

}


//springBoot {
//	buildInfo {
//		properties {
//			additional.set(mapOf("BP_NATIVE_IMAGE_BUILD_ARGUMENTS" to "--initialize-at-build-time=ch.qos.logback.classic.Logger"))
//		}
//	}
//}
