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

import java.util.Calendar

internal object ValidationUtils {

    const val MAX_YEARS_CARD_VALIDITY = 30

    fun isValidYearMonth(month: Int, year: Int): Boolean {
        val currentDate = Calendar.getInstance()
        val currentYear = currentDate.get(Calendar.YEAR)
        val currentMonth = currentDate.get(Calendar.MONTH)

        return when {
            month < 0 || month > 12 -> false
            currentYear > year -> false
            currentYear + MAX_YEARS_CARD_VALIDITY < year -> false
            currentYear == year -> month >= currentMonth
            else -> true
        }
    }

    fun isValidYearMonth(yearMonth: String): Boolean {
        if (yearMonth.isBlank() || yearMonth.length != 5) {
            return false
        }

        val (month, year) = yearMonth.split("/")

        return isValidYearMonth((month.toIntOrNull() ?: 0) - 1, (year.toIntOrNull() ?: -1) + 2000)
    }

}

