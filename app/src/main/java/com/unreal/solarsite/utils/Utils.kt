package com.unreal.solarsite.utils


fun String.toCustomSentenceCase(): String {
    return this.replace("_", " ")
        .lowercase()
        .replaceFirstChar { it.uppercase() }
}