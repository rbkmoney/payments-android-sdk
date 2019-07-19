/*
 *
 * Copyright 2019 RBKmoney
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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

