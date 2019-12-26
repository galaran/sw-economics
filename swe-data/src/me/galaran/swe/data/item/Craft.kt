package me.galaran.swe.data.item

import kotlin.properties.Delegates

class Recipe : Item() {

    val components = mutableListOf<Pair<Item, Int>>()

    lateinit var product: Item
    var productQuantity = 1

    lateinit var chance: RecipeChance
}

enum class RecipeChance {
    PERCENT_100, PERCENT_70, PERCENT_60,
}

class KeyPart : Item() {

    lateinit var product: Equipment
}

class Resource : Item() {

    var isBasic: Boolean by Delegates.notNull()
}
