/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.utils.imageio

import com.sun.javafx.iio.ImageLoader
import com.sun.javafx.iio.ImageLoaderFactory
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.PixelFormat
import javafx.scene.image.WritableImage
import me.gserv.archival.utils.ImageFormat
import org.librawfx.*
import java.awt.color.ColorSpace
import java.awt.image.BufferedImage
import java.awt.image.DataBuffer
import java.util.*
import javax.imageio.ImageReadParam
import javax.imageio.ImageReader
import javax.imageio.ImageTypeSpecifier
import javax.imageio.metadata.IIOMetadata
import javax.imageio.spi.ImageReaderSpi
import javax.imageio.stream.ImageInputStream

val loaderFactory: ImageLoaderFactory by lazy {
	RAWImageLoader.getSettings()
	RAWImageLoaderFactory.getInstance()
}

class RawImageIOPlugin : ImageReaderSpi(
	"gserv.me",
	"0.0.1",
	arrayOf("RAW"),
	RAWDescriptor.getInstance().extensions.toTypedArray(),
	ImageFormat.RAW.mimeTypes.toTypedArray(),
	"me.gserv.archival.utils.imageio.RawImageIOPlugin.Reader",
	arrayOf(ImageInputStream::class.java),
	null,
	false,
	null,
	null,
	null,
	null,
	false,
	null,
	null,
	null,
	null,
) {
	override fun canDecodeInput(input: Any): Boolean {
		if (input !is ImageInputStream) {
			return false
		}

		val buf = ByteArray(32)

		input.read(buf, 0, 32)
		input.seek(0)

		return RAWDescriptor.getInstance().signatures.any {
			it.matches(buf)
		}
	}

	override fun createReaderInstance(extension: Any?): ImageReader =
		Reader(this)

	override fun getDescription(locale: Locale): String =
		"RAW image plugin based on LibRaw"

	inner class Reader(originatingProvider: ImageReaderSpi) : ImageReader(originatingProvider) {
		val loader: ImageLoader = loaderFactory.createImageLoader((input as ImageInputStream).toInputStream())
		val rawImage = LibrawImage(loader as RAWImageLoader, hashMapOf("Default" to RawDecoderSettings()))
		val byteArray: ByteArray = (input as ImageInputStream).toInputStream().readAllBytes()
		val bytes: IntArray = rawImage.readPixelDataFromStream(byteArray).toJavaFX()

		override fun getImageTypes(imageIndex: Int): Iterator<ImageTypeSpecifier?>? =
			arrayOf(
				ImageTypeSpecifier.createInterleaved(
					ColorSpace.getInstance(ColorSpace.TYPE_RGB),
					arrayOf(0, 1, 2).toIntArray(),
					DataBuffer.TYPE_BYTE,
					false,
					false
				)
			).iterator()

		override fun read(imageIndex: Int, param: ImageReadParam?): BufferedImage? {
			val img = WritableImage(getWidth(imageIndex), getHeight(imageIndex))
			val pw = img.getPixelWriter()

			pw.setPixels(
				0,
				0,
				rawImage.getImageWidth().toInt(),
				rawImage.getImageHeight().toInt(),
				PixelFormat.getIntArgbInstance(),
				bytes,
				0,
				rawImage.getImageWidth().toInt()
			)

			return SwingFXUtils.fromFXImage(img, null)
		}

		override fun getHeight(imageIndex: Int): Int = rawImage.imageHeight.toInt()
		override fun getImageMetadata(imageIndex: Int): IIOMetadata? = null
		override fun getNumImages(allowSearch: Boolean): Int = 1
		override fun getStreamMetadata(): IIOMetadata? = null
		override fun getWidth(imageIndex: Int): Int = rawImage.imageWidth.toInt()
	}
}

fun ByteArray.toJavaFX(): IntArray {
	val raw = IntArray(size * 4 / 3)

	for (j in 0..<size / 3) {
		raw[j] = (-0x1000000
			or ((this[3 * j + 0].toInt() and 0xFF) shl 16)
			or ((this[3 * j + 1].toInt() and 0xFF) shl 8)
			or ((this[3 * j + 2].toInt() and 0xFF)))
	}

	return raw
}
