package com.github.yarten.aimaclionplugin.core

import com.intellij.openapi.diagnostic.thisLogger
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.jvm.javaio.copyTo
import java.io.File


suspend fun File.downloadFromUrl(url: String) {
    try {
        val client = HttpClient()
        val response = client.get(url)
        this.outputStream().use { output ->
            response.bodyAsChannel().copyTo(output)
        }
        thisLogger().info("Downloaded file: ${this.absolutePath}")
    } catch (e: Exception) {
        thisLogger().warn("Could not download file $url: ${e.message}")
        throw e
    }
}
