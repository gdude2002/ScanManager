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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.rounded.Cached
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.FileKitPlatformSettings
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.gserv.archival.Colors
import me.gserv.archival.dropTarget
import me.gserv.archival.types.VisibilityTogglingWindow
import me.gserv.archival.utils.components.PrimaryButton
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.floor

class CompareWindow(val parent: VisibilityTogglingWindow) {
	val logger = KotlinLogging.logger { }

	var isOpen by mutableStateOf(false)
	var isPickerOpen by mutableStateOf(false)
	var isViewReversed by mutableStateOf(false)

	var progress: Float? by mutableStateOf(0f)
	var statusText by mutableStateOf("Waiting for input...")

	val firstImageState: MutableState<BufferedImage?> = mutableStateOf(null)
	val firstImageFileState: MutableState<File?> = mutableStateOf(null)

	val secondImageState: MutableState<BufferedImage?> = mutableStateOf(null)
	val secondImageFileState: MutableState<File?> = mutableStateOf(null)

	var comparisonImage by mutableStateOf<BufferedImage?>(null)
	var reverseComparisonImage by mutableStateOf<BufferedImage?>(null)

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
	lateinit var processingScope: CoroutineScope

	fun close() {
		dropTarget.clearState()

		isOpen = false

		parent.show()

		isPickerOpen = false
		isViewReversed = false

		firstImageFile = null
		secondImageFile = null

		firstImage = null
		secondImage = null

		comparisonImage = null
		reverseComparisonImage = null

		progress = 0f
		statusText = "Waiting for input..."
	}

	fun open() {
		isOpen = true

		dropTarget.clearState()

		dropTarget.smallText = "Waiting..."
		dropTarget.icon = Icons.Default.Image
	}

	fun open(firstImage: File, secondImage: File) {
		isOpen = true

		dropTarget.clearState()

		dropTarget.smallText = "Waiting..."
		dropTarget.icon = Icons.Default.Image

		processingScope.launch {
			filePicked(firstImage, firstImageFileState, firstImageState)
			filePicked(secondImage, secondImageFileState, secondImageState)
		}
	}

	fun pickFile(fileTarget: MutableState<File?>, imageTarget: MutableState<BufferedImage?>) {
		pickerFileTarget = fileTarget
		pickerImageTarget = imageTarget

		isPickerOpen = true
	}

	fun filePicked(file: File, fileTarget: MutableState<File?>, imageTarget: MutableState<BufferedImage?>) {
		logger.info { "Loading image: ${file.absolutePath}" }

		statusText = "Loading image..."
		dropTarget.smallText = "Loading..."

		progress = null
		dropTarget.loadingProgress = null

		fileTarget.value = file
		imageTarget.value = ImageIO.read(file)

		if (firstImage != null && secondImage != null) {
			runComparison(firstImage!!, secondImage!!)
		} else {
			logger.info { "Image loaded successfully" }

			statusText = "Image loaded."
			dropTarget.smallText = "Done."
			dropTarget.icon = Icons.Default.Check

			progress = 1f
			dropTarget.loadingProgress = 1f
		}
	}

	fun runComparison(first: BufferedImage, second: BufferedImage) {
		isViewReversed = false

		statusText = "Resizing images..."
		dropTarget.smallText = "Resizing..."
		dropTarget.icon = Icons.Default.FormatSize

		progress = 0f
		dropTarget.loadingProgress = 0f

		var maxWidth = maxOf(first.width, second.width)
		var maxHeight = maxOf(first.height, second.height)

		val widthRatio = 1000f / maxWidth
		val heightRatio = 1000f / maxHeight
		val scaleRatio = minOf(widthRatio, heightRatio)

		maxWidth = floor(maxWidth * scaleRatio).toInt()
		maxHeight = floor(maxHeight * scaleRatio).toInt()

		val resampler = ResampleOp(maxWidth, maxHeight)

		val firstImageResized =
			if (first.width != maxWidth || first.height != maxHeight) {
				logger.info { "Resizing first image..." }

				resampler.filter(first, null)
			} else {
				first
			}

		progress = 0.33f
		dropTarget.loadingProgress = 0.33f

		val secondImageResized =
			if (second.width != maxWidth || second.height != maxHeight) {
				logger.info { "Resizing second image..." }

				resampler.filter(second, null)
			} else {
				second
			}

		logger.info { "Visually comparing images..." }

		statusText = "Comparing images..."
		dropTarget.smallText = "Comparing..."
		dropTarget.icon = Icons.Default.Visibility

		progress = 0.66f
		dropTarget.loadingProgress = 0.66f

		comparisonImage = ImageComparison(firstImageResized, secondImageResized)
			.setRectangleLineWidth(5)
			.compareImages()
			.result

		reverseComparisonImage = ImageComparison(secondImageResized, firstImageResized)
			.setRectangleLineWidth(5)
			.compareImages()
			.result

		logger.info { "Comparison finished successfully" }

		statusText = "Comparison done."
		dropTarget.smallText = "Done."
		dropTarget.icon = Icons.Default.Check

		progress = 1f
		dropTarget.loadingProgress = 1f
	}

