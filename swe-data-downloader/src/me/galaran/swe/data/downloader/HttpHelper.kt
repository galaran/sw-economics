package me.galaran.swe.data.downloader

import java.io.InputStream
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.zip.GZIPInputStream

const val PAGE_RQ_HEADERS = """
    Host: l2db.ru
    User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:70.0) Gecko/20100101 Firefox/70.0
    Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
    Accept-Language: ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3
    Accept-Encoding: gzip, deflate, br
    Connection: keep-alive
    Cookie: chonicle=interlude; PHPSESSID=tbc8oafanr2i9dh231jmv15845
    Upgrade-Insecure-Requests: 1
    Cache-Control: max-age=0"""

const val IMAGE_RQ_HEADERS = """
    Host: l2db.ru
    User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:70.0) Gecko/20100101 Firefox/70.0
    Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
    Accept-Language: ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3
    Accept-Encoding: gzip, deflate, br
    Connection: keep-alive
    Cookie: chonicle=interlude; PHPSESSID=tbc8oafanr2i9dh231jmv15845
    Upgrade-Insecure-Requests: 1
    Pragma: no-cache
    Cache-Control: no-cache"""

fun createHttpClient(): HttpClient {
    System.setProperty("jdk.httpclient.allowRestrictedHeaders", "Host,Connection")
    return HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NEVER).build()
}

fun HttpRequest.Builder.setupHeaders(headers: String): HttpRequest.Builder {
    headers.split('\n')
        .map { it.trim() }
        .filterNot { it.isEmpty() }
        .map { it.split(": ") }
        .forEach { this.setHeader(it[0], it[1]) }
    return this
}

fun checkStatusCode(rs: HttpResponse<*>, errorText: String) {
    if (rs.statusCode() != 200) {
        throw IllegalStateException("$errorText, statusCode=${rs.statusCode()}")
    }
}

fun encureContentUTF8(rs: HttpResponse<*>) {
    val contentType: String = rs.headers().firstValue("Content-Type").orElse("")
    if (!contentType.contains("charset=utf-8")) {
        throw IllegalArgumentException("Not UTF-8!: $contentType")
    }
}

fun HttpResponse<InputStream>.readAllBytes(): ByteArray {
    when (this.headers().firstValue("Content-Encoding").orElse(null)) {
        "gzip" -> GZIPInputStream(this.body())
        else -> this.body()
    }.use {
        return it.readAllBytes()
    }
}
