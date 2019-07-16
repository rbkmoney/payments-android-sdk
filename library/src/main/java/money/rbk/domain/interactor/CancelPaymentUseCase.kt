package money.rbk.domain.interactor

import money.rbk.di.Injector
import money.rbk.domain.interactor.base.UseCase
import money.rbk.domain.interactor.input.EmptyInputModel
import money.rbk.domain.repository.CheckoutRepository
import money.rbk.presentation.model.EmptyIUModel

internal class CancelPaymentUseCase(
    private val checkoutRepository: CheckoutRepository = Injector.checkoutRepository
) : UseCase<EmptyInputModel, EmptyIUModel>() {

    override fun invoke(inputModel: EmptyInputModel, onResultCallback: (EmptyIUModel) -> Unit,
        onErrorCallback: (Throwable) -> Unit) {
        try {
            checkoutRepository.paymentId = null
            checkoutRepository.paymentTool = null
            checkoutRepository.contactInfo = null
        } catch (t: Throwable) {
            onErrorCallback(t)
        }
        onResultCallback(EmptyIUModel)
    }

}
