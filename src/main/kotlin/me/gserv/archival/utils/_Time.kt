/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.text.DateFormat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import javax.swing.text.DateFormatter

private val dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.SHORT);
private val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

fun LocalDateTime.Companion.now() =
    Clock.System.now().toLocalDateTime(TimeZone.UTC)

fun LocalDate.Companion.now() =
    LocalDateTime.now().date

fun LocalDateTime.format(): String =
    dateTimeFormatter.format(toJavaLocalDateTime())

fun LocalDate.format(): String =
    dateFormatter.format(toJavaLocalDate())
