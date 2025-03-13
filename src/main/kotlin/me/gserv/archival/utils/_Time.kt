/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.utils

import kotlinx.datetime.*
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private val dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.SHORT)
private val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)

fun LocalDateTime.Companion.now() =
	Clock.System.now().toLocalDateTime(TimeZone.UTC)

fun LocalDate.Companion.now() =
	LocalDateTime.now().date

val Instant.Companion.ZERO: Instant
	get() = fromEpochMilliseconds(0)

val LocalDateTime.Companion.ZERO: LocalDateTime
	get() = Instant.ZERO.toLocalDateTime(TimeZone.UTC)

val LocalDate.Companion.ZERO: LocalDate
	get() = LocalDateTime.ZERO.date

fun LocalDateTime.Companion.fromEpochMilliseconds(millis: Long) =
	Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC)

fun LocalDate.Companion.fromEpochMilliseconds(millis: Long) =
	LocalDateTime.fromEpochMilliseconds(millis).date

fun LocalDateTime.format(): String =
	dateTimeFormatter.format(toJavaLocalDateTime())

fun LocalDate.format(): String =
	dateFormatter.format(toJavaLocalDate())
