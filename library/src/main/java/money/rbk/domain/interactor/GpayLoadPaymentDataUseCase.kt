package money.rbk.domain.interactor

import money.rbk.di.Injector
import money.rbk.domain.interactor.base.UseCase
import money.rbk.domain.interactor.input.GpayLoadPaymentDataInputModel
import money.rbk.domain.repository.GpayRepository
import money.rbk.presentation.model.PaymentDataTaskModel

internal class GpayLoadPaymentDataUseCase(
    private val gpayRepository: GpayRepository = Injector.gpayRepository
) : UseCase<GpayLoadPaymentDataInputModel, PaymentDataTaskModel>() {

    override fun invoke(inputModel: GpayLoadPaymentDataInputModel,
        onResultCallback: (PaymentDataTaskModel) -> Unit,
        onErrorCallback: (Throwable) -> Unit) {

        bgExecutor(onErrorCallback) {

            val task = gpayRepository.loadPaymentData(inputModel.price, inputModel.currency)

            uiExecutor {
                onResultCallback(PaymentDataTaskModel(task))
            }
        }

    }
}
