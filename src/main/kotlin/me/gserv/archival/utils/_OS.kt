/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.utils

enum class OS(val envSep: String) {
	Windows(";"),
	MacOS(":"),
	Linux(";"),
	Solaris(";"),
	Unknown(";"),
}

val currentOsString = System.getProperty("os.name")
val lower = currentOsString.lowercase()

val currentOs = when {
	lower.contains("win") -> OS.Windows
	lower.contains("mac") -> OS.MacOS
	lower.contains("sunos") -> OS.Solaris

	lower.contains("nix") || lower.contains("nux") || lower.contains("aix") -> OS.Linux

	else -> OS.Unknown
}
