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

package money.rbk.data.utils

import android.content.Context
import android.os.Build
import money.rbk.BuildConfig
import money.rbk.presentation.utils.isTablet
import java.util.Locale
import java.util.UUID

internal object ClientInfoUtils {

    lateinit var userAgent: String

    lateinit var fingerprint: String

    fun initialize(context: Context) {
        generateUserAgent(context)
        generateFingerprint(context)
    }

    private fun getUniquePsuedoID(context: Context): String {

        val devIDShort =
            "35" + Build.BOARD.length % 10 + Build.BRAND.length % 10 + Build.CPU_ABI.length % 10 + Build.DEVICE.length % 10 + Build.MANUFACTURER.length % 10 + Build.MODEL.length % 10 + Build.PRODUCT.length % 10 + Locale.getDefault().hashCode() % 10

        var serial = "serial"
        try {
            serial = android.os.Build::class.java.getField("SERIAL")
                .get(null)
                .toString()
            return UUID(devIDShort.hashCode().toLong(), serial.hashCode().toLong()).toString()
        } catch (exception: Exception) { /* silent */
        }

        return UUID(devIDShort.hashCode().toLong(), serial.hashCode().toLong()).toString()
    }

    private fun generateFingerprint(context: Context) {

        fingerprint = getUniquePsuedoID(context)
    }

    private fun generateUserAgent(context: Context) {
        val versionName = BuildConfig.VERSION_NAME
        val debugSuffix = if (BuildConfig.DEBUG) "-debug" else ""
        val version = versionName + debugSuffix
        val osVersion = Build.VERSION.RELEASE
        userAgent =
            "RBK.Money.SDK.Android/$version Android/$osVersion ${getDeviceType(context.isTablet)}"
    }

    private fun getDeviceType(isTablet: Boolean) = if (isTablet) {
        "tablet"
    } else {
        "smartphone"
    }

}
