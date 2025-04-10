/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.utils

import dev.brachtendorf.jimagehash.hash.Hash
import dev.brachtendorf.jimagehash.hashAlgorithms.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import me.gserv.archival.utils.serializers.HashSerializer
import java.awt.image.BufferedImage

private const val hashBits = 64

fun getHashes(image: BufferedImage) = Hashes(
	average = image.averageHash(),
	difference = image.differenceHash(),
	median = image.medianHash(),
	perceptive = image.perceptiveHash(),
	rotational = image.rotationalHash(),
)

fun Hash.encodeToString() =
	Json.encodeToString(HashContainer(this))

fun String.decodeToHash() =
	Json.decodeFromString<HashContainer>(this).hash

fun BufferedImage.averageHash(): Hash =
	AverageHash(hashBits)
		.hash(this)

fun BufferedImage.differenceHash(): Hash =
	DifferenceHash(hashBits, DifferenceHash.Precision.Triple)
		.hash(this)

fun BufferedImage.medianHash(): Hash =
	MedianHash(hashBits)
		.hash(this)

fun BufferedImage.perceptiveHash(): Hash =
	PerceptiveHash(hashBits)
		.hash(this)

fun BufferedImage.rotationalHash(): Hash =
	RotPHash(hashBits)
		.hash(this)

@Serializable
data class HashContainer(
	@Serializable(with = HashSerializer::class)
	val hash: Hash
)

@Serializable
data class Hashes(
	@Serializable(with = HashSerializer::class)
	val average: Hash,

	@Serializable(with = HashSerializer::class)
	val difference: Hash,

	@Serializable(with = HashSerializer::class)
	val median: Hash,

	@Serializable(with = HashSerializer::class)
	val perceptive: Hash,

	@Serializable(with = HashSerializer::class)
	val rotational: Hash,
) {
	fun encode() = EncodedHashes(
		average = average.encodeToString(),
		difference = difference.encodeToString(),
		median = median.encodeToString(),
		perceptive = perceptive.encodeToString(),
		rotational = rotational.encodeToString(),
	)
}

@Serializable
data class EncodedHashes(
	val average: String,
	val difference: String,
	val median: String,
	val perceptive: String,
	val rotational: String,
) {
	fun decode() = Hashes(
		average = average.decodeToHash(),
		difference = difference.decodeToHash(),
		median = median.decodeToHash(),
		perceptive = perceptive.decodeToHash(),
		rotational = rotational.decodeToHash(),
	)
}
