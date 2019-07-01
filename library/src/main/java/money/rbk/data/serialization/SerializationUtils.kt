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

import money.rbk.data.utils.parseApiDate
import org.json.JSONArray
import org.json.JSONObject
import java.util.Date

class JsonBuilder(val jsonObject : JSONObject) {

    infix fun String.set(value: Int?) {
        if (value != null) {
            jsonObject.put(this, value)
        }
    }

    infix fun String.set(value: Int) {
        jsonObject.put(this, value)
    }

    infix fun String.setNotNull(value: String?) {
        if (value != null) {
            jsonObject.put(this, value)
        }
    }

    infix fun String.set(value: String) {
        jsonObject.put(this, value)
    }

    infix fun String.set(value: Boolean?) {
        if (value != null) {
            jsonObject.put(this, value)
        }
    }

    infix fun String.set(value: Boolean) {
        jsonObject.put(this, value)
    }

    infix fun String.setNotNull(value: JSONObject?) {
        if (value != null) {
            jsonObject.put(this, value)
        }
    }

    infix fun String.set(value: JSONObject) {
        jsonObject.put(this, value)
    }

    infix fun String.set(value: Serializable) {
        jsonObject.put(this, value.toJson())
    }


    infix fun String.setNotNull(value: JSONArray?) {
        if (value != null) {
            jsonObject.put(this, value)
        }
    }

    infix fun String.set(value: JSONArray) {
        jsonObject.put(this, value)

    }
}

inline fun <reified T> jsonArray(vararg values: T): JSONArray =
    JSONArray().apply {
        values.forEach { put(it) }
    }

inline fun json(jsonObject: JSONObject = JSONObject(), jsonBuilderCallback: JsonBuilder.() -> Unit): JSONObject =
    JsonBuilder(jsonObject).also(jsonBuilderCallback).jsonObject

inline operator fun <reified T, reified D> JSONObject.invoke(key: String,
    parser: Deserializer<D, T>): T {
    if (isNull(key)) return null as T

    return when (D::class) {
        String::class -> optString(key, null)?.let { parser.fromJson(it as D) } as T
        JSONObject::class -> optJSONObject(key)?.let { parser.fromJson(it as D) } as T
        JSONArray::class -> optJSONArray(key)?.let { parser.fromJson(it as D) } as T
        else -> throw RuntimeException("Unable to parse")
    }
}

inline operator fun <reified T> JSONObject.invoke(key: String): T {
    if (isNull(key)) return null as T

    return when (val returnClass = T::class) {
        String::class -> optString(key, null) as T
        Int::class -> optInt(key, 0) as T
        Boolean::class -> optBoolean(key, false) as T
        Double::class -> optDouble(key, 0.0) as T
        Long::class -> optLong(key, 0L) as T
        Date::class -> optString(key, null)?.parseApiDate() as T
        else -> throw RuntimeException("Unable to parse ${returnClass.java.name}")
    }
}
