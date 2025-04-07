package me.gserv.archival.windows.set

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.gserv.archival.Colors
import me.gserv.archival.data.Database
import me.gserv.archival.data.GlobalState
import me.gserv.archival.data.entities.Image
import me.gserv.archival.dropTarget
import me.gserv.archival.utils.components.DialogContainer
import me.gserv.archival.utils.forEach
import me.gserv.archival.utils.getHashes
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

	fun doImport() {
		Database.transaction {
			val binder = GlobalState.binder!!
			val set = GlobalState.set!!
			val progressFraction = 1.0f / files.size

			var currentFileIndex = set.getHighestIndex()

			files.forEachIndexed { index, (uri, quality) ->
				currentFileIndex += 1

				val currentProgress = progressFraction * index
				val path = uri.toPath()
				val fileName = path.fileName.toString()

				val psdFile = File(
					binder.editsDirectory,
					set.editFileName(currentFileIndex.toString())
				).absoluteFile

				val jpegFile = File(
					binder.originalsDirectory,
					set.originalFileName(currentFileIndex.toString())
				).absoluteFile

				dropTarget.loadingProgress = currentProgress
				progress = currentProgress

				progressFilename = fileName
				progressText = "Loading file..."

				logger.info { "Loading file: $fileName" }

				val image = ImageIO.read(uri.toURL())

				progressText = "Calculating hashes..."

				logger.info { "Calculating hashes..." }

				val hashes = getHashes(image)

				progressText = "Saving PSD..."

				logger.info { "Saving PSD: $psdFile" }

				Files.copy(
					path,
					psdFile.toPath(),
					StandardCopyOption.COPY_ATTRIBUTES,
				)

				progressText = "Saving JPEG..."

				logger.info { "Saving JPEG: $jpegFile" }

				ImageIO.write(image, "jpeg", jpegFile)

				progressText = "Saving to database..."

				logger.info { "Saving to database..." }

				// Delete existing image data.
				Image.findById(psdFile.toString())?.delete()
				Image.findById(jpegFile.toString())?.delete()

				forEach(psdFile, jpegFile) {
					Image.create(it) {
						addHashes(hashes)

						this.binder = binder
						this.set = set
						this.quality = quality
					}
				}

				logger.info { "" }
			}

			GlobalState.set!!.totalScans += files.size
		}

		progressFilename = ""
		progressText = "Cleaning up old files..."

		dropTarget.loadingProgress = 1.0f
		progress = 1.0f

		files.forEach { (uri, _) ->
			logger.info { "Deleting file: $uri" }

			uri.toPath().deleteExisting()
		}

		progressText = "Done!"

		logger.info { "Done!" }

		callback()
		close()
	}

	@Composable
	@Preview
	fun create() {
		if (!::processingScope.isInitialized) {
			processingScope = rememberCoroutineScope { Dispatchers.IO }
		}

		if (isOpen) {
			Dialog({}, DialogProperties(false, false, true)) {
				Colors.Theme { colors ->
					DialogContainer {
						Column(
							modifier = Modifier.Companion.padding(10.dp),
							verticalArrangement = Arrangement.spacedBy(5.dp),
							horizontalAlignment = Alignment.CenterHorizontally,
						) {
							Text("Importing files..")

							if (progressFilename.isNotEmpty()) {
								Text("Current file: $progressFilename")
							} else {
								Text("")
							}

							Text(progressText)

							LinearProgressIndicator({ progress })
						}
					}
				}
			}
		}
	}
}
