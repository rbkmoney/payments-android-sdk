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

