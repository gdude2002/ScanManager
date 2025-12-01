/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.m3.views

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.gserv.archival.m3.components.MDText
import me.gserv.archival.m3.components.MainHeader

@Composable
fun aboutView() {
	Column {
		Row {
			MainHeader(
				"About This Software",
				textAlign = TextAlign.Center,
				modifier = Modifier
					.weight(1f)
			)
		}

		val scrollState = rememberScrollState()

		Row(
			Modifier
				.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
				.padding(10.dp)
		) {
			Box {
				Column(
					Modifier
						.verticalScroll(scrollState)
						.absolutePadding(right = 17.dp)
				) {
					Row {
						MDText(
							"""
						    # Scan Manager
						    This is a piece of open-source software, licensed under the EUPL 1.2.
							You can find the source code [on GitHub](https://github.com/gdude2002/ScanManager).

						    ## Acknowledgements
							This software relies on (and contains) the following Kotlin and Java libraries:

							- [Batik](https://xmlgraphics.apache.org/batik/) (Apache 2 Licence)
							- [Exposed](https://www.jetbrains.com/exposed/) (Apache 2 Licence)
							- [FileKit](https://github.com/vinceglb/FileKit) (MIT Licence)
							- [Flyway](https://github.com/flyway/flyway) (Apache 2 Licence)
							- [H2 Database Engine](https://github.com/h2database/h2database) (MPL 2 / EPL 1 Licence)
							- [Image Comparison](https://github.com/romankh3/image-comparison) (Apache 2 Licence)
							- [JImageHash](https://github.com/KilianB/JImageHash) (MIT Licence)
							- [Jackson Data Formats: YAML](https://github.com/FasterXML/jackson-dataformats-text) (Apache 2 Licence)
							- [Jansi](https://github.com/fusesource/jansi) (Apache 2 Licence)
							- [JavaFX](https://openjfx.io/) (GPL 2 Licence)
							- [JetBrains Compose](https://www.jetbrains.com/compose-multiplatform/) (Apache 2 Licence)
							- [Jetpack Compose](https://developer.android.com/compose) (Apache 2 Licence)
							- [Kotlin Logging](https://github.com/oshai/kotlin-logging) (Apache 2 Licence)
							- [Kotlin](https://kotlinlang.org/) (Apache 2 Licence)
							- [Landscapist](https://github.com/skydoves/landscapist) (Apache 2 Licence)
							- [LibRawFX](https://github.com/lanthale/LibRawFX) (LGPL 2.1 Licence)
							- [Log4J](https://logging.apache.org/log4j/) (Apache 2 Licence)
							- [Multiplatform Markdown Renderer](https://github.com/mikepenz/multiplatform-markdown-renderer) (Apache 2 Licence)
							- [NightMonkeys](https://github.com/gotson/NightMonkeys) (MIT Licence)
							- [PlatformTools](https://github.com/kdroidFilter/Platform-Tools) (MIT Licence)
							- [QOI Java](https://github.com/saharNooby/qoi-java) (MIT Licence)
							- [Reorderable](https://github.com/Calvin-LL/Reorderable) (Apache 2 Licence)
							- [SLF4J](https://www.slf4j.org/) (MIT Licence)
							- [Slugify](https://github.com/slugify/slugify) (Apache 2 Licence)
							- [TwelveMonkeys](https://github.com/haraldk/TwelveMonkeys/) (BSD 3-Clause Licence)

							This software also relies on (and contains) the following native libraries:

							- [Brotli](https://github.com/google/brotli) (MIT Licence)
							- [Highway](https://github.com/google/highway) (Apache 2 / BSD 3-Clause Licence)
							- [JasPer](https://github.com/jasper-software/jasper) (JasPer Licence Version 2.0)
							- [Little CMS](https://github.com/mm2/Little-CMS) (BSD 3-Clause Licence)
							- [libaom](https://github.com/mozilla/aom) (BSD 2-Clause Licence)
							- [libjpeg](https://github.com/winlibs/libjpeg) (Independent JPEG Group / BSD 3-Clause Licence)
							- [libjxl](https://github.com/libjxl/libjxl) (BSD 3-Clause Licence)
							- [zlib](https://github.com/madler/zlib) (zlib Licence)

							This software is based in part on the work of the Independent JPEG Group.
							"""
						)
					}
				}

				VerticalScrollbar(
					modifier = Modifier.align(Alignment.CenterEnd),
					adapter = rememberScrollbarAdapter(scrollState)
				)
			}
		}
	}
}
