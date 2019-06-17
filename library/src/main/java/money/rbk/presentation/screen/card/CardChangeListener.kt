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

import android.util.Log
import money.rbk.data.CreditCardType
import money.rbk.presentation.utils.extensions.clearLength
import money.rbk.presentation.utils.extensions.removeSpaces
import money.rbk.presentation.utils.extensions.toMask
import ru.tinkoff.decoro.FormattedTextChangeListener
import ru.tinkoff.decoro.watchers.FormatWatcher
import ru.tinkoff.decoro.watchers.MaskFormatWatcher

class CardChangeListener : FormattedTextChangeListener {

    private var isDetected = false
    private var currentMaskList: List<String>? = null
    private var currentMask: String? = null

    override fun onTextFormatted(formatter: FormatWatcher?, newFormattedText: String) {
        Log.i("AAAAAA", "NEW FORMATTED TEXT = $newFormattedText")

        val predictedCardTypes = CreditCardType.predetect(newFormattedText.removeSpaces())
        if ((predictedCardTypes.size == 1) and isDetected.not()) {
            Log.i("AAAAAA", "DETECTED! CARD = ${predictedCardTypes.first().name}")
            currentMaskList = predictedCardTypes
                .first()
                .formatMasks
                .toList()
                .sortedBy { it.clearLength() }
            isDetected = true
        }
        if (newFormattedText.isEmpty() or newFormattedText.isBlank()) {
            isDetected = false
        }
        setNextMask(formatter, newFormattedText)
    }

    private fun setNextMask(formatter: FormatWatcher?, newFormattedText: String) {
        if (currentMaskList != null) {
            val newMask = currentMaskList!!.find { it.clearLength() >= newFormattedText.clearLength() }
            if (currentMask == newMask) {
                return
            }
            val isTerminated = currentMaskList!!.last() == newMask
            val mask = newMask?.toMask(isTerminated)

            Log.i("AAAAAA", "CURRENT MASK = $newMask,  IS TERMINATED = $isTerminated")
            mask?.insertFront(newFormattedText)
            (formatter as MaskFormatWatcher).swapMask(mask)
            currentMask = newMask
        }
    }

    override fun beforeFormatting(oldValue: String?, newValue: String?): Boolean {
        return false
    }
}