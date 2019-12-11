package me.galaran.swe.data.downloader

import me.galaran.swe.data.downloader.ImageDownloadResult.*
import java.io.InputStream
import java.io.PrintWriter
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption.*
import java.time.Duration

const val MAX_ITEM_ID_INTERLUDE = 9161

const val ITEM_PAGE_URL_PATTERN = "https://l2db.ru/items/loock/%s/interlude"

const val ITEM_IMAGE_PATH = "/themes/l2db/images/items/"
const val ITEM_IMAGE_URL_PATTERN = "http://l2db.ru$ITEM_IMAGE_PATH%s"

val httpClient: HttpClient = createHttpClient()

val downloadDir: Path = Paths.get("db-parser", "download")

fun main() {
    if (Files.exists(downloadDir)) throw IllegalStateException("${downloadDir.toAbsolutePath()} already exists")

    for (itemId in 1..MAX_ITEM_ID_INTERLUDE) {
        try {
            val itemDescr: ItemDescr? = downloadItemPage(itemId)
            if (itemDescr != null) {
                val imageDownloadResult = downloadItemImage(itemDescr.imageName)
                log("$imageDownloadResult :: ${itemDescr.imageName}")
            } else {
                log("Invalid item: $itemId")
            }
            Thread.sleep(400)
        } catch (ex: Exception) {
            log("Error downloading item with id=$itemId")
            Files.newBufferedWriter(logFile, Charsets.UTF_8, CREATE, WRITE, APPEND).use {
                ex.printStackTrace(PrintWriter(it))
            }
            ex.printStackTrace()
        }
    }
}



data class ItemDescr(val imageName: String)

enum class ImageDownloadResult {
    DOWNLOADED, ALREADY_EXISTS, NOT_FOUND_404
}

fun downloadItemPage(itemId: Int): ItemDescr? {
    val rq: HttpRequest = HttpRequest.newBuilder()
        .uri(URI(ITEM_PAGE_URL_PATTERN.format(itemId.toString())))
        .GET()
        .setupHeaders(PAGE_RQ_HEADERS)
        .version(HttpClient.Version.HTTP_1_1)
        .timeout(Duration.ofSeconds(20))
        .build()
    val rs: HttpResponse<InputStream> = httpClient.send(rq, HttpResponse.BodyHandlers.ofInputStream())

    checkStatusCode(rs, "ItemID=$itemId")
    encureContentUTF8(rs)

    val bytes: ByteArray = rs.readAllBytes()
    saveItemPage(itemId, bytes)
    log("${rq.uri()} DONE ======> Size=${bytes.size} bytes | ${rs.headers().map()}")

    val html = String(bytes, Charsets.UTF_8)
    val itemMatch: MatchResult? = "<img src=\"$ITEM_IMAGE_PATH(.+?)\" alt=\"$itemId\">".toRegex().find(html)
    return if (itemMatch != null) ItemDescr(itemMatch.groupValues[1]) else null
}

fun saveItemPage(itemId: Int, bytes: ByteArray) {
    val group: Int = itemId / 100 * 100
    val groupDir = downloadDir.resolve("items/$group")
    Files.createDirectories(groupDir)

    Files.write(groupDir.resolve("$itemId.html"), bytes, CREATE_NEW)
}

fun downloadItemImage(imageName: String): ImageDownloadResult {
    val imagesDir = downloadDir.resolve("item-images")
    Files.createDirectories(imagesDir)
    if (Files.exists(imagesDir.resolve(imageName))) return ALREADY_EXISTS

    val rq: HttpRequest = HttpRequest.newBuilder()
        .uri(URI(ITEM_IMAGE_URL_PATTERN.format(imageName)))
        .GET()
        .setupHeaders(IMAGE_RQ_HEADERS)
        .version(HttpClient.Version.HTTP_1_1)
        .timeout(Duration.ofSeconds(20))
        .build()
    val rs: HttpResponse<InputStream> = httpClient.send(rq, HttpResponse.BodyHandlers.ofInputStream())

    if (rs.statusCode() == 404) return NOT_FOUND_404
    checkStatusCode(rs, "ItemImage=$imageName")

    val bytes: ByteArray = rs.readAllBytes()

    Files.write(imagesDir.resolve(imageName), bytes, CREATE_NEW)
    return DOWNLOADED
}

private val logFile = downloadDir.resolve("log.txt")

fun log(string: String) {
    Files.writeString(logFile, string + System.lineSeparator(), Charsets.UTF_8, CREATE, WRITE, APPEND)
    println(string)
}
