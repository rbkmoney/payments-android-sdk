package money.rbk.data.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicReference

/**
 * @author Arthur Korchagin (artur.korchagin@simbirsoft.com)
 * @since 27.06.19
 */
private val API_DATE_FORMAT = DateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
private val API_DATE_SHORT_FORMAT = DateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")

private class DateFormat(pattern: String) {
    private val format = AtomicReference(SimpleDateFormat(pattern, Locale.getDefault()))

    fun formatDate(date: Date) = format.get().format(date)
    fun parseDate(date: String) = format.get().parse(date)
}

fun String.parseApiDate(): Date = API_DATE_FORMAT.parseDate(this)

fun String.parseShortApiDate(): Date = API_DATE_SHORT_FORMAT.parseDate(this)