	@Composable
	@Preview
	fun create() {
		if (!::processingScope.isInitialized) {
			processingScope = rememberCoroutineScope { Dispatchers.IO }
		}

		if (isOpen) {
			parent.hide()

			Window(::close, state = state, resizable = false, title = "Compare Images") {
				scope = this

				if (isPickerOpen) {
					val pickerScope = rememberCoroutineScope { Dispatchers.IO }

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
						logger.info { "File picked: ${file?.path}" }

						if (file != null) {
							pickerScope.launch {
								logger.info { "Starting processing..." }

								filePicked(file.file, pickerFileTarget, pickerImageTarget)
							}
						}

						isPickerOpen = false
					}

					launcher.launch()
				}

				Colors.Theme { colors ->
					Box(Modifier.background(color = colors.WindowBackground)) {
						Row(
							horizontalArrangement = Arrangement.spacedBy(10.dp),
							modifier = Modifier.padding(10.dp)
								.fillMaxSize()
						) {
							Column(
								verticalArrangement = Arrangement.spacedBy(10.dp),
								modifier = Modifier
									.background(colors.SectionBackground, RoundedCornerShape(15.dp))
									.padding(vertical = 10.dp, horizontal = 15.dp)
									.requiredWidth(200.dp)
									.fillMaxHeight()
							) {
								Text("Pick two images to compare; click a loaded image below to replace it.")

								if (firstImageFile == null || firstImage == null) {
									PrimaryButton(
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

									OutlinedButton(
										{ pickFile(firstImageFileState, firstImageState) },
										modifier = Modifier.fillMaxWidth(),
										shape = MaterialTheme.shapes.small,
										contentPadding = PaddingValues(0.dp),
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
									PrimaryButton(
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

									OutlinedButton(
										{ pickFile(secondImageFileState, secondImageState) },
										modifier = Modifier.fillMaxWidth(),
										shape = MaterialTheme.shapes.small,
										contentPadding = PaddingValues(0.dp),
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

								if (comparisonImage != null && reverseComparisonImage != null) {
									PrimaryButton(
										{isViewReversed = !isViewReversed},
										modifier = Modifier.fillMaxWidth()
									) {
										Row(
											horizontalArrangement = Arrangement.spacedBy(10.dp),
										) {
											Icon(
												Icons.Rounded.Cached,
												""
											)

											Text("Swap View")
										}
									}
								}

								if (statusText.isNotEmpty()) {
									Text(statusText, modifier = Modifier.fillMaxWidth())
								}

								if (progress != null) {
									LinearProgressIndicator(progress = { progress ?: 0f }, Modifier.fillMaxWidth())
								} else {
									LinearProgressIndicator(Modifier.fillMaxWidth())
								}
							}

							Column(
								modifier = Modifier
									.background(colors.SectionBackground, RoundedCornerShape(15.dp))
									.padding(vertical = 10.dp, horizontal = 15.dp)
									.fillMaxHeight()
									.fillMaxWidth()
							) {
								if (comparisonImage != null && reverseComparisonImage != null) {
									if (!isViewReversed) {
										Image(
											comparisonImage!!.toPainter(),
											"Comparison image",
											contentScale = ContentScale.Fit,
											modifier = Modifier.fillMaxSize()
										)
									} else {
										Image(
											reverseComparisonImage!!.toPainter(),
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
	}
}
