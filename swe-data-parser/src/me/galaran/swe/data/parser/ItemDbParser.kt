package me.galaran.swe.data.parser

import me.galaran.swe.data.ItemDb
import me.galaran.swe.data.item.*
import me.galaran.swe.data.item.ArmorType.*
import me.galaran.swe.data.item.ItemGrade.*
import me.galaran.swe.data.item.JewelryType.*
import me.galaran.swe.data.item.WeaponType.*
import org.w3c.dom.Document
import java.io.ByteArrayInputStream
import java.nio.file.Files
import java.nio.file.Paths
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

private val DOWNLOAD_PATH = Paths.get("swe-data-downloader", "download")

fun main() {
    for (itemId in 1..ItemDb.MAX_ITEM_ID_INTERLUDE) {
        val item: Item? = parseItem(itemId)
        if (item != null) {
            ItemDb.add(item)
        }
    }
    println("===================================================")

    ItemDb.all().filterIsInstance<Jewelry>().forEach(::println)

    /*val json = Json(JsonConfiguration.Stable)
    val jsonItem: String = json.stringify(Item.serializer(), item)
    println(jsonItem)

    val itemAgain: Item = json.parse(Item.serializer(), jsonItem)
    println(itemAgain)*/
}

val propertyKeys = LinkedHashSet<String>()

private const val START_LINE = """<a href="/items/_/_/interlude/">Все вещи</a>"""

fun parseItem(itemId: Int): Item? {
    val group: Int = itemId / 100 * 100
    val lines = Files.readAllLines(DOWNLOAD_PATH.resolve("items/$group/$itemId.html"))

    check(lines.count { it == START_LINE } == 1)

    val start = lines.indexOf(START_LINE)
    check(lines[start + 1] == "<div>")
    lines[start + 2].let {
        if (it != "<h3>") {
            if ("Вещь найдена в хрониках" in it || "Вещь не найдена не в одних из хроник" in it) {
                return null
            } else {
                throw IllegalArgumentException(it)
            }
        }
    }

    val imageMatch: MatchResult? = """<img src="/themes/l2db/images/items/(.*?).png" alt="$itemId">""".toRegex().find(lines[start + 3])
    val imageId = checkNotNull(imageMatch).groupValues[1]

    val itemName = lines[start + 4]

    val properties = parseProperties(lines)

    val itemWeight = properties["Вес"]!!.toInt()
    val item = createTypedItem(properties, itemId, itemName, itemWeight)

    if (item is Recipe) {
        parseRecipe(lines, item)
    }

    return item.apply {
        id = itemId.toString()
        this.imageId = imageId
        name = itemName
        basePrice = properties["Базовая цена"]!!.toInt()
        weight = itemWeight
    }
}

val itemClassRegex = """(\w{1,2})-grade \((\d{1,4})\)""".toRegex()

