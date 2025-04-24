package me.gserv.archival.utils.imageio

import java.io.IOException
import java.io.InputStream
import javax.imageio.stream.ImageInputStream

class ImageInputStreamWrapper(val stream: ImageInputStream) : InputStream() {
	override fun read(): Int =
		stream.read()

	@Throws(IOException::class)
	override fun read(b: ByteArray?, off: Int, len: Int): Int =
		stream.read(b, off, len)
}

fun ImageInputStream.toInputStream() =
	ImageInputStreamWrapper(this)
