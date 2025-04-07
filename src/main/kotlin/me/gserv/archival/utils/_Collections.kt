package me.gserv.archival.utils

inline fun <T> forEach(vararg items: T, callback: (T) -> Unit) {
	items.forEach(callback)
}
