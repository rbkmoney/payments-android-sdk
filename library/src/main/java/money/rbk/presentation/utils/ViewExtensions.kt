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

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText
import androidx.annotation.DrawableRes
import money.rbk.R

private fun EditText.setErrorState() {
    setBackgroundResource(R.drawable.background_edit_text_error)
    setRightDrawable(R.drawable.ic_cross)
}


private fun EditText.setOkayState(@DrawableRes onValidDrawable: Int?) {
    setBackgroundResource(R.drawable.background_edit_text)
    setRightDrawable(onValidDrawable)
}

fun EditText.clearState() {
    setBackgroundResource(R.drawable.background_edit_text)
    removeRightDrawable()
}

fun EditText.setValid(
    isValid: Boolean,
    @DrawableRes onValidDrawable: Int? = R.drawable.ic_check
) =
    if (isValid) {
        this.setOkayState(onValidDrawable)
    } else {
        this.setErrorState()
    }

fun EditText.setRightDrawable(@DrawableRes drawableId: Int?) {
    if (drawableId == null) {
        removeRightDrawable()
    } else {
        val drawables = this.compoundDrawables
        val currentDrawable = context.getDrawable(drawableId!!)
        this.setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], currentDrawable, drawables[3])
    }
}

fun EditText.removeRightDrawable() {
    val drawables = this.compoundDrawables
    this.setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], null, drawables[3])
}

fun View.makeGone(){
    this.visibility = GONE
}


fun View.makeVisible(){
    this.visibility = VISIBLE
}