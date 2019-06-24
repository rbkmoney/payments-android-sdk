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
import android.os.Parcelable
import money.rbk.presentation.exception.ActivityExtraNonnullException
import money.rbk.presentation.exception.WrongActivityArgumentException
import kotlin.reflect.KProperty

inline fun <reified T> Activity.extraNullable(key: String): ActivityArgsNullableDelegate<T> =

    when (T::class) {
        Int::class -> ActivityArgsNullableDelegate { intent?.getIntExtra(key, 0) as? T }
        String::class -> ActivityArgsNullableDelegate { intent?.getStringExtra(key) as? T }
        Boolean::class -> ActivityArgsNullableDelegate { intent?.getBooleanExtra(key, false) as? T }
        Float::class -> ActivityArgsNullableDelegate { intent?.getFloatExtra(key, 0f) as? T }
        Long::class -> ActivityArgsNullableDelegate { intent?.getLongExtra(key, 0) as? T }
        CharSequence::class -> ActivityArgsNullableDelegate { intent?.getCharSequenceExtra(key) as? T }
        ByteArray::class -> ActivityArgsNullableDelegate { intent?.getByteArrayExtra(key) as? T }
        else -> {
            if (Parcelable::class.java.isAssignableFrom(T::class.java)) {
                ActivityArgsNullableDelegate { intent?.getParcelableExtra<Parcelable>(key) as? T }
            } else {
                throw WrongActivityArgumentException(T::class)
            }
        }
    }

class ActivityArgsNullableDelegate<T>(private val getter: () -> T?) {
    operator fun getValue(thisRef: Activity, property: KProperty<*>) = getter()
}

inline fun <reified T> Activity.extra(key: String): ActivityArgsDelegate<T> =
    ActivityArgsDelegate(extraNullable(key))

class ActivityArgsDelegate<T>(private val nullableDelegate: ActivityArgsNullableDelegate<T>) {
    operator fun getValue(thisRef: Activity, property: KProperty<*>) =
        nullableDelegate.getValue(thisRef, property)
            ?: throw ActivityExtraNonnullException(thisRef.javaClass, property.name)
}
