package money.rbk.presentation.screen.gpay

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.WalletConstants
import kotlinx.android.synthetic.main.fmt_google_pay.*
import money.rbk.R
import money.rbk.di.Injector
import money.rbk.presentation.model.BrowserRequestModel
import money.rbk.presentation.screen.base.BaseFragment

/**
 * @author Arthur Korchagin (artur.korchagin@simbirsoft.com)
 * @since 27.06.19
 */

class GpayFragment : BaseFragment<GpayView>(), GpayView {

    override fun showRedirect(request: BrowserRequestModel) {
    }

    override val presenter: GpayPresenter by lazy { GpayPresenter(navigator) }

    private val userEmail = Injector.email

    companion object {
        fun newInstance() = GpayFragment()

        val LOAD_PAYMENT_DATA_REQUEST_CODE = 123

        val SUPPORTED_PAYMENT_METHODS = listOf(
            WalletConstants.PAYMENT_METHOD_CARD,
            WalletConstants.PAYMENT_METHOD_TOKENIZED_CARD
        )

        val SUPPORTED_NETWORKS = listOf(
            WalletConstants.CARD_NETWORK_VISA,
            WalletConstants.CARD_NETWORK_AMEX,
            WalletConstants.CARD_NETWORK_MASTERCARD,
            WalletConstants.CARD_NETWORK_DISCOVER,
            WalletConstants.CARD_NETWORK_INTERAC,
            WalletConstants.CARD_NETWORK_JCB
        )
    }

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fmt_google_pay, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        edEmail.setText(userEmail)
        presenter.init(view.context, this)
    }

    override fun onReadyToPay() {
        btnPay.setOnClickListener {
            btnPay.isClickable = false
            presenter.performPayment()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            LOAD_PAYMENT_DATA_REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK ->
                        presenter.onGpayPaymentSuccess(data, edEmail.text.toString())

                    Activity.RESULT_CANCELED -> {
                        // Nothing to do here normally - the user simply cancelled without selecting a
                        // payment method.
                    }

                    AutoResolveHelper.RESULT_ERROR ->
                        presenter.onGpayPaymentError(data)
                }
                btnPay.isClickable = true
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun showProgress() {
        btnPay.isClickable = false
        pbLoading.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        btnPay.isClickable = true
        pbLoading.visibility = View.GONE
    }

}
