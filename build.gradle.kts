import me.modmuss50.mpp.ReleaseType
import java.net.HttpURLConnection
import java.net.URI
import java.security.MessageDigest

plugins {
    // Applies the correct Loom variant for the active Minecraft version
    id("dev.kikugie.loom-back-compat")
    id("me.modmuss50.mod-publish-plugin")
}

// Do NOT set group here - Stonecutter's version subprojects manage it.
version = "${property("mod.version")}+${sc.current.version}"
base.archivesName = property("mod.name") as String

val requiredJava: JavaVersion = when {
    sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
    else -> JavaVersion.VERSION_21
}

// Blank means "don't publish to Modrinth" - see stonecutter.properties.toml
val modrinthId: String = findProperty("publish.modrinth_id")?.toString()?.trim().orEmpty()

fun nodeProperty(key: String): String = findProperty(key)?.toString()?.trim()?.takeIf { it.isNotEmpty() }
    ?: error("Missing `$key` for ${sc.current.version} - add it in stonecutter.properties.toml")

// The inclusive Minecraft range this node's jar covers, and the single source of truth
// for both what Loader enforces and what Modrinth is told.
val mcRangeStart: String = nodeProperty("publish.mc_range_start")
val mcRangeEnd: String = nodeProperty("publish.mc_range_end")

// Written into fabric.mod.json's `depends.minecraft`
val mcCompat: String = if (mcRangeStart == mcRangeEnd) mcRangeStart else ">=$mcRangeStart <=$mcRangeEnd"

dependencies {
    minecraft("com.mojang:minecraft:${sc.current.version}")
    // Mojang mappings on every version, so class/member names match across 1.21.9 and 26.1
    loomx.applyMojangMappings()

    // Use `mod{dependency type}` even on 26.1+ - loom-back-compat converts them
    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
}

loom {
    decompilerOptions.named("vineflower") {
        options.put("mark-corresponding-synthetics", "1") // Names lambdas - useful for mixin targets
    }

    runConfigs.all {
        preferGradleTask = true
        generateRunConfig = true
        jvmArguments.add("-Dmixin.debug.export=true")
    }
}

java {
    targetCompatibility = requiredJava
    sourceCompatibility = requiredJava

    toolchain {
        vendor = JvmVendorSpec.ADOPTIUM
        languageVersion = JavaLanguageVersion.of(requiredJava.majorVersion)
    }
}

tasks {
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    processResources {
        fun MutableMap<String, String>.register(key: String, value: String) {
            inputs.property(key, value)
            set(key, value)
        }

        val props = buildMap {
            register("id", sc.properties["mod.id"])
            register("name", sc.properties["mod.name"])
            register("version", project.version.toString())
            register("minecraft", mcCompat)
            register("loader", Regex("\\d+\\.\\d+").find(sc.properties.get<String>("deps.fabric_loader"))!!.value)
        }

        filesMatching("fabric.mod.json") { expand(props) }
        filesMatching("*.mixins.json") { expand("java" to "JAVA_${requiredJava.majorVersion}") }
    }

    withType<Jar> {
        val name = project.property("mod.id")
        inputs.property("mod_id", name)
        from(rootProject.file("LICENSE.txt")) { rename { "${it}_$name" } }
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        description = "Builds mod jars and copies results to `build/libs/{mod version}/`"

        inputs.property("version", project.property("mod.version"))
        from(loomx.modJar.flatMap { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
    }
}

publishMods {
    file = loomx.modJar.flatMap { it.archiveFile }

    // project.version is "{mod version}+{mc version}", which keeps each upload distinct
    displayName = "${property("mod.name")} ${property("mod.version")} for ${sc.current.version}"
    changelog = providers.environmentVariable("CHANGELOG").orElse("No changelog provided.")
    type = ReleaseType.STABLE
    modLoaders.addAll("fabric", "quilt")
    dryRun = providers.environmentVariable("PUBLISH_DRY_RUN").map(String::toBoolean).orElse(false)

    if (modrinthId.isNotEmpty()) {
        modrinth {
            accessToken = providers.environmentVariable("MODRINTH_TOKEN")
            projectId = modrinthId

            // Expanded against Mojang's manifest when publishing
            minecraftVersionRange {
                start = mcRangeStart
                end = mcRangeEnd
            }

            requires("fabric-api")
        }
    }
}

// Skip the upload when Modrinth already holds a file with this exact content, so
// re-running a publish (after a partial failure, say) doesn't create duplicate versions.
// Loom builds jars reproducibly, so an unchanged source tree yields an unchanged hash.
if (modrinthId.isNotEmpty()) {
    tasks.named("publishModrinth") {
        val jarFile = loomx.modJar.flatMap { it.archiveFile }
        val projectId = modrinthId

        onlyIf {
            val file = jarFile.get().asFile
            val sha512 = MessageDigest.getInstance("SHA-512")
                .digest(file.readBytes())
                .joinToString("") { "%02x".format(it.toInt() and 0xFF) }

            // 200 means some Modrinth version already has this file; 404 means it's new.
            // Network trouble returns null, and we publish rather than silently skipping.
            val existing = runCatching {
                val connection = URI("https://api.modrinth.com/v2/version_file/$sha512?algorithm=sha512")
                    .toURL()
                    .openConnection() as HttpURLConnection
                connection.setRequestProperty("User-Agent", "ScrollRebind/publish (duplicate check)")
                connection.connectTimeout = 30_000
                connection.readTimeout = 30_000

                if (connection.responseCode == 200) {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } else {
                    null
                }
            }.getOrNull()

            // Confirm the match belongs to this project before trusting it
            val alreadyPublished = existing != null &&
                Regex(""""project_id"\s*:\s*"$projectId"""").containsMatchIn(existing)

            if (alreadyPublished) {
                logger.lifecycle("Modrinth already has ${file.name} (sha512 match) - skipping upload")
            }

            !alreadyPublished
        }
    }
}