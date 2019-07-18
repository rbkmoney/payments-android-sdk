package money.rbk.data.serialization

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import java.lang.reflect.Type
import kotlin.reflect.KClass

internal class SealedJsonDeserializer<T : Any>(private val distributor: SealedDistributor<T>) :
    JsonDeserializer<T> {

    override fun deserialize(json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext): T {

        val fieldValue = (json.asJsonObject.get(distributor.field) as? JsonPrimitive)?.asString
            ?: throw JsonParseException("Unable to find key field ${distributor.field}")

        val fool =
            distributor.values.find { fieldValue == it.name } ?: if (distributor.fallback != null) {
                return distributor.fallback
            } else {
                throw JsonParseException("Unable to parse $typeOfT")
            }

        return context.deserialize(json, fool.kClass.java)
    }
}

internal class SealedDistributor<T : Any>(val field: String,
    val values: Array<out SealedDistributorValue<T>>,
    val fallback: T? = null)

internal interface SealedDistributorValue<T : Any> {
    val name: String
    val kClass: KClass<out T>
}

