package aguDataSystem.server.http.models.response

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive

object ByteArrayAsEmptyArraySerializer : KSerializer<ByteArray> {
	override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ByteArrayAsEmptyArray")

	override fun serialize(encoder: Encoder, value: ByteArray) {
		val jsonEncoder = encoder as? JsonEncoder
		if (jsonEncoder != null) {
			val jsonArray = JsonArray(value.map { JsonPrimitive(it.toInt()) })
			jsonEncoder.encodeJsonElement(jsonArray)
		} else {
			encoder.encodeString(value.joinToString(",") { it.toString() })
		}
	}

	override fun deserialize(decoder: Decoder): ByteArray {
		val jsonDecoder = decoder as? JsonDecoder
		return if (jsonDecoder != null) {
			val jsonElement = jsonDecoder.decodeJsonElement()
			when {
				jsonElement is JsonArray && jsonElement.isEmpty() -> ByteArray(0)
				jsonElement is JsonPrimitive && jsonElement.isString && jsonElement.content.isEmpty() -> ByteArray(0)
				jsonElement is JsonArray -> jsonElement.map { it.jsonPrimitive.int.toByte() }.toByteArray()
				else -> throw IllegalArgumentException("Unexpected JSON element: $jsonElement")
			}
		} else {
			val stringValue = decoder.decodeString()
			if (stringValue.isEmpty()) {
				ByteArray(0)
			} else {
				stringValue.split(",").map { it.toByte() }.toByteArray()
			}
		}
	}
}