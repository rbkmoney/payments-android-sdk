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

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings.Secure
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import money.rbk.BuildConfig
import money.rbk.presentation.utils.isTablet
import java.security.MessageDigest

internal class ClientInfoUtils(private val application: Application) {

    companion object {
        private const val ERROR_NO_MANAGER = "no_manager"
        private const val ERROR_NO_PERMISSION = "no_permission"
        private const val ERROR_UNEXPECTED_EXCEPTION = "unexpected_exception"
        private const val ERROR_NO_GP_SERVICES = "no_gp_services"
    }

    val userAgent: String by lazy {
        val versionName = BuildConfig.VERSION_NAME
        val debugSuffix = if (BuildConfig.DEBUG) "-debug" else ""
        val version = versionName + debugSuffix
        val osVersion = Build.VERSION.RELEASE

        "RBK.Money.SDK.Android/$version Android/$osVersion ${getDeviceType(application.isTablet)}"
    }

    val fingerprint: String by lazy {
        (getIdFromTelephonyManager(application) + getSecureAndroidId(application) + getWiFiMacAddress(
            application) + getPseudoId() + getAdvertisingId(application)).sha1()
    }

    private fun getPseudoId(): String {
        return Build.BOARD + Build.BRAND +
            Build.DEVICE + Build.DISPLAY + Build.HOST +
            Build.ID + Build.MANUFACTURER +
            Build.MODEL + Build.PRODUCT +
            Build.TAGS + Build.TYPE +
            Build.USER

    }

    @SuppressLint("HardwareIds")
    private fun getAdvertisingId(context: Context): String {
        return try {
            if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS) {
                return AdvertisingIdClient.getAdvertisingIdInfo(context.applicationContext)
                    .id
            } else {
                ERROR_NO_GP_SERVICES
            }
        } catch (error: Throwable) {
            ERROR_UNEXPECTED_EXCEPTION + error.message
        }
    }

    @SuppressLint("HardwareIds")
    private fun getWiFiMacAddress(context: Context): String {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
                ?: return ERROR_NO_MANAGER

        return try {
            wifiManager.connectionInfo.macAddress
        } catch (error: Throwable) {
            ERROR_UNEXPECTED_EXCEPTION + error.message
        }
    }

    @SuppressLint("HardwareIds")
    private fun getSecureAndroidId(context: Context): String {
        return try {
            Secure.getString(context.contentResolver, Secure.ANDROID_ID)
        } catch (error: Throwable) {
            ERROR_UNEXPECTED_EXCEPTION + error.message
        }
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    private fun getIdFromTelephonyManager(context: Context): String {

        val telephonyManager =
            (context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager)
                ?: return ERROR_NO_MANAGER

        return try {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED)
                "${telephonyManager.deviceId}_${telephonyManager.subscriberId}"
            else ERROR_NO_PERMISSION
        } catch (error: Throwable) {
            ERROR_UNEXPECTED_EXCEPTION + error.message
        }
    }

    private fun getDeviceType(isTablet: Boolean) = if (isTablet) {
        "tablet"
    } else {
        "smartphone"
    }

    private fun convertToHex(data: ByteArray): String =
        data.joinToString(separator = "") {
            String.format("%02X", it)
        }

    private fun String.sha1(): String {
        return try {
            val md = MessageDigest.getInstance("SHA-1")
            val textBytes = toByteArray()
            md.update(textBytes, 0, textBytes.size)
            val sha1hash = md.digest()
            convertToHex(sha1hash)
        } catch (error: Throwable) {
            ERROR_UNEXPECTED_EXCEPTION + error.message
        }
    }
}
