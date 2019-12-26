package me.galaran.swe.data

import me.galaran.swe.data.item.Item

object ItemDb {

    const val MAX_ITEM_ID_INTERLUDE = 9161

    private val list = ArrayList<Item>(MAX_ITEM_ID_INTERLUDE)
    private val byId = LinkedHashMap<String, Item>(MAX_ITEM_ID_INTERLUDE)
    private val byName = LinkedHashMap<String, Item>(MAX_ITEM_ID_INTERLUDE)

    fun add(item: Item) {
        list += item
        byId[item.id] = item
        byName[item.name] = item
    }

    fun all(): List<Item> = list
}
