package money.rbk.presentation.model

import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.PaymentData

/**
 * @author Arthur Korchagin (artur.korchagin@simbirsoft.com)
 * @since 02.07.19
 */
class PaymentDataTaskModel(val PaymentDataTask: Task<PaymentData>) : BaseIUModel()
