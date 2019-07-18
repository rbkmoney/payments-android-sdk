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

import android.app.Activity
import android.graphics.Point
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentActivity
import money.rbk.R
import kotlin.math.min

internal fun FragmentActivity.hideKeyboard() {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    val view = currentFocus ?: View(this)
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

internal fun Activity.adjustSize() {
    if (isTablet) {
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(window.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = min(screenHeight, resources.getDimensionPixelOffset(R.dimen.height_screen))
        window.attributes = lp
    }
}

internal val Activity.screenHeight: Int
    get() = windowManager.defaultDisplay
        .run {
            Point().also { getSize(it) }
                .y
        }
