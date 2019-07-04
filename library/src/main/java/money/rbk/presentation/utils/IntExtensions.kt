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

package money.rbk.presentation.utils

import java.text.NumberFormat
import java.util.Locale

private val localFormat by lazy {
    NumberFormat.getInstance()
        .apply {
            minimumFractionDigits = 2
        }
}

private val internationalFormat by lazy {
    NumberFormat.getInstance(Locale.ENGLISH)
        .apply {
            minimumFractionDigits = 2
        }
}

internal fun Int.formatPrice(): String =
    localFormat.format(this / 100.0)

internal fun Int.formatInternationalPrice(): String =
    internationalFormat.format(this / 100.0)
