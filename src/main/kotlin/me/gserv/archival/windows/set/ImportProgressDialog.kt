package me.gserv.archival.windows.set

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cached
import androidx.compose.material.icons.rounded.Error
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.RenderVectorGroup
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.ImageLoader
import coil3.PlatformContext
import com.skydoves.landscapist.animation.circular.CircularRevealPlugin
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.placeholder.placeholder.PlaceholderPlugin
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.gserv.archival.Colors
import me.gserv.archival.data.Database
import me.gserv.archival.data.GlobalState
import me.gserv.archival.dropTarget
import me.gserv.archival.utils.PsdDecoder
import me.gserv.archival.utils.components.DialogContainer
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import javax.imageio.ImageIO
import kotlin.io.path.deleteExisting
import kotlin.io.path.toPath

class ImportProgressDialog {
	val logger = KotlinLogging.logger { }

	var isOpen by mutableStateOf(false)

	var callback: Callback by mutableStateOf({})
	var files: List<EvaluatedImage> by mutableStateOf(listOf())

	var progress by mutableStateOf(0.0f)
	var progressFilename by mutableStateOf("")
	var progressText by mutableStateOf("")

	lateinit var processingScope: CoroutineScope

	fun close() {
		callback = {}
		files = emptyList()
		progress = 0.0f
		progressFilename = ""
		progressText = ""

		isOpen = false
	}

	fun open(files: List<EvaluatedImage>, callback: Callback) {
		this.callback = callback
		this.files = files

		isOpen = true

		processingScope.launch {
			doImport()
		}
	}

	suspend fun doImport() {
		val binder = GlobalState.binder!!
		val set = GlobalState.set!!
		val progressFraction = 1.0f / files.size

		var currentFileIndex = set.getHighestIndex()

		files.forEachIndexed { index, (uri, quality) ->
			currentFileIndex += 1

			val currentProgress  = progressFraction * index
			val path = uri.toPath()
			val fileName = path.fileName.toString()

			val psdFile = File(
				binder.editsDirectory,
				set.editFileName(currentFileIndex.toString())
			)

			val jpegFile = File(
				binder.originalsDirectory,
				set.originalFileName(currentFileIndex.toString())
			)

			dropTarget.loadingProgress = currentProgress
			progress = currentProgress

			progressFilename = fileName
			progressText = "Loading file..."

			val image = ImageIO.read(uri.toURL())

			progressText = "Calculating hashes..."
			// TODO: Calculate hashes

			val averageHash = ""
			val differenceHash = ""
			val medianHash = ""
			val perceptiveHash = ""
			val rotationalHash = ""

			progressText = "Saving PSD..."

			Files.copy(
				path,
				psdFile.toPath(),
				StandardCopyOption.COPY_ATTRIBUTES,
			)

			progressText = "Saving JPEG..."

			ImageIO.write(image, "jpeg", jpegFile)

			progressText = "Saving to database..."

			Database.transaction {
				// TODO: Store data into database
			}
		}

		progressFilename = ""
		progressText = "Cleaning up old files..."

		dropTarget.loadingProgress = 1.0f
		progress = 1.0f

		files.forEach { (uri, _) ->
			uri.toPath().deleteExisting()
		}

		progressText = "Done!"

		dropTarget.loadingProgress = null

		callback()
		close()
	}

	@Composable
	@Preview
	fun create() {
		val imageLoader = ImageLoader
			.Builder(PlatformContext.Companion.INSTANCE)
			.components {
				add(PsdDecoder.Factory)
			}
			.build()

		val iconTintPainter =
			@Composable { image: ImageVector, color: Color ->
				rememberVectorPainter(
					defaultWidth = image.defaultWidth,
					defaultHeight = image.defaultHeight,
					viewportWidth = image.viewportWidth,
					viewportHeight = image.viewportHeight,
					name = image.name,
					tintColor = color,
					tintBlendMode = image.tintBlendMode,
					autoMirror = image.autoMirror
				) { _, _ ->
					RenderVectorGroup(group = image.root)
				}
			}

		if (!::processingScope.isInitialized) {
			processingScope = rememberCoroutineScope { Dispatchers.IO }
		}

		if (isOpen) {
			Dialog({}, DialogProperties(false, false, true)) {
				Colors.Theme { colors ->
					val imageComponent = rememberImageComponent {
						add(
							CircularRevealPlugin(
								duration = 350
							)
						)

						add(
							PlaceholderPlugin.Loading(
								iconTintPainter(
									Icons.Rounded.Cached,
									colors.Material.primary
								)
							)
						)

						add(
							PlaceholderPlugin.Failure(
								iconTintPainter(
									Icons.Rounded.Error,
									colors.Material.error
								)
							)
						)
					}

					DialogContainer {
						Column(
							modifier = Modifier.Companion.padding(10.dp),
							verticalArrangement = Arrangement.spacedBy(5.dp)
						) {}
					}
				}
			}
		}
	}
}
