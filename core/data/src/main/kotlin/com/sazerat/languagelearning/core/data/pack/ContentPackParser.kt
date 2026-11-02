package com.sazerat.languagelearning.core.data.pack

import kotlinx.serialization.json.Json

/** Reads a language pack from its JSON text. Plain Kotlin, so it is unit tested on the JVM. */
object ContentPackParser {
    // Unknown keys are skipped: a newer pipeline may add optional fields without breaking older apps.
    // A change older apps cannot read raises `format` instead.
    private val json = Json { ignoreUnknownKeys = true }

    /** Throws [IllegalArgumentException] when the text is not a pack this app can read. */
    fun parse(text: String): ContentPack {
        val pack = json.decodeFromString<ContentPack>(text)
        require(pack.format == ContentPack.FORMAT) {
            "Pack ${pack.languageCode} has format ${pack.format}, this app reads format ${ContentPack.FORMAT}"
        }
        return pack
    }
}
