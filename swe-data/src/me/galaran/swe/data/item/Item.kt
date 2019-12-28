package me.galaran.swe.data.item

import kotlinx.serialization.Serializable

@Serializable
abstract class Item {

    lateinit var id: String
    lateinit var imageId: String
    lateinit var name: String
    var weight = Int.MIN_VALUE
    var basePrice = Int.MIN_VALUE

    lateinit var grade: ItemGrade

    fun hasImage() = imageId.isNotEmpty()

    override fun toString() = "Item(id='$id', imageId='$imageId', name='$name', grade=$grade)"

    override fun equals(other: Any?) = this === other || other != null && javaClass == other.javaClass && id == (other as Item).id
    override fun hashCode() = id.hashCode()
}

@Serializable
class UnclassifiedItem(val rawType: String) : Item()

enum class ItemGrade {
    NOGRADE, D, C, B, A, S
}
