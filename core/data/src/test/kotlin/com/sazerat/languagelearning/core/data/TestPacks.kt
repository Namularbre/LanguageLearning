package com.sazerat.languagelearning.core.data

import com.sazerat.languagelearning.core.data.pack.ContentPack
import com.sazerat.languagelearning.core.data.pack.ContentPackParser
import java.io.File

/** The packs shipped in the APK, read from the source tree (unit tests run in the module directory). */
object TestPacks {
    val directory = File("src/main/assets/packs")

    val all: List<ContentPack> by lazy {
        directory.listFiles { file -> file.extension == "json" }.orEmpty().sortedBy { it.name }.map {
            ContentPackParser.parse(it.readText())
        }
    }
}