fun createTypedItem(properties: LinkedHashMap<String, String>, itemId: Int, itemName: String, itemWeight: Int): Item {
    val result: Item = when (val type: String? = properties["Тип"]) {
        "Мечи (sword)" -> Weapon(SWORD)
        "Ударное (blunt)" -> Weapon(BLUNT)
        "Кинжалы (dagger)" -> Weapon(DAGGER)
        "Луки (bow)" -> Weapon(BOW)
        "Копья/пики (pole)" -> Weapon(SPEAR)
        "Магические книги (etc)" -> Weapon(MAGIC_BOOK)
        "Кастеты (fist)" -> Weapon(FISTS)
        "Сдвоенное (dual)" -> Weapon(DUAL_SWORDS)

        "Верх (chest)" -> Armor(BODY_UP)
        "Низ (legs)" -> Armor(BODY_LOW)
        "Цельное (fullbody)" -> Armor(BODY_FULL)
        "Шлемы (head)" -> Armor(HELMET)
        "Рукавицы (gloves)" -> Armor(GLOVES)
        "Ботинки (feet)" -> Armor(BOOTS)
        "Щиты (shield)" -> Armor(SHIELD)
        "back" -> Armor(BACK)
        "auwear" -> Armor(AUWEAR)

        "Ожерелья (neck)" -> Jewelry(NECKLACE)
        "Сережки (ear)" -> Jewelry(EARRING)
        "Кольца (finger)" -> Jewelry(RING)

        "Рецепты (recipe)" -> Recipe()

        "material" -> {
            when (itemWeight) {
                2 -> Resource()
                60 -> KeyPart()
                else -> UnclassifiedItem(type)
            }
        }

        "arrow" -> UnclassifiedItem(type)
        "adena" -> UnclassifiedItem(type)
        "potion" -> UnclassifiedItem(type)
        "misc" -> UnclassifiedItem(type)
        "quest" -> UnclassifiedItem(type)
        "scroll" -> UnclassifiedItem(type)
        "spellbook" -> UnclassifiedItem(type)
        "cryst" -> UnclassifiedItem(type)
        "soulshot" -> UnclassifiedItem(type)
        "pet_collar" -> UnclassifiedItem(type)
        "ticket" -> UnclassifiedItem(type)
        "lotto" -> UnclassifiedItem(type)
        "race_ticket" -> UnclassifiedItem(type)
        "dye" -> UnclassifiedItem(type)
        "seed" -> UnclassifiedItem(type)
        "harvest" -> UnclassifiedItem(type)
        "seven_sign" -> UnclassifiedItem(type)
        "wedding" -> UnclassifiedItem(type)
        "Рыба (fish)" -> UnclassifiedItem(type)
        "rod" -> UnclassifiedItem(type)
        "" -> UnclassifiedItem("<empty>")
        else -> throw IllegalArgumentException()
    }

    val itemClass: String? = properties["Класс"]
    if (itemClass == null) {
        result.grade = NOGRADE
        if (result is Equipment) result.crystals = null
    } else {
        val classMatch: MatchResult = itemClassRegex.find(itemClass)!!
        val grade = classMatch.groupValues[1]
        val crystals = classMatch.groupValues[2].toInt()

        val parsedGrade: ItemGrade = when (grade) {
            "NG" -> NOGRADE
            "D" -> D
            "C" -> C
            "B" -> B
            "A" -> A
            "S" -> S
            else -> throw IllegalArgumentException()
        }
        result.grade = parsedGrade

        if (result is Equipment) {
            result.crystals = when {
                result.grade == NOGRADE -> null
                crystals == 0 -> null
                itemName.startsWith("Shadow Weapon: ") -> null
                crystals in 1..20 -> {
//                    println("#$itemId $itemName: $crystals cry")
                    Equipment.CRYSTALS_UNKNOWN
                }
                else -> crystals
            }
        }
    }

    properties.keys.forEach { propertyKeys += it }

    return result
}

fun parseProperties(lines: List<String>): LinkedHashMap<String, String> {
    val propertyTableStart: Int = lines.indexOf("""<table class="esTable">""")
    check(propertyTableStart >= 0)
    var propertyTableEnd = propertyTableStart
    do {
        propertyTableEnd++
    } while (lines[propertyTableEnd] != "</table>")
    val propertyTable: String = """<?xml version="1.0" encoding="UTF-8"?>""" +
            lines.subList(propertyTableStart, propertyTableEnd + 1).joinToString(separator = "")
    val properties: Document = xmlParser.parse(ByteArrayInputStream(propertyTable.toByteArray()))

    val result = LinkedHashMap<String, String>()

    properties.documentElement.childNodes.let { trList ->
        for (i in 0 until trList.length) {
            val tr = trList.item(i)
            tr.childNodes.let {
                result[it.item(0).textContent] = it.item(1).textContent
            }
        }
    }

//    result.forEach { (k, v) -> println("$k: $v") }

    return result
}

val xmlParser: DocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
