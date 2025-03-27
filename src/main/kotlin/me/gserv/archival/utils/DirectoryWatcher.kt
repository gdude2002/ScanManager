/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.utils

import androidx.compose.runtime.snapshots.SnapshotStateList
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import java.net.URI
import java.nio.file.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

class DirectoryWatcher(val path: Path) : CoroutineScope {
	override val coroutineContext: CoroutineContext = Dispatchers.IO

	private val logger = KotlinLogging.logger {}
	private var shouldStop = false

	val files = SnapshotStateList<URI>()

	val service: WatchService = FileSystems.getDefault().newWatchService()
	var job: Job? = null

	init {
		path.register(
			service,
			StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE,
			StandardWatchEventKinds.OVERFLOW,
		)
	}

	fun start() {
		shouldStop = false

		launch {
			runInterruptible(coroutineContext) {
				while(true) {
					val key: WatchKey? = service.poll(100L, TimeUnit.MILLISECONDS)

					if (shouldStop) {
						logger.debug { "Stop requested, closing service." }

						service.close()
						break
					}

					if (key == null) {
						continue
					}

					try {
						for (event in key.pollEvents()) {
							try {
								when(event.kind()) {
									StandardWatchEventKinds.ENTRY_CREATE -> {
										val filePath = path.resolve(event.context() as Path).toAbsolutePath()

										logger.debug { "File created: $filePath"}
										files.add(filePath.toUri())
									}

									StandardWatchEventKinds.ENTRY_DELETE -> {
										val filePath = path.resolve(event.context() as Path).toAbsolutePath()

										logger.debug { "File deleted: $filePath"}
										files.remove(filePath.toUri())
									}

									StandardWatchEventKinds.OVERFLOW -> logger.warn {
										"Overflow detected, some events may be missing. Event count: ${event.count()}"
									}
								}
							} catch (e: Exception) {
								logger.error(e) {
									"Error processing ${event.kind().name()} event for ${event.context()}"
								}
							}
						}
					} finally {
						key.reset()
					}
				}
			}
		}
	}

	fun stop() {
		shouldStop = true
	}
}
