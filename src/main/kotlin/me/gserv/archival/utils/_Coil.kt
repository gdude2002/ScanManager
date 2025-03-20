/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.utils

import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.asImage
import coil3.decode.DecodeResult
import coil3.decode.DecodeUtils
import coil3.decode.Decoder
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import coil3.request.maxBitmapSize
import coil3.size.Precision
import coil3.util.component1
import coil3.util.component2
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Rect
import org.jetbrains.skia.impl.use
import org.jetbrains.skiko.toImage
import javax.imageio.ImageIO
import org.jetbrains.skia.Image as SkiaImage

class PsdDecoder(
	val result: SourceFetchResult,
	val options: Options,
) : Decoder {
	override suspend fun decode(): DecodeResult? {
		val image = ImageIO
			.read(result.source.source().inputStream())
			.toImage()

		val isSampled: Boolean
		val bitmap: Bitmap

		try {
			bitmap = Bitmap.makeFromImage(image, options)
			bitmap.setImmutable()
			isSampled = bitmap.width < image.width || bitmap.height < image.height
		} finally {
			image.close()
		}

		return DecodeResult(
			image = bitmap.asImage(),
			isSampled = isSampled,
		)
	}

	object Factory : Decoder.Factory {
		override fun create(
			result: SourceFetchResult,
			options: Options,
			imageLoader: ImageLoader
		): Decoder? {
			if (
				result.mimeType != "image/vnd.adobe.photoshop" &&
				result.source.fileOrNull()?.toFile()?.extension != "psd"
			) {
				return null
			}

			return PsdDecoder(result, options)
		}

	}
}

/**
 * Create a [Bitmap] from [image] for the given [options].
 *
 * Copy-pasted from Coil, because this is internal for some reason?
 */
@OptIn(ExperimentalCoilApi::class)
internal fun Bitmap.Companion.makeFromImage(
	image: SkiaImage,
	options: Options,
): Bitmap {
	val srcWidth = image.width
	val srcHeight = image.height

	val (dstWidth, dstHeight) = DecodeUtils.computeDstSize(
		srcWidth = srcWidth,
		srcHeight = srcHeight,
		targetSize = options.size,
		scale = options.scale,
		maxSize = options.maxBitmapSize,
	)

	var multiplier = DecodeUtils.computeSizeMultiplier(
		srcWidth = srcWidth,
		srcHeight = srcHeight,
		dstWidth = dstWidth,
		dstHeight = dstHeight,
		scale = options.scale,
	)

	// Only upscale the image if the options require an exact size.
	if (options.precision == Precision.INEXACT) {
		multiplier = multiplier.coerceAtMost(1.0)
	}

	val outWidth = (multiplier * srcWidth).toInt()
	val outHeight = (multiplier * srcHeight).toInt()
	val bitmap = Bitmap()

	bitmap.allocN32Pixels(outWidth, outHeight)

	Canvas(bitmap).use { canvas ->
		canvas.drawImageRect(
			image = image,
			src = Rect.makeWH(srcWidth.toFloat(), srcHeight.toFloat()),
			dst = Rect.makeWH(outWidth.toFloat(), outHeight.toFloat()),
		)
	}

	return bitmap
}
