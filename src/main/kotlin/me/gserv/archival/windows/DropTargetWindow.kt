/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

@file:OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)

package me.gserv.archival.windows

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOff
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import me.gserv.archival.config.AppConfig
import me.gserv.archival.data.GlobalState
import java.awt.geom.RoundRectangle2D

class DropTargetWindow(val parent: MainWindow) {
    lateinit var state: WindowState
    lateinit var tooltipState: WindowState

    var isHovered by mutableStateOf(true)
    var tooltipText by mutableStateOf<String?>(null)
    var currentSet by mutableStateOf(0)

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
            val size = with(LocalDensity.current) {
                100.dp.toPx()
            }

            window.shape = RoundRectangle2D.Float(0f, 0f, size, size, size, size)

            var backgroundColor = MaterialTheme.colors.primary
            var borderColor = Color.White

            if (isHovered) {
                backgroundColor = backgroundColor.copy(alpha = 0.8f)
                borderColor = borderColor.copy(alpha = 0.5f)
            }

            WindowDraggableArea(
                Modifier.fillMaxSize()
                    .clip(CircleShape)
                    .background(Color.Transparent, CircleShape)
            ) {
                Box(
                    Modifier.fillMaxSize()
                        .clip(CircleShape)
                        .background(backgroundColor, CircleShape)
                        .border(3.dp, borderColor, CircleShape)
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
                        Spacer(Modifier.weight(1f, true))

                        if (AppConfig.dataFolder == null) {
                            Icon(
                                Icons.Default.FolderOff, "",
                                tint = borderColor,
                            )

                            Text(
                                "No data\nfolder",
                                fontSize = 0.75.em,
                                color = borderColor,
                                textAlign = TextAlign.Center
                            )
                        } else if (GlobalState.binder == null) {
                            Icon(
                                Icons.Default.FolderOff, "",
                                tint = borderColor,
                            )

                            Text(
                                "No binder\nselected",
                                fontSize = 0.75.em,
                                color = borderColor,
                                textAlign = TextAlign.Center
                            )
                        } else if (GlobalState.sets.isEmpty()) {
                            Icon(
                                Icons.Default.QuestionMark, "",
                                tint = borderColor,
                            )

                            Text(
                                "No sets\nin binder",
                                fontSize = 0.75.em,
                                color = borderColor,
                                textAlign = TextAlign.Center
                            )
                        } else {

                            Text(
                                "Selected set",
                                fontSize = 0.75.em,
                                color = borderColor,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                "${GlobalState.sets[currentSet].id}",
                                fontSize = 1.25.em,
                                color = borderColor,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(Modifier.weight(1f, true))
                    }
                }
            }
        }
    }

    fun onLeftClick(event: PointerEvent) {

    }

    fun onRightClick(event: PointerEvent) {

    }

    fun onMiddleClick(event: PointerEvent) {

    }

    fun onForwardClick(event: PointerEvent) {
        onScrollUp(event)
    }

    fun onBackClick(event: PointerEvent) {
        onScrollDown(event)
    }

    fun onScrollUp(event: PointerEvent) {
        if (GlobalState.sets.isEmpty() || currentSet == GlobalState.sets.size - 1) {
            return
        }

        currentSet += 1
    }

    fun onScrollDown(event: PointerEvent) {
        if (GlobalState.sets.isEmpty() || currentSet == 0) {
            return
        }

        currentSet -= 1
    }

    fun onMouseEnter(event: PointerEvent) {
        isHovered = false
    }

    fun onMouseExit(event: PointerEvent) {
        isHovered = true
    }
}
