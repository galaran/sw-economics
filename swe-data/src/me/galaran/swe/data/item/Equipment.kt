package me.galaran.swe.data.item

import kotlinx.serialization.Serializable

@Serializable
sealed class Equipment : Item() {
    var crystals: Int = CRYSTALS_NON_CRYSTALLIZABLE

    fun isCrystallizable() = crystals > 0

    companion object {
        const val CRYSTALS_NON_CRYSTALLIZABLE = 0
        const val CRYSTALS_INVALID = -1
    }
}

@Serializable
class Weapon(val type: WeaponType) : Equipment() {
//    var pAtk: Int
//    var mAtk: Int

//    var soulshotUsage: Int
//    var spiritshotUsage: Int
}

enum class WeaponType {
    BLUNT,
    //TWO_HANDED_BLUNT,
    BOW,
    DAGGER,
    DUAL_SWORDS,
    FISTS,
    MAGIC_BOOK,
    SPEAR,
    SWORD
    //TWO_HANDED_SWORD
}

@Serializable
class Armor(val type: ArmorType) : Equipment() {
//    var pDef: Int
}

enum class ArmorType {
    HELMET,
    BODY_UP,
    BODY_LOW,
    BODY_FULL,
    GLOVES,
    BOOTS,
    SHIELD,
    BACK,
    AUWEAR
}

@Serializable
class Jewelry(val type: JewelryType) : Equipment() {
//    var mDef: Int
}

enum class JewelryType {
    NECKLACE,
    EARRING,
    RING
}
