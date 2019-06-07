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

import android.os.Parcelable
import androidx.fragment.app.Fragment
import money.rbk.presentation.exception.FragmentArgumentNonnullException
import money.rbk.presentation.exception.WrongFragmentArgumentException
import kotlin.reflect.KProperty

inline fun <reified T> Fragment.argNullable(key: String): FragmentArgsNullableDelegate<T> =

    when (T::class) {
        Int::class -> FragmentArgsNullableDelegate { arguments?.getInt(key) as? T }
        String::class -> FragmentArgsNullableDelegate { arguments?.getString(key) as? T }
        Boolean::class -> FragmentArgsNullableDelegate { arguments?.getBoolean(key) as? T }
        Float::class -> FragmentArgsNullableDelegate { arguments?.getFloat(key) as? T }
        Long::class -> FragmentArgsNullableDelegate { arguments?.getLong(key) as? T }
        CharSequence::class -> FragmentArgsNullableDelegate { arguments?.getCharSequence(key) as? T }
        else -> {
            if (Parcelable::class.java.isAssignableFrom(T::class.java)) {
                FragmentArgsNullableDelegate { arguments?.getParcelable<Parcelable>(key) as? T }
            } else {
                throw WrongFragmentArgumentException(T::class)
            }
        }
    }

class FragmentArgsNullableDelegate<T>(private val getter: () -> T?) {
    operator fun getValue(thisRef: Fragment, property: KProperty<*>) = getter()
}

inline fun <reified T> Fragment.arg(key: String): FragmentArgsDelegate<T> =
    FragmentArgsDelegate(argNullable(key))

class FragmentArgsDelegate<T>(private val nullableDelegate: FragmentArgsNullableDelegate<T>) {
    operator fun getValue(thisRef: Fragment, property: KProperty<*>) =
        nullableDelegate.getValue(thisRef, property)
            ?: throw FragmentArgumentNonnullException(thisRef.javaClass, property.name)
}