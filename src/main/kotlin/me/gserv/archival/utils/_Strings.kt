/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.utils

import com.github.slugify.Slugify

private val slugify = Slugify.builder().transliterator(true).build()

fun String.toSlug(): String =
	slugify.slugify(this)!!

fun String.isInteger() =
	all { it.isDigit() }

fun String.isFloat() =
	all { it.isDigit() || it == '.' }
