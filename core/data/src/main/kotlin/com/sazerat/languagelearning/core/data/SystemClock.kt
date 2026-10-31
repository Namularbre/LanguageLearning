package com.sazerat.languagelearning.core.data

import com.sazerat.languagelearning.core.domain.model.Clock
import java.time.Instant
import java.time.ZoneId

/** The device's clock and time zone. The zone is read on each call, so a trip abroad is followed. */
class SystemClock : Clock {
    override fun now(): Long = System.currentTimeMillis()

    override fun dayOf(millis: Long): Long =
        Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate().toEpochDay()
}
