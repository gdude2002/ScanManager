/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

@file:OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)

package me.gserv.archival.windows

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.rememberWindowState
import io.github.oshai.kotlinlogging.KotlinLogging
import me.gserv.archival.Colors
import me.gserv.archival.data.GlobalState
import me.gserv.archival.utils.isInteger
import java.awt.geom.RoundRectangle2D

class DropTargetWindow(val parent: MainWindow) {
	val logger = KotlinLogging.logger { }

	lateinit var state: WindowState
	lateinit var tooltipState: WindowState

	var isHovered by mutableStateOf(true)
	var tooltipText by mutableStateOf<String?>(null)
	var currentSet by mutableStateOf(0)

	var bigText by mutableStateOf<String?>(null)
	var composableBody by mutableStateOf<(@Composable DropTargetWindow.() -> Unit)?>(null)
	var icon by mutableStateOf<ImageVector?>(null)
	var iconDescription by mutableStateOf<String?>(null)
	var loadingProgress by mutableStateOf<Float?>(1f)
	var smallText by mutableStateOf<String?>(null)

	fun clearState() {
		bigText = null
		composableBody = null
		icon = null
		iconDescription = null
		loadingProgress = 1f
		smallText = null
	}

	fun state(body: (@Composable DropTargetWindow.() -> Unit)? = null) {
		composableBody = {
			clearState()

			body?.invoke(this@DropTargetWindow)
		}
	}

	@Preview
	@Composable
	fun create() {
		LaunchedEffect(GlobalState.sets) {
			currentSet = 0
		}

		state = rememberWindowState(
			position = WindowPosition(BiasAlignment(0.975f, 0.975f)),
			size = DpSize(100.dp, 100.dp)
		)

		tooltipState = rememberWindowState(
			position = WindowPosition(Alignment.BottomEnd),
		)

		Window(
			onCloseRequest = {},
			alwaysOnTop = true,
			resizable = false,
			undecorated = true,
			transparent = true,
			state = state
		) {
			Colors.Theme { colors ->
				val size = with(LocalDensity.current) {
					100.dp.toPx()
				}

				window.shape = RoundRectangle2D.Float(0f, 0f, size, size, size, size)

				var backgroundColor = colors.Material.onPrimary
				var borderColor = colors.Material.primary

				if (isHovered) {
					backgroundColor = backgroundColor.copy(alpha = 0.8f)
					borderColor = borderColor.copy(alpha = 0.5f)
				}

				WindowDraggableArea(
					Modifier.fillMaxSize()
						.background(Color.Transparent, CircleShape)
						.clip(CircleShape)
				) {
					Box(
						Modifier.fillMaxSize()
							.background(backgroundColor, CircleShape)
							.clip(CircleShape)
							.pointerInput(Unit) {
								awaitPointerEventScope {
									while (true) {
										val event = awaitPointerEvent()

										when (event.type) {
											PointerEventType.Press -> {
												if (event.buttons.isPrimaryPressed) {
													onLeftClick(event)
												}

												if (event.buttons.isSecondaryPressed) {
													onRightClick(event)
												}

												if (event.buttons.isTertiaryPressed) {
													onMiddleClick(event)
												}

												if (event.buttons.isBackPressed) {
													onBackClick(event)
												}

												if (event.buttons.isForwardPressed) {
													onForwardClick(event)
												}
											}

											PointerEventType.Scroll -> {
												if (event.changes.any { it.scrollDelta.y > 0 }) {
													onScrollUp(event)
												}

												if (event.changes.any { it.scrollDelta.y < 0 }) {
													onScrollDown(event)
												}
											}

											PointerEventType.Enter -> onMouseEnter(event)
											PointerEventType.Exit -> onMouseExit(event)
										}
									}
								}
							}
					) {
						if (loadingProgress != null) {
							CircularProgressIndicator(
								color = borderColor,
								trackColor = backgroundColor,
								progress = { loadingProgress ?: 0f },
								modifier = Modifier.fillMaxSize()
							)
						} else {
							CircularProgressIndicator(
								color = borderColor,
								trackColor = backgroundColor,
								modifier = Modifier.fillMaxSize()
							)
						}

						Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
							Spacer(Modifier.weight(1f, true))

							AnimatedContent(icon) {
								if (it != null) {
									Icon(it, iconDescription, tint = borderColor)
								}
							}

							AnimatedContent(
								smallText,
								transitionSpec = { slideTransition() }
							) {
								if (it != null) {
									Text(
										it,
										fontSize = 0.75.em,
										color = borderColor,
										textAlign = TextAlign.Center
									)
								}
							}

							AnimatedContent(
								bigText,
								transitionSpec = { slideTransition() }
							) {
								if (it != null) {
									Text(
										it,
										fontSize = 1.25.em,
										color = borderColor,
										textAlign = TextAlign.Center
									)
								}
							}

							composableBody?.invoke(this@DropTargetWindow)

							Spacer(Modifier.weight(1f, true))
						}
					}
				}
			}
		}
	}

	fun onLeftClick(event: PointerEvent) {
		logger.debug { "Left-click event received" }
	}

	fun onRightClick(event: PointerEvent) {
		logger.debug { "Right-click event received" }
	}

	fun onMiddleClick(event: PointerEvent) {
		logger.debug { "Middle-click event received" }
	}

	fun onForwardClick(event: PointerEvent) {
		logger.debug { "Forward button press event received" }

		onScrollDown(event)
	}

	fun onBackClick(event: PointerEvent) {
		logger.debug { "Back button press event received" }

		onScrollUp(event)
	}

	fun onScrollUp(event: PointerEvent) {
		logger.debug { "Scroll-up event received" }

		if (
			GlobalState.sets.isEmpty() ||
			currentSet == GlobalState.sets.size - 1
		) {
			return
		}

		currentSet += 1
	}

	fun onScrollDown(event: PointerEvent) {
		logger.debug { "Scroll-down event received" }

		if (
			GlobalState.sets.isEmpty() ||
			currentSet == 0
		) {
			return
		}

		currentSet -= 1
	}

	fun onMouseEnter(event: PointerEvent) {
		logger.debug { "Mouse cursor entered window" }

		isHovered = false
	}

	fun onMouseExit(event: PointerEvent) {
		logger.debug { "Mouse cursor exited window" }

		isHovered = true
	}

	// ...

	fun <S : String?> AnimatedContentTransitionScope<S>.slideTransition(): ContentTransform {
		var transition: ContentTransform = (  // Default Compose transform
			fadeIn(animationSpec = tween(220, delayMillis = 90)) + scaleIn(
				initialScale = 0.92f,
				animationSpec = tween(220, delayMillis = 90)
			)).togetherWith(
			fadeOut(animationSpec = tween(90))
		)

		if (initialState != null && targetState != null) {
			if (initialState!!.isInteger() && targetState!!.isInteger()) {
				val initialInt = initialState!!.toLong()
				val targetInt = targetState!!.toLong()

				transition = if (targetInt > initialInt) {
					(slideInHorizontally { width -> width } + fadeIn() togetherWith
						slideOutHorizontally { width -> -width } + fadeOut())
				} else {
					(slideInHorizontally { width -> -width } + fadeIn() togetherWith
						slideOutHorizontally { width -> width } + fadeOut())
				}.using(SizeTransform(clip = false))
			}
		}

		return transition
	}
}
