/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.windows.set

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.RenderVectorGroup
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.FrameWindowScope
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.animation.circular.CircularRevealPlugin
import com.skydoves.landscapist.coil3.CoilImage
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.placeholder.placeholder.PlaceholderPlugin
import io.github.oshai.kotlinlogging.KotlinLogging
import me.gserv.archival.Colors
import me.gserv.archival.data.enums.ImageQuality
import me.gserv.archival.utils.DirectoryWatcher
import me.gserv.archival.utils.components.DialogContainer
import me.gserv.archival.utils.components.EnumDropdown
import me.gserv.archival.utils.components.SecondaryOutlinedButton
import me.gserv.archival.utils.components.SuccessButton
import me.gserv.archival.utils.createImageLoader
import java.awt.Desktop
import kotlin.io.path.toPath

typealias Callback = () -> Unit

class ImportFilesDialog(
	val parent: FrameWindowScope,
) {
	val logger = KotlinLogging.logger { }

	var callback: Callback by mutableStateOf({})
	var directoryWatcher: DirectoryWatcher? by mutableStateOf(null)

	var isOpen by mutableStateOf(false)

	fun close() {
		callback = {}

		directoryWatcher?.clearCallbacks(this)
		directoryWatcher = null

		isOpen = false
	}

	fun open(directoryWatcher: DirectoryWatcher, callback: Callback) {
		this.callback = callback
		this.directoryWatcher = directoryWatcher

		isOpen = true
	}

	@Composable
	@Preview
	fun create() {
		val imageLoader = createImageLoader()

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

		if (isOpen) {
			directoryWatcher!!.clearCallbacks(this)

			val files = directoryWatcher!!.files
				.map { EvaluatedImage(it) }
				.toMutableStateList()

			directoryWatcher!!.onAdded(this) { files.add(EvaluatedImage(it)) }
			directoryWatcher!!.onDeleted(this) { files.remove(EvaluatedImage(it)) }

			Dialog(
				{}, DialogProperties(
					dismissOnBackPress = false,
					dismissOnClickOutside = false,
					usePlatformDefaultWidth = true
				)
			) {
				val progressDialog = ImportProgressDialog()
				progressDialog.create()

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
							modifier = Modifier.padding(10.dp),
							verticalArrangement = Arrangement.spacedBy(5.dp)
						) {
							Row(
								verticalAlignment = Alignment.CenterVertically,
								horizontalArrangement = Arrangement.spacedBy(10.dp)
							) {
								Icon(
									Icons.Rounded.UploadFile,
									"",
									modifier = Modifier
										.absolutePadding(top = 1.dp)
										.size(30.dp)
								)

								Text(
									"Import Files",
									fontSize = TextUnit(1.5F, TextUnitType.Em)
								)

								Spacer(Modifier.weight(1f, true))

								SecondaryOutlinedButton(
									onClick = {
										close()
									},
								) {
									Icon(
										Icons.Rounded.Cancel,
										"",
										modifier = Modifier.absolutePadding(right = 4.dp)
									)

									Text("Cancel")
								}

								SuccessButton(
									onClick = {
										progressDialog.open(files.toList()) {
											callback()
											close()
										}
									},
								) {
									Icon(
										Icons.Rounded.Save,
										"",
										modifier = Modifier.absolutePadding(right = 4.dp)
									)

									Text("Save")
								}
							}

							Text(
								"Order the files by clicking the corresponding arrows, and click \"Save\"."
							)

							val verticalScrollState = rememberScrollState(0)

							Box {
//								ReorderableColumn(
//									list = files,
//
//									onSettle = { fromIndex, toIndex ->
//										logger.info { "===" }
//										logger.info { "List before: ${files.joinToString { it.toPath().fileName.toString() }}" }
//
//										files.apply {
//											add(toIndex, removeAt(fromIndex))
//										}
//
//
//										logger.info { "List after: ${files.joinToString { it.toPath().fileName.toString() }}" }
//									},
//
//									modifier = Modifier
//										.verticalScroll(verticalScrollState)
//										.absolutePadding(right = 17.dp),
//
//									verticalArrangement = Arrangement.spacedBy(10.dp)
//								) { index, uri, isDragging ->
//									val interactionSource = remember { MutableInteractionSource() }
//
//									Card(
//										onClick = {},
//										interactionSource = interactionSource,
//										modifier = Modifier
//											.semantics {
//												customActions = listOf(
//													CustomAccessibilityAction(
//														label = "Move Up",
//
//														action = {
//															if (index > 0) {
//																directoryWatcher!!.files.apply {
//																	add(index - 1, removeAt(index))
//																}
//
//																true
//															} else {
//																false
//															}
//														}
//													),
//
//													CustomAccessibilityAction(
//														label = "Move Down",
//
//														action = {
//															if (index < directoryWatcher!!.files.size - 1) {
//																directoryWatcher!!.files.apply {
//																	add(index + 1, removeAt(index))
//																}
//
//																true
//															} else {
//																false
//															}
//														}
//													),
//												)
//											}
//									) {
//										Row(
//											verticalAlignment = Alignment.CenterVertically,
//											modifier = Modifier
//												.background(colors.SectionBackground, RoundedCornerShape(15.dp))
//												.padding(10.dp)
//										) {
//											IconButton(
//												modifier = Modifier
//													.draggableHandle(interactionSource = interactionSource)
//													.clearAndSetSemantics { },
//												onClick = {},
//											) {
//												Icon(Icons.Rounded.DragHandle, contentDescription = "Reorder")
//											}
//
//											Text(
//												uri.toPath().fileName.toString() + " ($index)",
//												fontSize = TextUnit(1.25F, TextUnitType.Em)
//											)
//
//											Spacer(Modifier.weight(1f))
//
//											OutlinedButton(
//												{
//													Desktop.getDesktop()
//														.browse(uri)
//												},
//												border = BorderStroke(0.dp, Color.Transparent),
//												modifier = Modifier.size(150.dp),
//												shape = MaterialTheme.shapes.small,
//												contentPadding = PaddingValues(0.dp),
//											) {
//												CoilImage(
//													component = imageComponent,
//													imageModel = { uri.toPath().toFile() },
//													imageLoader = { imageLoader },
//													modifier = Modifier.fillMaxSize(),
//
//													imageOptions = ImageOptions(
//														contentScale = ContentScale.Crop,
//														alignment = Alignment.Center,
//														contentDescription = "Original image",
//														requestSize = IntSize(1000, 1000),
//													)
//												)
//											}
//										}
//									}
//								}

								Column(
									modifier = Modifier
										.verticalScroll(verticalScrollState)
										.absolutePadding(right = 17.dp),

									verticalArrangement = Arrangement.spacedBy(10.dp)
								) {
									files.forEachIndexed { index, image ->
										Card {
											Row(
												verticalAlignment = Alignment.CenterVertically,
												modifier = Modifier
													.background(colors.SectionBackground, RoundedCornerShape(15.dp))
													.height(170.dp)
													.padding(10.dp)
											) {
												Column(
													verticalArrangement = Arrangement.spacedBy(10.dp)
												) {
													Spacer(Modifier.weight(1f))

													TextButton(
														onClick = {
															if (index > 0) {
																files.apply {
																	add(index - 1, removeAt(index))
																}
															}

															logger.info { "($index -> ${index - 1}) ${files.map { it.uri }}" }
														},

														enabled = index != 0
													) {
														Icon(
															Icons.Rounded.ArrowUpward,
															"Move up"
														)
													}

													TextButton(
														onClick = {
															if (index < files.size - 1) {
																files.apply {
																	add(index + 1, removeAt(index))
																}
															}

															logger.info { "($index -> ${index + 1}) ${files.map { it.uri }}" }
														},

														enabled = index != files.size - 1
													) {
														Icon(
															Icons.Rounded.ArrowDownward,
															"Move down"
														)
													}

													Spacer(Modifier.weight(1f))
												}

												Column(
													modifier = Modifier.width(250.dp),
													horizontalAlignment = Alignment.CenterHorizontally,
													verticalArrangement = Arrangement.spacedBy(10.dp)
												) {
													Spacer(Modifier.weight(1f))

													Text(
														image.uri.toPath().fileName.toString() + " ($index)",
														fontSize = TextUnit(1.25F, TextUnitType.Em),
														textAlign = TextAlign.Center,
														modifier = Modifier.fillMaxWidth()
													)

													EnumDropdown<ImageQuality>(
														default = ImageQuality.NOT_EVALUATED,
														selected = image.quality,
														buttonPrefix = "Quality: ",
														boxModifier = Modifier.absolutePadding(top = 10.dp),
														buttonModifier = Modifier.fillMaxWidth()
													) { selected ->
														logger.info { "(${image.quality.readableName} -> ${selected.readableName}) ${image.uri}" }

														image.quality = selected
													}

													Spacer(Modifier.weight(1f))
												}

												Spacer(Modifier.weight(1f))

												OutlinedButton(
													{
														Desktop.getDesktop()
															.browse(image.uri)
													},
													border = BorderStroke(0.dp, Color.Transparent),
													modifier = Modifier.size(150.dp),
													shape = MaterialTheme.shapes.small,
													contentPadding = PaddingValues(0.dp),
												) {
													CoilImage(
														component = imageComponent,
														imageModel = { image.uri.toPath().toFile() },
														imageLoader = { imageLoader },
														modifier = Modifier.fillMaxSize(),

														imageOptions = ImageOptions(
															contentScale = ContentScale.Crop,
															alignment = Alignment.Center,
															contentDescription = "Original image",
															requestSize = IntSize(1000, 1000),
														)
													)
												}
											}
										}
									}

								}

								VerticalScrollbar(
									modifier = Modifier.align(Alignment.CenterEnd),
									adapter = rememberScrollbarAdapter(verticalScrollState)
								)
							}

							Spacer(Modifier.weight(1f))
						}
					}
				}
			}
		}
	}
}
