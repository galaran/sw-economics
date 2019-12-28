package me.galaran.swe.data.parser

import me.galaran.swe.data.item.Recipe
import me.galaran.swe.data.item.RecipeChance

private const val START_LINE = "<br><b>Рецепт(ы):"

private val RECIPE_REGEX = """<b>Уровень (\d), Количество (\d+), шанс (\d{2,3})%, mp (\d+), npc fee \d+ \(<a href="/items/loock/(\d+)/interlude/">Рецепт</a>\)</b><br>""".toRegex()
private val PRODUCT_REGEX = """<br><b><a href="/items/loock/(\d+)/interlude/">.+?</a></b> цена <span id="\d+itmCost" style="color:#0000FF;">0</span>a<br>""".toRegex()
private val COMPONENT_REGEX = """(\d+) <a href="/items/loock/(\d+)/interlude/">.+</a><br>""".toRegex()

fun parseRecipe(lines: List<String>, target: Recipe) {
    val start: Int
    when (lines.count { it == START_LINE }) {
        0 -> {
            target.isValid = false
            target.chance = RecipeChance.PERCENT_100
            target.productId = "<none>"
            return
        }
        1 -> start = lines.indexOf(START_LINE)
        else -> throw IllegalStateException("#${target.id} ${target.name}: Multiple recipe definitions")
    }

    target.productId = PRODUCT_REGEX.find(lines[start + 3])!!.groupValues[1]

    val recipeMatch: MatchResult = RECIPE_REGEX.find(lines[start + 4])!!
    check(recipeMatch.groupValues[5] == target.id)
    target.craftLevel = recipeMatch.groupValues[1].toInt()
    target.productQuantity = recipeMatch.groupValues[2].toInt()
    target.chance = RecipeChance.byValue(recipeMatch.groupValues[3].toInt())
    target.mpUsage = recipeMatch.groupValues[4].toInt()

    var componentIndex = 1
    while (true) {
        val componentStart: IndexedValue<String> = lines.withIndex()
            .filter { it.value.startsWith("""<input id="${target.id}ingr_$componentIndex" type="text" value="""") }
            .filter { it.value.endsWith("> X") }
            .also { check(it.size in 0..1) }
            .firstOrNull()
            ?: break

        val componentMatch: MatchResult = COMPONENT_REGEX.find(lines[componentStart.index + 1])!!
        target.components += Pair(componentMatch.groupValues[2], componentMatch.groupValues[1].toInt())

        componentIndex++
    }

    if (target.components.isEmpty()) {
        target.isValid = false
    }
}
