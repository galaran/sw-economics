package me.galaran.swe.data.item

import kotlinx.serialization.Serializable

@Serializable
class Recipe : Item() {

    var isValid = true

    var craftLevel = Int.MIN_VALUE
    lateinit var chance: RecipeChance
    var mpUsage = Int.MIN_VALUE

    val components = mutableListOf<Pair<String, Int>>()
    lateinit var productId: String
    var productQuantity = Int.MIN_VALUE
}

enum class RecipeChance(private val value: Int) {
    PERCENT_100(100),
    PERCENT_70(70),
    PERCENT_60(60),
    PERCENT_25(25);

    companion object {
        fun byValue(value: Int): RecipeChance = values().find { it.value == value }!!
    }
}

@Serializable
class KeyPart : Item() {
    var productId: String? = null
}

@Serializable
class Resource : Item() {
    var isBasic = false
}
