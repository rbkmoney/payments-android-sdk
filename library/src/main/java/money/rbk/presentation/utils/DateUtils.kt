package money.rbk.presentation.utils

object DateUtils {
    fun formatMonthYear(month: Int, year: Int) =
        "${(month + 1).toString().padStart(2, '0')}/${year % 100}"
}
