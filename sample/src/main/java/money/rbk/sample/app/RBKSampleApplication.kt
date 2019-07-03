package money.rbk.sample.app

import money.rbk.sample.network.NetworkService

/**
 * @author Arthur Korchagin (artur.korchagin@simbirsoft.com)
 * @since 24.06.19
 */
class RBKSampleApplication : RBKBaseApplication() {

    companion object {
        lateinit var networkService: NetworkService
    }

    override fun onCreate() {
        super.onCreate()
        networkService = NetworkService(this)
    }

}
