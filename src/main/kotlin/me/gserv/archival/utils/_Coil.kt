/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.utils

import androidx.compose.runtime.Composable
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.annotation.ExperimentalCoilApi
import coil3.asImage
import coil3.decode.DecodeResult
import coil3.decode.DecodeUtils
import coil3.decode.Decoder
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import coil3.request.maxBitmapSize
import coil3.size.Precision
import coil3.util.Logger
import coil3.util.component1
import coil3.util.component2
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Rect
import org.jetbrains.skia.impl.use
import org.jetbrains.skiko.toImage
import javax.imageio.ImageIO
import org.jetbrains.skia.Image as SkiaImage

@Composable
fun createImageLoader(): ImageLoader =
	ImageLoader
		.Builder(PlatformContext.INSTANCE)
		.components {
			add(ImageIODecoder.Factory)
		}
		.logger(SLF4JLogger())
		.build()

class SLF4JLogger(override var minLevel: Logger.Level = Logger.Level.Debug) : Logger {
	private val logger = KotlinLogging.logger("coil3.util.Logger")

	override fun log(
		tag: String,
		level: Logger.Level,
		message: String?,
		throwable: Throwable?
	) {
		when (level) {
			Logger.Level.Verbose -> logger.trace(throwable) { message }
			Logger.Level.Debug -> logger.debug(throwable) { message }
			Logger.Level.Info -> logger.info(throwable) { message }
			Logger.Level.Error -> logger.error(throwable) { message }
			Logger.Level.Warn -> logger.warn(throwable) { message }
		}
	}

}

class ImageIODecoder(
	val result: SourceFetchResult,
	val options: Options,
) : Decoder {
	override suspend fun decode(): DecodeResult? {
		val image = ImageIO
			.read(result.source.file().toFile().inputStream())
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
			val format = ImageFormat.find(
				result.source.fileOrNull()?.toFile()?.name,
				result.mimeType
			)

			if (format != null) {
				return ImageIODecoder(result, options)
			}

			return null
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
