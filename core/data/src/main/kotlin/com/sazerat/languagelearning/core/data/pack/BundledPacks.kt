package com.sazerat.languagelearning.core.data.pack

import android.content.res.AssetManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * The language packs shipped in the APK (`src/main/assets/packs/`), so the app has content on its
 * first launch, offline (spec §6.2). From M2, packs downloaded from the API are stored in Room and
 * these are only the starting point.
 */
class BundledPacks(private val assets: AssetManager) {
    // Read once, on first use, then kept in memory: a few hundred kilobytes in all.
    private val packs: List<ContentPack> by lazy {
        assets.list(DIRECTORY).orEmpty().filter { it.endsWith(".json") }.sorted().map { file ->
            ContentPackParser.parse(assets.open("$DIRECTORY/$file").bufferedReader().use { it.readText() })
        }
    }

    /** Every bundled pack. Reading the files happens off the main thread. */
    val all: Flow<List<ContentPack>> = flow { emit(packs) }.flowOn(Dispatchers.IO)

    private companion object {
        const val DIRECTORY = "packs"
    }
}
