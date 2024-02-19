package kiradk.client.utils

import kotlin.reflect.KProperty

@KiraDKExperimental
public object LazyTODO {

    public operator fun <T> getValue(thisRef: Any?, property: KProperty<*>): T = TODO()

    public operator fun <T> setValue(thisRef: Any?, property: KProperty<*>, value: T): Unit = TODO()
}
