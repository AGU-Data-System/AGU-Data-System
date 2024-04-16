import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.2.3"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.22"
	kotlin("plugin.spring") version "1.9.22"
	kotlin("plugin.serialization") version "1.9.22"
}

group = "AGUDataSystem"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	// for coroutines
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")

	// for JDBI
	implementation("org.jdbi:jdbi3-core:3.45.1")
	implementation("org.jdbi:jdbi3-kotlin:3.45.1")
	implementation("org.jdbi:jdbi3-postgres:3.45.1")
	implementation("org.postgresql:postgresql:42.7.3")

	// for Web-Scraping
	implementation("org.jsoup:jsoup:1.17.2")

	// for JSON serialization
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

	testImplementation("org.springframework.boot:spring-boot-starter-test")

	// To use WebTestClient on tests
	testImplementation("org.springframework.boot:spring-boot-starter-webflux")
	testImplementation(kotlin("test"))
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
