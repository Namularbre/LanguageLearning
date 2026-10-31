package com.sazerat.languagelearning.core.domain.model

/**
 * Current time, behind an interface so tests can fix it. Times are epoch milliseconds and days are
 * epoch days (days since 1970-01-01) in the device's time zone: the domain stays free of any date
 * library, and only the implementation in `:core:data` knows about time zones.
 */
public interface Clock {
    /** Now, in epoch milliseconds. */
    public fun now(): Long

    /** The local day of [millis], as an epoch day. */
    public fun dayOf(millis: Long): Long

    /** Today, as an epoch day. */
    public fun today(): Long = dayOf(now())
}

public const val MILLIS_PER_MINUTE: Long = 60_000L
public const val MILLIS_PER_DAY: Long = 24 * 60 * MILLIS_PER_MINUTE
