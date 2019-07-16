package money.rbk.data.exception

import com.google.android.gms.common.api.Status

/**
 * @author Arthur Korchagin (artur.korchagin@simbirsoft.com)
 * @since 02.07.19
 */

sealed class GpayException(message: String? = null, cause: Throwable? = null) :
    Exception(message, cause) {

    internal object GpayPaymentMethodTokenException :
        GpayException("Google pay can't receive paymentMethodToken")

    internal object GpayNotReadyException : GpayException("Google pay is not ready")

    internal class GpayCantPerformPaymentException(status: Status?) :
        GpayException("Google pay cant perform payment " +
            status?.run { "status:$statusCode message:$statusMessage" }.orEmpty())
}
