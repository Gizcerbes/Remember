package com.uogames.clientApi.version3.network

import android.content.Context
import com.squareup.picasso.Cache
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import com.uogames.Singleton
import com.uogames.clientApi.version3.network.provider.*
import com.uogames.clientApi.version3.network.service.*
import com.uogames.network.BuildConfig

class NetworkProvider private constructor(private val c: Client) {

    private val picasso = Singleton<Picasso>()

    val card = CardProvider(CardService(c.client))
    val image = ImageProvider(ImageService(c.client))
    val moduleCard = ModuleCardProvider(ModuleCardService(c.client))
    val module = ModuleProvider(ModuleService(c.client))
    val phrase = PhraseProvider(PhraseService(c.client))
    val pronounce = PronunciationProvider(PronunciationService(c.client))
    val report = ReportProvider(ReportService(c.client))
    val user = UserProvider(UserService(c.client))


    companion object {

        private val instance = Singleton<NetworkProvider>()

        fun getInstance(
            context: Context,
            secret: () -> String,
            data: (() -> Map<String, String>)?
        ): NetworkProvider {
            return instance.get {
                val keystore = context.assets.open("keys/keystore.bks").readBytes()
                val client = Client(
                    secret = secret,
                    data = data,
                    defaultUrl = BuildConfig.SERVER_IP,
                    keystoreInput = keystore,
                    keystorePassword = BuildConfig.NETWORK_PASSWORD.toCharArray()
                )
                return@get NetworkProvider(client)
            }
        }


    }

    fun getPicasso(context: Context): Picasso {
        return picasso.get {
            Picasso
                .Builder(context)
                .downloader(OkHttp3Downloader(c.okHttpClient))
                .build()
        }
    }

}