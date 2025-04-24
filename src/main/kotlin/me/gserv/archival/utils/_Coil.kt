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
import coil3.util.DebugLogger
import coil3.util.component1
import coil3.util.component2
import io.github.oshai.kotlinlogging.KotlinLogging
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.PixelFormat
import javafx.scene.image.WritableImage
import javafx.scene.image.WritablePixelFormat
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Rect
import org.jetbrains.skia.impl.use
import org.jetbrains.skiko.toImage
import org.libraw.nativ.libraw_data_t.image
import org.librawfx.LibrawImage
import org.librawfx.RAWImageLoader
import org.librawfx.RawDecoderSettings
import java.awt.image.*
import java.awt.image.BufferedImage.TYPE_INT_RGB
import java.nio.IntBuffer
import javax.imageio.ImageIO
import org.jetbrains.skia.Image as SkiaImage


@Composable
fun createImageLoader(): ImageLoader =
	ImageLoader
		.Builder(PlatformContext.INSTANCE)
		.components {
			add(ImageIODecoder.Factory)
			add(LibRawDecoder.Factory)
		}
		.logger(DebugLogger())
		.build()

class LibRawDecoder(
	val result: SourceFetchResult,
	val options: Options,
) : Decoder {
	val logger = KotlinLogging.logger { }

	override suspend fun decode(): DecodeResult? {
		RAWImageLoader.getSettings()
		val file = result.source.fileOrNull()!!

		val rawImage = LibrawImage(
			file.toString(),

			hashMapOf("Default" to RawDecoderSettings())
		)

//		val loader = RAWImageLoaderFactory.getInstance().createImageLoader(file.toFile().inputStream())
//		val frame = loader.load(
//			0,
//			rawImage.imageWidth.toDouble(),
//			rawImage.imageHeight.toDouble(),
//			true,
//			true,
//			1.0f,
//			1.0f
//		)

		try {
			val metadata = rawImage.metaData

			logger.info {
				buildString {
					appendLine("=== $file ===")

					metadata
						.toSortedMap(String::compareTo)
						.forEach { (key, value) ->
							appendLine("    $key -> $value")
						}
				}
			}
		} catch (e: Exception) {
			logger.warn(e) { "Failed to load metadata: $file" }
		}

		// RGB Int array as would be provided to JavaFX
		val data = rawImage.readPixelData()
		val img = WritableImage(rawImage.getImageWidth().toInt(), rawImage.getImageHeight().toInt())
		val pw = img.getPixelWriter()

		pw.setPixels(
			0,
			0,
			rawImage.getImageWidth().toInt(),
			rawImage.getImageHeight().toInt(),
			PixelFormat.getIntArgbInstance(),
			data,
			0,
			rawImage.getImageWidth().toInt()
		)

//		val bufferedImage = MappedImageFactory.createCompatibleMappedImage(
//			rawImage.imageWidth.toInt(),
//			rawImage.imageHeight.toInt(),
//			TYPE_INT_RGB
//		)
//
//		bufferedImage.raster.setPixels(
//			0, 0,
//			bufferedImage.width, bufferedImage.height,
//			data
//		)

//		val raster = Raster.createWritableRaster(
//			SinglePixelPackedSampleModel(
//				TYPE_INT_RGB,
//				rawImage.imageHeight.toInt(),
//				rawImage.imageHeight.toInt(),rawImage.imageHeight.toInt() * rawImage.numBands,
//
//				arrayOf(
//					0x00ff0000,
//					0x0000ff00,
//					0x000000ff,
//					-0x1000000,
//				).toIntArray()
//			),
//			Point(0, 0)
//		)

//		val bufferedImage = BufferedImage(
//			ColorModel.getRGBdefault(),
//			raster,
//			true,
//			null
//		)

		val image = SwingFXUtils.fromFXImage(img, null).toImage()

		val isSampled: Boolean
		val bitmap: Bitmap

		try {
			bitmap = Bitmap.makeFromImage(image, options)
			bitmap.setImmutable()
			isSampled = bitmap.width < rawImage.imageWidth || bitmap.height < rawImage.imageHeight
		} finally {
			rawImage
		}

//		val data = rawImage
//			.readPixelData()
//			.map { it.toByte() }
//			.toByteArray()
//
//		val bitmap = Bitmap()
//
//		bitmap.setImageInfo(
//			ImageInfo(
//				ColorInfo(
//					ColorType.RGB_888X,
//					ColorAlphaType.OPAQUE,
//					ColorSpace.sRGB,
//				),
//				rawImage.imageWidth.toInt(),
//				rawImage.imageHeight.toInt()
//			)
//		)
//
//		bitmap.installPixels(data)
//
//
//		val isSampled: Boolean
//
// 		try {
//			bitmap.setImmutable()
//			isSampled = bitmap.width < rawImage.imageWidth || bitmap.height < rawImage.imageHeight
//		} finally {
//			rawImage
//		}

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
			val format = RawImageFormat.find(
				result.source.fileOrNull()?.toFile()?.name,
				result.mimeType
			)

			if (format != null) {
				return LibRawDecoder(result, options)
			}

			return null
		}
	}
}

class ImageIODecoder(
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
