package com.liviolopez.citycrimes.utils

import android.webkit.URLUtil

/**
 * API return videos only on .mp4
 * - Contain only alphanumeric characters, whitespace, dot, hyphens and underscore
 * - has .mp4 extension
 * - can have parameters after extension
 */
object StringUtils {
    fun isVideoUrl(url: String) : Boolean {
      return URLUtil.guessFileName(url, null, null).split(".").last() == "mp4"
    }
}