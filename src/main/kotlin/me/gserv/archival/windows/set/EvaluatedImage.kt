package me.gserv.archival.windows.set

import me.gserv.archival.data.enums.ImageQuality
import java.net.URI

data class EvaluatedImage(
	val uri: URI,
	var quality: ImageQuality = ImageQuality.NOT_EVALUATED
)
