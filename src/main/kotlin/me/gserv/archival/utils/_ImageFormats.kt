/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.utils

import kotlin.text.Regex.Companion.escape

sealed class ImageFormat(
	val name: String,
	val mimeTypes: Set<String>,
	val matchers: Set<Regex>,
	val writableExtension: Set<String> = setOf(),
) {
	object CUR : ImageFormat("Windows Cursor", setOf(), regexSetOf("cur"))
	object GIF : ImageFormat("Graphics Interchange Format", setOf("image/gif"), regexSetOf("gif"))
	object HDR : ImageFormat("High Dynamic Range", setOf(), regexSetOf("hdr"))
	object ICNS : ImageFormat("Apple Icon", setOf("image/x-icns"), regexSetOf("icns"), setOf("icns"))
	object IFF : ImageFormat("Interchange File Format", setOf("application/x-iff"), regexSetOf("iff"), setOf("iff"))
	object JLS : ImageFormat("Lossless JPEG", setOf("image/jls"), regexSetOf("jls"))
	object PCX : ImageFormat("Picture Exchange", setOf("image/vnd.zbrush.pcx", "image/x-pcx"), regexSetOf("dcx", "pcx"))
	object PNG : ImageFormat("Portable Network Graphics", setOf("image/png"), regexSetOf("png"))
	object PTNG : ImageFormat("Apple MacPaint Picture", setOf("image/x-macpaint"), regexSetOf("ptng"))
	object SVG : ImageFormat("Scalable Vector Graphics", setOf("image/svg+xml"), regexSetOf("svg", "svgz"))
	object TIFF : ImageFormat("Tag Image File Format", setOf("image/tiff"), regexSetOf("tif", "tiff"), setOf("tiff"))
	object WBMP : ImageFormat("WAP Bitmap", setOf("image/vnd.wap.wbmp"), regexSetOf("wbmp"))
	object WEBP : ImageFormat("Web Picture", setOf("image/webp"), regexSetOf("webp"))
	object WMF : ImageFormat("Windows Metafile Format", setOf("application/x-msmetafile"), regexSetOf("wmf"))
	object XWD : ImageFormat("X Window Dump", setOf("image/x-xwindowdump"), regexSetOf("xwd"))

	object BMP : ImageFormat(
		"Windows Bitmap",

		setOf("image/bmp", "image/x-bmp"),
		regexSetOf("bmp", ".dib"),
		setOf("bmp")
	)

	object DDS : ImageFormat(
		"DirectDraw Surface",

		setOf("image/vnd-ms.dds", "image/x-direct-draw-surface"),
		regexSetOf("dds")
	)

	object ICO : ImageFormat(
		"Windows Icon",

		setOf("image/x-icon", "image/vnd.microsoft.icon"),
		regexSetOf("ico"),
		setOf("ico")
	)

	object JPEG : ImageFormat(
		"Joint Photographic Experts Group",

		setOf("image/jpeg"),
		regexSetOf("jpg", "jpeg", "jpe", "jif", "jfif", "jfi"),
		setOf("jpeg")
	)

	object PAM : ImageFormat(
		"Portable Arbitrary Map",

		setOf(
			"image/x-portable-bitmap",
			"image/x-portable-graymap",
			"image/x-portable-pixmap",
			"image/x-portable-anymap",
			"image/x-portable-arbitrarymap",
		),

		regexSetOf("pam", "pbm", "pgm", "ppm", "pfm"),
		setOf("pam", "pfm")
	)

	object PICT : ImageFormat(
		"Apple QuickDraw Picture",

		setOf("image/x-pict"),
		regexSetOf("pict", "pct", "pic"),
		setOf("pict")
	)

	object PSD : ImageFormat(
		"Adobe Photoshop",

		setOf("image/vnd.adobe.photoshop"),
		regexSetOf("psb", "psd"),
		setOf("psd")
	)

	object SGI : ImageFormat(
		"Silicon Graphics Image",

		setOf("image/sgi"),
		regexSetOf("sgi", "rgb", "rgba", "bw", "int", "inta")
	)

	object TGA : ImageFormat(
		"Truevision Graphics Adapter",

		setOf("image/x-targa", "image/x-tga"),
		regexSetOf("tga", "icb", "vda", "vst"),
		setOf("tga")
	)

	object THUMBSDB : ImageFormat(
		"Windows Thumbs.db",

		setOf(),
		setOf("thumbs\\.db".toRegex(RegexOption.IGNORE_CASE))
	)

	companion object {
		val all by lazy {
			ImageFormat::class.sealedSubclasses
				.map { it.objectInstance as ImageFormat }
		}

		fun find(filename: String?, mimeType: String? = null): ImageFormat? =
			filename?.let { findByFilename(it) }
				?: findByMimeType(mimeType)

		fun findByMimeType(mimeType: String? = null): ImageFormat? =
			all.firstOrNull { format ->
				format.mimeTypes.any { type ->
					type == mimeType
				}
			}

		fun findByFilename(filename: String): ImageFormat? =
			all.filter { format ->
				format.matchers.any { regex ->
					filename.matches(regex)
				}
			}.maxByOrNull { format ->
				format.matchers.maxOf { regex ->
					regex.toString()
				}
			}

		fun findByName(name: String): ImageFormat? =
			all.firstOrNull { it.name.equals(name, true) }

		fun regexSetOf(vararg items: String) =
			items.map {
				".*\\.${escape(it)}".toRegex(RegexOption.IGNORE_CASE)
			}.toSet()
	}
}
