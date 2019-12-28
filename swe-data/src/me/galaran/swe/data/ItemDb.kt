package me.galaran.swe.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.modules.SerializersModule
import me.galaran.swe.data.item.*
import java.nio.file.Files
import java.nio.file.Path

object ItemDb {

    const val MAX_ITEM_ID_INTERLUDE = 9161

    @Serializable
    private class DB {
        val byId = LinkedHashMap<String, Item>(MAX_ITEM_ID_INTERLUDE)
    }

    private var db = DB()

    fun all(): Collection<Item> = db.byId.values
    inline fun <reified T> allIs(): List<T> = all().filterIsInstance<T>()

    fun add(item: Item) {
        db.byId[item.id] = item
    }

    fun applyFixes() {
        resolveKeyPartProducts()
    }

    fun saveTo(dbFile: Path) {
        Files.write(dbFile, serializer.stringify(DB.serializer(), db).toByteArray())
    }

    fun loadFrom(dbFile: Path) {
        db = serializer.parse(DB.serializer(), Files.readAllBytes(dbFile).toString(Charsets.UTF_8))
    }

    @Suppress("EXPERIMENTAL_API_USAGE")
    private val serializer = Json(JsonConfiguration(prettyPrint = true, useArrayPolymorphism = true), SerializersModule {
        polymorphic(Item::class) {
            UnclassifiedItem::class with UnclassifiedItem.serializer()
            Recipe::class with Recipe.serializer()
            KeyPart::class with KeyPart.serializer()
            Resource::class with Resource.serializer()
            Weapon::class with Weapon.serializer()
            Armor::class with Armor.serializer()
            Jewelry::class with Jewelry.serializer()
        }
    })
}
