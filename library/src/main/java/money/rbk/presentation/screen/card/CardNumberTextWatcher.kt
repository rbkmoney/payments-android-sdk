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

import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import money.rbk.data.CreditCardType
import money.rbk.presentation.utils.extensions.clearLength
import money.rbk.presentation.utils.extensions.removeSpaces
import money.rbk.presentation.utils.extensions.toMask
import ru.tinkoff.decoro.watchers.MaskFormatWatcher

class CardNumberTextWatcher(private val fomatter: MaskFormatWatcher) : TextWatcher {

    private var ignore = false
    private val handler = Handler(Looper.getMainLooper())
    private var isDetected = false
    private var currentMaskMap: Map<Int, String>? = null
    private var currentMask: String? = null
    private var changedString: String? = null

    override fun afterTextChanged(s: Editable?) {

        if (ignore) {
            return
        }
        val currentNumber = s.toString()

        val predictedCardTypes = CreditCardType.predetect(currentNumber.removeSpaces())
        if ((predictedCardTypes.size == 1)) {
            Log.i("AAAAAA", "DETECTED!")
            isDetected = true
            currentMaskMap = predictedCardTypes
                .first()
                .formatMasks
                .associateBy({ it.clearLength() }, { it })
                .toSortedMap()
            setNextMask()
        } else {
            Log.i("AAAAAA", "UNDETECTED!")
            isDetected = false
        }

        if (currentMask != null && currentNumber.clearLength() > currentMask!!.clearLength()) {
            setNextMask()
        }
        ignore = false
    }

    private fun setNextMask() {
        if (currentMaskMap != null) {
            val keySet = currentMaskMap!!.keys
            val currentKey: Int?
            currentKey = if (currentMask != null) {
                keySet.find { it > currentMask!!.clearLength() }
            } else {
                keySet.first()
            }
            currentMask = currentMaskMap!![currentKey]

            Log.i("AAAAA", "NEW MASK = $currentMask , WITH VALUE = $changedString")

            fomatter.setMask(currentMask!!.toMask(keySet.last() == currentKey).apply {
                ignore = true
                this.insertFront(changedString)
            })
        }
    }


    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        changedString = s.toString()
    }
}