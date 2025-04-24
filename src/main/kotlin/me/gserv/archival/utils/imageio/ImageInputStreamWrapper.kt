/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

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
