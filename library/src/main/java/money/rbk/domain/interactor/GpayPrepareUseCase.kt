package money.rbk.domain.interactor

import com.google.android.gms.common.api.ApiException
import money.rbk.data.exception.GpayException
import money.rbk.di.Injector
import money.rbk.domain.interactor.base.UseCase
import money.rbk.domain.interactor.input.EmptyInputModel
import money.rbk.domain.repository.GpayRepository
import money.rbk.presentation.model.CheckoutInfoModel
import money.rbk.presentation.model.GpayPrepareInfoModel

/**
 * @author Arthur Korchagin (artur.korchagin@simbirsoft.com)
 * @since 02.07.19
 */
internal class GpayPrepareUseCase(
    private val gpayRepository: GpayRepository = Injector.gpayRepository,
    private val checkoutStateUseCase: UseCase<EmptyInputModel, CheckoutInfoModel> = CheckoutStateUseCase()
) : UseCase<EmptyInputModel, GpayPrepareInfoModel>() {

    override fun invoke(inputModel: EmptyInputModel,
        onResultCallback: (GpayPrepareInfoModel) -> Unit,
        onErrorCallback: (Throwable) -> Unit) {

        val onCheckoutInfoCallback = { checkoutInfoModel: CheckoutInfoModel ->
            onResultCallback(GpayPrepareInfoModel(gpayRepository.gatewayMerchantId, checkoutInfoModel))
        }

        gpayRepository.checkReadyToPay()
            .addOnCompleteListener { task ->
                try {
                    val result = task.getResult(ApiException::class.java)
                    if (result == true) {
                        checkoutStateUseCase(inputModel, onCheckoutInfoCallback, onErrorCallback)
                    } else {
                        onErrorCallback(GpayException.GpayNotReadyException)
                    }
                } catch (e: ApiException) {
                    onErrorCallback(e)
                }
            }

    }
}
