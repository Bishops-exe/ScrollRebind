plugins {
    id("dev.kikugie.stonecutter")
}

// Rewritten by the "Set active project to ..." Gradle tasks - don't edit by hand.
stonecutter active "26.1"

// https://stonecutter.kikugie.dev/wiki/config/params
stonecutter parameters {
    dependencies["fapi"] = node.project.property("deps.fabric_api") as String
}