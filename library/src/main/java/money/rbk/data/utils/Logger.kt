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

package money.rbk.data.utils

import android.util.Log
import money.rbk.BuildConfig

@Suppress("NOTHING_TO_INLINE")
inline fun log(tag: String, method: String, text: String) {
    if (BuildConfig.DEBUG) Log.d(tag, "-> $method ->$text")
}

@Suppress("NOTHING_TO_INLINE")
inline fun log(throwable: Throwable) {
    if (BuildConfig.DEBUG) throwable.printStackTrace()
}
