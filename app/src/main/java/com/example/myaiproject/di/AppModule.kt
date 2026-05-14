package com.example.myaiproject.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.myaiproject.data.local.db.AppDatabase
import com.example.myaiproject.data.remote.api.AiApi
import com.example.myaiproject.data.remote.api.AuthApi
import com.example.myaiproject.data.remote.api.TransactionApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences>
        by preferencesDataStore(name = "settings")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8000/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    fun provideAuthApi(retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    @Provides
    fun provideTransactionApi(retrofit: Retrofit): TransactionApi =
        retrofit.create(TransactionApi::class.java)

    @Provides
    fun provideAiApi(retrofit: Retrofit): AiApi =
        retrofit.create(AiApi::class.java)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "finance_db"
        ).build()

    @Provides
    fun provideTransactionDao(db: AppDatabase) = db.transactionDao()

    @Provides
    fun provideGoalDao(db: AppDatabase) = db.goalDao()

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.dataStore
}