/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.utils.serializers

import dev.brachtendorf.jimagehash.hash.Hash
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigInteger

@OptIn(ExperimentalSerializationApi::class)
class HashSerializer : KSerializer<Hash> {
	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Hash", PrimitiveKind.STRING)

	override fun serialize(
		encoder: Encoder,
		value: Hash
	) {
		encoder.encodeString(
			buildString {
				append(value.bitResolution)
				append("/")
				append(value.algorithmId)
				append("/")
				append(value.hashValue)
			}
		)
	}

	override fun deserialize(decoder: Decoder): Hash {
		val parts = decoder.decodeString().split("/")

		return Hash(
			/* hashValue = */ BigInteger(parts[2]),
			/* hashLength = */ parts[0].toInt(),
			/* algorithmId = */ parts[1].toInt(),
		)
	}
}
