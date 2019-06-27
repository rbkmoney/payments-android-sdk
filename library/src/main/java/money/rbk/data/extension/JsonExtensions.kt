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

package money.rbk.data.extension

import money.rbk.data.serialization.Deserializer
import money.rbk.data.utils.parseApiDate
import org.json.JSONArray
import org.json.JSONObject

internal fun JSONArray.toStringList(): List<String> {
    val result = ArrayList<String>(length())
    for (i in 0 until length()) {
        result.add(getString(i))
    }
    return result
}

internal fun JSONObject.parseStringList(key: String) =
    getJSONArray(key).toStringList()

internal inline fun <reified T> JSONObject.getNullable(key: String): T? = opt(key) as? T

internal inline fun <reified T> JSONObject.parseNullable(key: String,
    parser: Deserializer<JSONObject, T>): T? = optJSONObject(key)?.let(parser::fromJson)

internal inline fun <reified T> JSONObject.parseString(key: String,
    parser: Deserializer<String, T>): T = getString(key).let(parser::fromJson)

internal inline fun <reified T> JSONObject.parse(key: String,
    parser: Deserializer<JSONObject, T>): T = getJSONObject(key).let(parser::fromJson)

internal inline fun <reified T> JSONObject.parseNullableString(key: String,
    parser: Deserializer<String, T>): T? =
    optString(key)
        ?.takeIf(CharSequence::isNotBlank)
        ?.let(parser::fromJson)

internal inline fun <reified T, O> JSONObject.parseList(key: String, parser: Deserializer<O, T>) =
    parser.fromJsonArray(getJSONArray(key))

internal fun JSONObject.parseDate(key: String) =
    getString(key).parseApiDate()

internal inline fun <reified T, O> JSONObject.parseNullableList(key: String,
    parser: Deserializer<O, T>): List<T>? = optJSONArray(key)?.let(parser::fromJsonArray)
