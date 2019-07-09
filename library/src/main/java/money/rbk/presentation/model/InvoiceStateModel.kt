package money.rbk.presentation.model

import androidx.annotation.StringRes

/**
 * @author Arthur Korchagin (artur.korchagin@simbirsoft.com)
 * @since 05.07.19
 */
sealed class InvoiceStateModel : BaseIUModel() {

    class Success(val paymentToolName: String, val email: String) : InvoiceStateModel()

    class Failed(@StringRes val reasonResId: Int) : InvoiceStateModel()

    object Pending : InvoiceStateModel()

}
