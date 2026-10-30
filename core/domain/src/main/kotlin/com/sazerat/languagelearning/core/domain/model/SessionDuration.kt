package com.sazerat.languagelearning.core.domain.model

/** Daily session lengths offered to the user (spec §4). */
public enum class SessionDuration(public val minutes: Int) {
    FIVE(minutes = 5),
    TEN(minutes = 10),
    FIFTEEN(minutes = 15),
    ;

    public companion object {
        public val Default: SessionDuration = FIVE
    }
}
