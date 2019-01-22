package net.ascheja.rokkstar.parser

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class LastNameDelegate(internal var value: String? = null): ReadWriteProperty<BaseParser, String?> {

    override operator fun setValue(thisRef: BaseParser, property: KProperty<*>, value: String?) {
        this.value = value
    }

    override operator fun getValue(thisRef: BaseParser, property: KProperty<*>): String? {
        return value
    }
}