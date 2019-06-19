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

package money.rbk.domain.entity

import money.rbk.data.extension.findEnum
import money.rbk.data.extension.parseNullable
import money.rbk.data.serialization.Deserializer
import org.json.JSONObject

data class PaymentError(
    val code: Code,
    val subError: PaymentError?
) {
    companion object : Deserializer<JSONObject, PaymentError> {
        override fun fromJson(json: JSONObject): PaymentError =
            PaymentError(
                code = findEnum(json.getString("code"), Code.Unknown),
                subError = json.parseNullable("subError", this)
            )
    }

    enum class Code {
        InvalidPaymentTool,     //	Неверный платежный инструмент (введен номер несуществующей карты, отсутствующего аккаунта и т.п.)
        AccountLimitsExceeded,  //	Превышены лимиты (например, в личном кабинете плательщика установлено ограничение по сумме платежа, стране списания)
        InsufficientFunds,      //  Недостаточно средств на счете
        PreauthorizationFailed, //  Предварительная авторизация отклонена (введен неверный код 3D-Secure, на форме 3D-Secure нажата ссылка отмены)
        RejectedByIssuer,       //  Платёж отклонён эмитентом (установлены запреты по стране списания, запрет на покупки в интернете, платеж отклонен антифродом эмитента и т.п.)
        PaymentRejected,        //  Платёж отклонён по иным причинам
        Unknown                 //  Неизвестная ошибка
    }

}