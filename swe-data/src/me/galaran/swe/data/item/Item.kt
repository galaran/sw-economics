package me.galaran.swe.data.item

import kotlinx.serialization.Serializable
import kotlin.properties.Delegates

@Serializable
abstract class Item {

    lateinit var id: String
    lateinit var imageId: String
    lateinit var name: String
    var weight: Int by Delegates.notNull()
    var basePrice: Int by Delegates.notNull()

    lateinit var grade: ItemGrade

    fun hasImage() = imageId != ""

    override fun toString() = "Item(id='$id', imageId='$imageId', name='$name', grade=$grade)"
}

class UnclassifiedItem(val rawType: String) : Item()

enum class ItemGrade {
    NOGRADE, D, C, B, A, S
}
