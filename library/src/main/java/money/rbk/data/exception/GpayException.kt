package money.rbk.data.exception

import com.google.android.gms.common.api.Status

internal sealed class GpayException(message: String? = null, cause: Throwable? = null) :
    Exception(message, cause) {

    internal object GpayPaymentMethodTokenException :
        GpayException("Google pay can't receive paymentMethodToken")

    internal object GpayNotReadyException : GpayException("Google pay is not ready")

    internal class GpayCantPerformPaymentException(status: Status?) :
        GpayException("Google pay cant perform payment " +
            status?.run { "status:$statusCode message:$statusMessage" }.orEmpty())
}
