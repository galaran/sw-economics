package me.galaran.swe.data.item

import kotlin.properties.Delegates

class Recipe : Item() {

    var isValid = true

    var level by Delegates.notNull<Int>()
    lateinit var chance: RecipeChance
    var mpUsage by Delegates.notNull<Int>()

    val components = mutableListOf<Pair<String, Int>>()
    lateinit var productId: String
    var productQuantity by Delegates.notNull<Int>()
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

class KeyPart : Item() {

    lateinit var productId: String
}

class Resource : Item() {

    var isBasic: Boolean by Delegates.notNull()
}
