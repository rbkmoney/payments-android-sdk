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

package money.rbk.data.network

internal object Constants {
    const val HOST = "api.rbk.money"
    const val BASE_URL = "https://$HOST/v2"
    val CERTS = arrayOf(
        "sha256/dY75Vyo/cYChbq9K4HoinKrrRKBgm/oPWPvwqSJMbpM=",
        "sha256/6YBE8kK4d5J1qu1wEjyoKqzEIvyRY5HyM/NB2wKdcZo=",
        "sha256/ICGRfpgmOUXIWcQ/HXPLQTkFPEFPoDyjvH7ohhQpjzs=")

}
