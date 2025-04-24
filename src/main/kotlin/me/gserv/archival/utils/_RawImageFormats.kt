/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.utils

import org.librawfx.RAWDescriptor
import kotlin.text.Regex.Companion.escape

sealed class RawImageFormat(
	val name: String,
	val mimeTypes: Set<String>,
	val matchers: Set<Regex>,
	val writableExtension: Set<String> = setOf(),
) {
	object RAW : RawImageFormat(
		"Raw Image",

		setOf(
			"image/x-adobe-dng",
			"image/x-canon-cr2", "image/x-canon-cr3", "image/x-canon-crw",
			"image/x-fuji-raf",
			"image/x-leica-rwl",
			"image/x-mamiya-mef", "image/x-mamiya-mfw",
			"image/x-nikon-nef", "image/x-nikon-nrw",
			"image/x-olympus-orf", "image/x-olympus-ori",
			"image/x-panasonic-raw", "image/x-panasonic-rw2",
			"image/x-pentax-pef",
			"image/x-samsung-srw",
			"image/x-sigma-x3f",
			"image/x-sony-arw",
		),

		regexSetOf(*RAWDescriptor.getInstance().extensions.toTypedArray())
	)

	companion object {
		val all by lazy {
			RawImageFormat::class.sealedSubclasses
				.map { it.objectInstance as RawImageFormat }
		}

		fun find(filename: String?, mimeType: String? = null): RawImageFormat? =
			filename?.let { findByFilename(it) }
				?: findByMimeType(mimeType)

		fun findByMimeType(mimeType: String? = null): RawImageFormat? =
			all.firstOrNull { format ->
				format.mimeTypes.any { type ->
					type == mimeType
				}
			}

		fun findByFilename(filename: String): RawImageFormat? =
			all.filter { format ->
				format.matchers.any { regex ->
					filename.matches(regex)
				}
			}.maxByOrNull { format ->
				format.matchers.maxOf { regex ->
					regex.toString()
				}
			}

		fun findByName(name: String): RawImageFormat? =
			all.firstOrNull { it.name.equals(name, true) }

		fun regexSetOf(vararg items: String) =
			items.map {
				".*\\.${escape(it)}".toRegex(RegexOption.IGNORE_CASE)
			}.toSet()
	}
}
