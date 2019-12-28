package me.galaran.swe.data

import me.galaran.swe.data.item.KeyPart
import me.galaran.swe.data.item.Recipe

fun resolveKeyPartProducts() {
    for (key in ItemDb.allIs<KeyPart>()) {
        val firstRecipe = ItemDb.allIs<Recipe>().firstOrNull { rec -> rec.isValid && rec.components.count { it.first == key.id } > 0 }
        key.productId = firstRecipe?.productId
    }
}
