package money.rbk.sample.app

import android.app.Application
import money.rbk.sample.network.NetworkService

/**
 * @author Arthur Korchagin (artur.korchagin@simbirsoft.com)
 * @since 24.06.19
 */
class RBKSampleApplication : Application() {

    companion object {
        lateinit var networkService: NetworkService
    }

    override fun onCreate() {
        super.onCreate()
        networkService = NetworkService(this)
    }

}
