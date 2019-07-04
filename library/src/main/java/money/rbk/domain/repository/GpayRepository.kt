package money.rbk.domain.repository

import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.PaymentData
import money.rbk.domain.entity.Currency

/**
 * @author Arthur Korchagin (artur.korchagin@simbirsoft.com)
 * @since 02.07.19
 */
internal interface GpayRepository {

    val gatewayMerchantId: String

    fun checkReadyToPay(): Task<Boolean>

    fun loadPaymentData(price: String, currency: Currency): Task<PaymentData>

    fun init(shopId: String)

}