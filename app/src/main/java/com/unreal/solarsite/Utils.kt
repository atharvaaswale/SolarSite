package com.unreal.solarsite


fun String.toCustomSentenceCase(): String {
    return this.replace("_", " ")
        .lowercase()
        .replaceFirstChar { it.uppercase() }
}