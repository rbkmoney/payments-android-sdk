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

import money.rbk.data.extension.findEnumOrNull
import money.rbk.data.extension.getNullable
import money.rbk.data.serialization.Deserializer
import org.json.JSONObject
//{
//    "description":"No match to pattern. Path to item: paymentTool.cardNumber",
//    "errorType":"schema_violated",
//    "name":"PaymentResourceParams"
//}

data class ApiError(
    val code: Code?,
    val message: String?,
    val subError: ApiError?
) {
    companion object : Deserializer<JSONObject, ApiError> {
        override fun fromJson(json: JSONObject): ApiError = ApiError(
            code = json.getNullable<String>("code")?.let { findEnumOrNull<Code>(it) },
            message = json.getNullable("message"),
            subError = json.getNullable("subError")
        )
    }

    enum class Code {

        /**	Недоступная в рамках действующего договора операция. */
        operationNotPermitted,

        /**	Ваш участник заблокирован или его операции приостановлены. В последнем случае вы можете их возобновить. */
        invalidPartyStatus,

        /**	Ваш магазин заблокирован или его операции приостановлены. В последнем случае вы можете их возобновить. */
        invalidShopStatus,

        /**	Ваш договор более не имеет силы, по причине истечения срока действия или расторжения. */
        invalidContractStatus,

        /**	Указан идентификатор несуществующего магазина. */
        invalidShopID,

        /**	Стоимость инвойса не указана или неверна, в частности, не равна стоимости позиций в корзине. */
        invalidInvoiceCost,

        /**	Некорректная корзина в инвойсе, Например, пустая. */
        invalidInvoiceCart,

        /**	Неверный статус инвойса. Например, при попытке оплатить отменённый инвойс. */
        invalidInvoiceStatus,

        /**	Последний запущенный платёж по указанному инвойсу ещё не достиг финального статуса. */
        invoicePaymentPending,

        /**	Неверный статус платежа. Например, при попытке подтвердить неуспешный платёж. */
        invalidPaymentStatus,

        /**	Не поддерживаемый системой или не подключенный в рамках действующего договора платежный инструмент. */
        invalidPaymentResource,

        /**	Неверное содержимое токена платёжного инструмента. */
        invalidPaymentToolToken,

        /**	Невернoе содержимое платёжной сессии. */
        invalidPaymentSession,

        /**	Невернo указан родительский рекуррентный платеж. */
        invalidRecurrentParent,

        /**	Недостаточный объём денежных средств на счёте магазина, например, для проведения возврата. */
        insufficentAccountBalance,

        /**	Попытка возврата сверх суммы платежа. */
        invoicePaymentAmountExceeded,

        /**	Попытка возврата средств в валюте, отличной от валюты платежа. */
        inconsistentRefundCurrency,

        /**	Попытка внести изменения участника, конфликтующие с изменениями в других заявках, ожидающих рассмотрения. */
        changesetConflict,

        /**	Неверные изменения участника, например, попытка создать магазин в валюте, недоступной в рамках договора. */
        invalidChangeset,

        /**	Неверный статус заявки. Например, при попытке отзыва уже принятой заявки. */
        invalidClaimStatus,

        /**	Неверная ревизия заявки. Например, в случае если заявку одновременно с вами кто-то уже принял или отклонил. */
        invalidClaimRevision,

        /**	Превышен разумный лимит выборки. В этом случае лучше запросить менее объёмный набор данных. */
        limitExceeded,

        /**	Неверный формат времени. */
        invalidDeadline,

        /**	Прочие неверные данные запроса. */
        invalidRequest,
    }

}