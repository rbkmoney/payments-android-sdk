# RBK.money Android payment mobile SDK

Библиотека позволяет осуществлять платежи через [RBKmoney Payments API](https://developer.rbk.money/api/)

SDK включает в себя все небходимые интерфейсы.

Репозиторий включает в себя:
* [Код библиотеки payments-android-sdk](./library)
* [Код примера интеграции SDK](./sample)

# Подключение библиотеки

## Gradle
```groovy
def libraryVersion = '0.0.24'
dependencies {
    implementation "rbk.money:payments-android-sdk:$libraryVersion"
}
```

## Продажа цифровых товаров
В случае, если в приложении продаются цифровые товары, необхожимо отключить Google Pay.
В AndroidManifest добавить:

```xml
<meta-data
    android:name="com.google.android.gms.wallet.api.enabled"
    tools:node="remove" />
```

# Использование 

1. Вызвать метод `RbkMoney.buildCheckoutIntent` с параметрами:
    * `activity : Activity` - Текущая `Activity`
    * `invoiceId : String` - Идентификатор инвойса (необходимо получить при создании инвойса, не входит в функционал SDK)
    * `invoiceAccessToken : String` - Токен доступа инвойса (необходимо получить при создании инвойса, не входит в функционал SDK)
    * `shopName : String` - Название магазина, будет отображено в заголовке
    * `useTestEnvironment : Boolean` (опциональное) - Использовать тестовое окружение для Google Pay
    * `email : String` (опциональное) - Электронная почта по умолчанию для отправки чеков

2. Запустить `Activity` с помощью полученного интента:
```swift
        startActivityForResult(RbkMoney.buildCheckoutIntent(this,
            invoiceModel.id,
            invoiceModel.invoiceAccessToken,
            invoiceModel.shopName,
            true,
            "test@test.com"
        ),
            CHECKOUT_REQUEST_CODE)

```

3. В методе `onActivityResult` - получить обратный вызов из SDK:
    * Activity.RESULT_OK - платёж прошёл успешно
    * Activity.RESULT_CANCELED - пользователь отменил операцию либо произошла ошибка


```swift
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHECKOUT_REQUEST_CODE) {
            when (resultCode) {
                RESULT_OK -> onPaymentSuccess()
                RESULT_CANCELED -> onPaymentCanceled()
            }
        }
    }

```

# Использование примера

Для запуска [примера](./sample) необходимо:

* Добавить в файл [assets/test_invoices.json](./sample/src/main/assets/test_invoices.json):
    - `id` - Идентификатор инвойса
    - `invoiceAccessToken` - Токен доступа инвойса
    - `shopId` - Идентификатор магазина
    - `shopName` - Название магазина
    - `description` - Описание инвойса
    
* Для создания инвойса по шаблону добавить в файл [assets/test_invoice_templates.json](./sample/src/main/assets/test_invoice_templates.json):
    - `shopName` - Название магазина
    - `invoiceTemplateId` - Идентификатор шаблона инвойса
    - `invoiceTemplateAccessToken` - Токен доступа шаблона инвойса
    

# Ссылки
* [Сайт RBKmoney](https://rbk.money/)
* [Документация API RBKmoney](https://developer.rbk.money/api/)
