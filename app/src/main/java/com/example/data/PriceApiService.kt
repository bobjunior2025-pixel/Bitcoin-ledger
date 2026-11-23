package com.example.data

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class PriceResponse(
  val bitcoin: CurrencyPrice?
)

data class CurrencyPrice(
  val usd: Double?,
  val brl: Double?
)

interface CoinGeckoApi {
  @GET("api/v3/simple/price")
  suspend fun getBitcoinPrice(
    @Query("ids") ids: String = "bitcoin",
    @Query("vs_currencies") vsCurrencies: String = "usd,brl"
  ): PriceResponse

  companion object {
    private const val BASE_URL = "https://api.coingecko.com/"

    val instance: CoinGeckoApi by lazy {
      Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(CoinGeckoApi::class.java)
    }
  }
}
