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

package money.rbk.presentation.utils

import android.widget.EditText
import money.rbk.R

private fun EditText.setErrorState() {
    setBackgroundResource(R.drawable.background_edit_text_error)
    val drawables = this.compoundDrawables
    val cross = context.getDrawable(R.drawable.ic_cross)
    this.setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], cross, drawables[3])
}


private fun EditText.setOkayState() {
    setBackgroundResource(R.drawable.background_edit_text)
    val drawables = this.compoundDrawables
    val check = context.getDrawable(R.drawable.ic_check)
    this.setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], check, drawables[3])
}


fun EditText.setValid(isValid: Boolean) =
    if (isValid) {
        this.setOkayState()
    } else {
        this.setErrorState()
    }