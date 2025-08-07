/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.utils

import me.gserv.archival.config.AppConfig
import java.nio.file.Path
import kotlin.io.path.absolute
import kotlin.io.path.relativeTo

fun Path.relativeToDataDir(): Path =
	absolute().relativeTo(Path.of(AppConfig.dataFolder).absolute())
