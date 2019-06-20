package money.rbk.domain.extension

import money.rbk.domain.entity.Invoice
import money.rbk.presentation.utils.formatPrice

/**
 * @author Arthur Korchagin (artur.korchagin@simbirsoft.com)
 * @since 20.06.19
 */

internal val Invoice.cost
    get() = "${amount.formatPrice()} ${currency.symbol}"