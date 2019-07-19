package money.rbk.presentation.model

import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.PaymentData

internal class PaymentDataTaskModel(val paymentDataTask: Task<PaymentData>) : BaseIUModel()
