package dk.cpe.pokemonapi.di

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dk.cpe.pokemonapi.model.Pokemon
import dk.cpe.pokemonapi.network.PokemonApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient.Builder = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor { message -> Log.i("NetworkModule", message) }.setLevel(HttpLoggingInterceptor.Level.BASIC))

    @Provides
    @Singleton
    fun provideMoshi(): Moshi.Builder {
        return Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
    }

    @Provides
    @Singleton
    fun provideBaseRetrofit(
        okHttpBuilder: OkHttpClient.Builder,
        moshi: Moshi.Builder
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpBuilder.build())
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(MoshiConverterFactory.create(moshi.build()))
            .build()
    }

    @Provides
    @Singleton
    fun providePokemonApi(retrofit: Retrofit): PokemonApi {
        return retrofit.create(PokemonApi::class.java)
    }

}