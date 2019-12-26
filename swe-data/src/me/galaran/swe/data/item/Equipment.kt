package me.galaran.swe.data.item

sealed class Equipment : Item() {
    var crystals: Int? = null

    fun isCrystallizable() = crystals != null && crystals != CRYSTALS_UNKNOWN

    companion object {
        const val CRYSTALS_UNKNOWN = -1
    }
}

class Weapon(val type: WeaponType) : Equipment() {
//    var pAtk: Int by Delegates.notNull()
//    var mAtk: Int by Delegates.notNull()

//    var soulshotUsage: Int by Delegates.notNull()
//    var spiritshotUsage: Int by Delegates.notNull()
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

class Armor(val type: ArmorType) : Equipment() {
//    var pDef: Int by Delegates.notNull()
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

class Jewelry(val type: JewelryType) : Equipment() {
//    var mDef: Int by Delegates.notNull()
}

enum class JewelryType {
    NECKLACE,
    EARRING,
    RING
}
