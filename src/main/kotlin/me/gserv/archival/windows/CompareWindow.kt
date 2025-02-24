/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.windows

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import com.github.romankh3.image.comparison.ImageComparison
import com.twelvemonkeys.image.ResampleOp
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.FileKitPlatformSettings
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.gserv.archival.Colors
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.floor

class CompareWindow(val parent: MainWindow) {
	var isOpen by mutableStateOf(false)
	var isPickerOpen by mutableStateOf(false)

	var progress: Float? by mutableStateOf(0f)
	var statusText by mutableStateOf("Waiting for input...")

	val firstImageState: MutableState<BufferedImage?> = mutableStateOf(null)
	val firstImageFileState: MutableState<File?> = mutableStateOf(null)

	val secondImageState: MutableState<BufferedImage?> = mutableStateOf(null)
	val secondImageFileState: MutableState<File?> = mutableStateOf(null)

	var comparisonImage by mutableStateOf<BufferedImage?>(null)
	var pickerFileTarget by mutableStateOf(firstImageFileState)
	var pickerImageTarget by mutableStateOf(firstImageState)

	var firstImageFile: File?
		get() = firstImageFileState.value
		set(value) {
			firstImageFileState.value = value
		}

	var secondImageFile: File?
		get() = secondImageFileState.value
		set(value) {
			secondImageFileState.value = value
		}

	var firstImage: BufferedImage?
		get() = firstImageState.value
		set(value) {
			firstImageState.value = value
		}

	var secondImage: BufferedImage?
		get() = secondImageState.value
		set(value) {
			secondImageState.value = value
		}

	val state = WindowState(
		size = DpSize(1200.dp, 900.dp)
	)

	lateinit var scope: FrameWindowScope

	fun close() {
		isOpen = false
		isPickerOpen = false

		firstImageFile = null
		secondImageFile = null

		firstImage = null
		secondImage = null

		comparisonImage = null

		progress = 0f
		statusText = "Waiting for input..."

		parent.state.isMinimized = false
		parent.scope.window.requestFocus()
	}

	fun open() {
		isOpen = true
		parent.state.isMinimized = true
	}

	fun pickFile(fileTarget: MutableState<File?>, imageTarget: MutableState<BufferedImage?>) {
		pickerFileTarget = fileTarget
		pickerImageTarget = imageTarget

		isPickerOpen = true
	}

	@Composable
	@Preview
	fun create() {
		val processingScope = rememberCoroutineScope { Dispatchers.IO }

		if (isOpen) {
			Window({ close() }, state = state, resizable = false, title = "Compare Images") {
				scope = this

				if (isPickerOpen) {
					val launcher = rememberFilePickerLauncher(
						type = PickerType.File(listOf("png", "jpg", "jpeg", "gif", "bmp", "psd")),
						title = "Select an image",
						platformSettings = FileKitPlatformSettings(parentWindow = window),

						initialDirectory = if (pickerFileTarget.value != null && pickerFileTarget.value!!.parentFile.isDirectory) {
							pickerFileTarget.value!!.parentFile.absolutePath
						} else {
							null
						}
					) { file ->
						if (file != null) {
							processingScope.launch {
								progress = null
								statusText = "Loading image..."

								pickerFileTarget.value = file.file
								pickerImageTarget.value = ImageIO.read(file.file)

								if (firstImage != null && secondImage != null) {
									statusText = "Resizing images..."
									progress = 0f

									var maxWidth = maxOf(firstImage!!.width, secondImage!!.width)
									var maxHeight = maxOf(firstImage!!.height, secondImage!!.height)

									val widthRatio = 1000f / maxWidth
									val heightRatio = 1000f / maxHeight
									val scaleRatio = minOf(widthRatio, heightRatio)

									maxWidth = floor(maxWidth * scaleRatio).toInt()
									maxHeight = floor(maxHeight * scaleRatio).toInt()

									val resampler = ResampleOp(maxWidth, maxHeight)

									val firstImageResized =
										if (firstImage!!.width != maxWidth || firstImage!!.height != maxHeight) {
											resampler.filter(firstImage, null)
										} else {
											firstImage!!
										}

									progress = 0.33f

									val secondImageResized =
										if (secondImage!!.width != maxWidth || secondImage!!.height != maxHeight) {
											resampler.filter(secondImage, null)
										} else {
											secondImage!!
										}

									statusText = "Comparing images..."
									progress = 0.66f

									comparisonImage = ImageComparison(firstImageResized, secondImageResized)
										.setRectangleLineWidth(5)
										.compareImages()
										.result

									statusText = "Comparison done."
									progress = 1f
								} else {
									statusText = "Image loaded."
									progress = 1f
								}
							}

							isPickerOpen = false
						}
					}

					launcher.launch()
				}

				MaterialTheme {
					Row(
						horizontalArrangement = Arrangement.spacedBy(10.dp),
						modifier = Modifier.padding(10.dp)
							.fillMaxSize()
					) {
						Column(
							verticalArrangement = Arrangement.spacedBy(10.dp),
							modifier = Modifier
								.background(Colors.LightGray, RoundedCornerShape(15.dp))
								.padding(vertical = 10.dp, horizontal = 15.dp)
								.requiredWidth(200.dp)
								.fillMaxHeight()
						) {
							Text("Pick two images to compare; click a loaded image below to replace it.")

							if (firstImageFile == null || firstImage == null) {
								Button(
									{ pickFile(firstImageFileState, firstImageState) },
									modifier = Modifier.fillMaxWidth()
								) {
									Text("Pick first image")
								}
							} else {
								Text(
									firstImageFile!!.name,
									overflow = TextOverflow.Ellipsis,
									softWrap = false,
								)

								TextButton(
									{ pickFile(firstImageFileState, firstImageState) },
									modifier = Modifier.fillMaxWidth()
								) {
									Image(
										firstImage!!.toPainter(),
										"First comparison image",
										contentScale = ContentScale.Fit,
										modifier = Modifier.fillMaxWidth()
									)
								}
							}

							if (secondImageFile == null || secondImage == null) {
								Button(
									{ pickFile(secondImageFileState, secondImageState) },
									modifier = Modifier.fillMaxWidth()
								) {
									Text("Pick second image")
								}
							} else {
								Text(
									secondImageFile!!.name,
									overflow = TextOverflow.Ellipsis,
									softWrap = false,
								)

								TextButton(
									{ pickFile(secondImageFileState, secondImageState) },
									modifier = Modifier.fillMaxWidth()
								) {
									Image(
										secondImage!!.toPainter(),
										"Second comparison image",
										contentScale = ContentScale.Fit,
										modifier = Modifier.fillMaxWidth()
									)
								}
							}

							Spacer(Modifier.weight(1f, true))

							if (statusText.isNotEmpty()) {
								Text(statusText, modifier = Modifier.fillMaxWidth())
							}

							if (progress != null) {
								LinearProgressIndicator(progress!!, Modifier.fillMaxWidth())
							} else {
								LinearProgressIndicator(Modifier.fillMaxWidth())
							}
						}

						Column(
							modifier = Modifier
								.background(Colors.LightGray, RoundedCornerShape(15.dp))
								.padding(vertical = 10.dp, horizontal = 15.dp)
								.fillMaxHeight()
								.fillMaxWidth()
						) {
							if (comparisonImage != null) {
								Image(
									comparisonImage!!.toPainter(),
									"Comparison image",
									contentScale = ContentScale.Fit,
									modifier = Modifier.fillMaxSize()
								)
							}
						}
					}
				}
			}
		}
	}
}
