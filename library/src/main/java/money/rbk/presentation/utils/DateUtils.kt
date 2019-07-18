package money.rbk.presentation.utils

object DateUtils {
    private const val CHAR_ZERO = '0'

    fun formatMonthYear(month: Int, year: Int) =
        "${(month + 1).toString().padStart(2, CHAR_ZERO)}/${year % 100}"
}
