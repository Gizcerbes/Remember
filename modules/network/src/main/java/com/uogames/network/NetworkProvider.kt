package com.uogames.network

import android.content.Context
import com.uogames.network.provider.*
import com.uogames.network.service.*

class NetworkProvider private constructor(private val client: HttpClient) {

	companion object {

		private var INSTANCE: NetworkProvider? = null

		fun getInstance(context: Context,  secret: () -> String, data: () -> Map<String, String>):NetworkProvider {
			if (INSTANCE == null) synchronized(this) {
				if (INSTANCE == null) {
					val keystore = context.assets.open("keys/keystore.bks").readBytes()
					val client = HttpClient(
						secret = secret,
						data = data,
						defaultUrl = {
							if (BuildConfig.DEBUG){
								"https://93.125.42.151:8081/remember-card"
							} else{
								"https://93.125.42.151:8080/remember-card"
							}
						},
						keystoreInput = keystore,
						keystorePassword = "itismyfirstseriousapp".toCharArray()
					)
					INSTANCE = NetworkProvider(client)
				}
			}
			return INSTANCE as NetworkProvider
		}

	}

	val card = CardProvider(CardService(client))
	val image = ImageProvider(ImageService(client))
	val moduleCard = ModuleCardProvider(ModuleCardService(client))
	val module = ModuleProvider(ModuleService(client))
	val phrase = PhraseProvider(PhraseService(client))
	val pronounce = PronunciationProvider(PronunciationService(client))
	val user = UserProvider(UserService(client))

	fun getPicasso(context: Context) = client.getPicasso(context)

}