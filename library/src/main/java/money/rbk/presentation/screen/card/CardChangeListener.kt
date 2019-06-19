/*
 *
 * Copyright 2019 RBKmoney
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package money.rbk.presentation.screen.card

import money.rbk.data.CreditCardType
import money.rbk.presentation.utils.removeSpaces
import ru.tinkoff.decoro.FormattedTextChangeListener
import ru.tinkoff.decoro.watchers.FormatWatcher

class CardChangeListener(private val edCallBack: (cardType: CreditCardType?) -> Unit) :
    FormattedTextChangeListener {

    override fun onTextFormatted(formatter: FormatWatcher?, newFormattedText: String) {
        val predictedCardType = CreditCardType.suggestCardType(newFormattedText.removeSpaces())

        if (predictedCardType != null) {
            edCallBack(predictedCardType)
        }
    }

    override fun beforeFormatting(oldValue: String?, newValue: String?): Boolean {
        return false
    }
}