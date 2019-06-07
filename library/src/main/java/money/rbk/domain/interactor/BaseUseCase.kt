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

package money.rbk.domain.interactor

import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import money.rbk.presentation.model.BaseIUModel

internal typealias BgExecutor = ((Throwable) -> Unit, () -> Unit) -> Unit
internal typealias UiExecutor = (() -> Unit) -> Unit

abstract class BaseUseCase<T : BaseIUModel> {

    private val asyncExecutor = AsyncTask.THREAD_POOL_EXECUTOR

    private val handler = Handler(Looper.getMainLooper())

    protected val bgExecutor: BgExecutor = { onErrorCallback, executeBlock ->
        asyncExecutor.execute {
            try {
                executeBlock()
            } catch (error: Throwable) {
                uiExecutor {
                    onErrorCallback(error)
                }
            }
        }

    }

    protected val uiExecutor: UiExecutor = { handler.post(it) }

    abstract operator fun invoke(onResultCallback: (T) -> Unit,
        onErrorCallback: (Throwable) -> Unit)

}
