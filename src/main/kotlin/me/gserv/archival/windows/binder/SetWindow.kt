/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.windows.binder

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import io.github.kdroidfilter.platformtools.darkmodedetector.windows.setWindowsAdaptiveTitleBar
import io.github.oshai.kotlinlogging.KotlinLogging
import me.gserv.archival.Colors
import me.gserv.archival.data.Database
import me.gserv.archival.data.GlobalState
import me.gserv.archival.data.entities.Set
import me.gserv.archival.dropTarget
import me.gserv.archival.utils.components.PrimaryButton
import me.gserv.archival.windows.BinderWindow
import java.awt.Dimension
import javax.imageio.ImageIO

class SetWindow(val parent: BinderWindow) {
	val logger = KotlinLogging.logger { }

	var isOpen by mutableStateOf(false)
	var setFiles by mutableStateOf(SnapshotStateList<Set.FileContainer>())

	val state = WindowState(
		size = DpSize(1000.dp, 700.dp),
	)

	lateinit var scope: FrameWindowScope

	fun close() {
		isOpen = false
		GlobalState.set = null

		parent.show()
	}

	fun open(currentSet: Set) {
		GlobalState.set = currentSet
		isOpen = true
	}

	@Composable
	fun create() {
		logger.info { "Creating window..." }

		if (isOpen) {
			logger.info { "Window is open!" }

			parent.hide()

			LaunchedEffect(GlobalState.set) {
				if (GlobalState.set != null) {
					dropTarget.state {
						smallText = "Set"
						bigText = GlobalState.set!!.id.value.toString()
					}

					Database.transaction {
						setFiles = GlobalState.set!!.getFiles().toMutableStateList()
					}
				}

			}

			Window(
				::close,
				state = state,
				title = "Set ${GlobalState.set?.id?.value} (Binder ${GlobalState.binder?.id?.value})"
			) {
				scope = this
				window.minimumSize = Dimension(1000, 700)
				window.setWindowsAdaptiveTitleBar()

				val itemHeight = 200.dp

				Colors.Theme { colors ->
					Box(Modifier.background(colors.WindowBackground).fillMaxSize()) {
						Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
							Box(
								Modifier
									.background(colors.SectionBackground, RoundedCornerShape(0.dp, 0.dp, 15.dp, 15.dp))
									.fillMaxWidth()
							) {
								Row(
									Modifier.padding(15.dp),
									verticalAlignment = Alignment.CenterVertically
								) {
									Text(
										"Set ${GlobalState.set?.id?.value} (${setFiles.size} scans)",
										fontSize = 1.5.em,

										modifier = Modifier
											.height(50.dp)
											.wrapContentHeight(Alignment.CenterVertically),
									)

									Spacer(Modifier.weight(1f, true))

									PrimaryButton({}) {
										Row(
											horizontalArrangement = Arrangement.spacedBy(10.dp),
											verticalAlignment = Alignment.CenterVertically,
										) {
											Icon(
												Icons.Rounded.Add,
												""
											)

											Text("Add Scan")
										}
									}
								}
							}

							if (!setFiles.isEmpty()) {
								LazyColumn(
									Modifier.fillMaxWidth()
										.absolutePadding(left = 10.dp, right = 10.dp, bottom = 10.dp),
									verticalArrangement = Arrangement.spacedBy(10.dp)
								) {
									items(setFiles) { files ->
										Box(Modifier.fillMaxWidth()) {
											Row(
												horizontalArrangement = Arrangement.spacedBy(10.dp)
											) {
												Text(
													files.index.toString().padStart(3, '0'),
													modifier = Modifier
														.background(colors.SectionBackground, RoundedCornerShape(15.dp))
														.padding(vertical = 10.dp, horizontal = 15.dp)
														.height(itemHeight)
														.width(100.dp)
														.wrapContentHeight(Alignment.CenterVertically),
													textAlign = TextAlign.Center,
													fontSize = 1.5.em,
												)

												Box(
													Modifier
														.background(colors.SectionBackground, RoundedCornerShape(15.dp))
														.align(Alignment.CenterVertically)
														.padding(vertical = 10.dp)
														.height(itemHeight)
														.fillMaxWidth(0.5f)
												) {
													if (files.original != null) {
														Image(
															ImageIO.read(files.original).toPainter(),
															"Original scan",
															modifier = Modifier
																.height(itemHeight)
																.fillMaxWidth()
																.padding(horizontal = 15.dp),
														)
													} else {
														Text(
															"Original Scan Missing",
															modifier = Modifier
																.fillMaxHeight()
																.fillMaxWidth()
																.wrapContentHeight(Alignment.CenterVertically),
															textAlign = TextAlign.Center,
															fontSize = 1.5.em,
														)
													}
												}

												Box(
													Modifier
														.background(colors.SectionBackground, RoundedCornerShape(15.dp))
														.align(Alignment.CenterVertically)
														.padding(vertical = 10.dp)
														.height(itemHeight)
														.fillMaxWidth()
												) {
													if (files.edit != null) {
														Image(
															ImageIO.read(files.edit).toPainter(),
															"Edited scan",
															modifier = Modifier
																.height(itemHeight)
																.fillMaxWidth()
																.padding(horizontal = 15.dp),
														)
													} else {
														Text(
															"Edited Scan Missing",
															modifier = Modifier
																.fillMaxHeight()
																.fillMaxWidth()
																.wrapContentHeight(Alignment.CenterVertically),
															textAlign = TextAlign.Center,
															fontSize = 1.5.em,
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
			}
		}
	}
}
