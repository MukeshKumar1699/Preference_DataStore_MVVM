package com.example.preferencedatastoremvvm

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.elvishew.xlog.flattener.ClassicFlattener
import com.elvishew.xlog.printer.AndroidPrinter
import com.elvishew.xlog.printer.file.FilePrinter
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator
import com.example.preferencedatastoremvvm.repo.OperationRepository
import com.example.preferencedatastoremvvm.repo.DataStoreRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val PREFERENCES_NAME = "my_preferences"

    @Provides
    @Singleton
    @Named("pref_name")// name in case of conflict
    fun providePreferenceName(): String = PREFERENCES_NAME

    @Provides
    fun provideContext(
        @ApplicationContext context: Context,
    ): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
         PreferenceDataStoreFactory.create(
            produceFile = {
                context.preferencesDataStoreFile("test_preference")
            }
        )

    @Provides
    fun provideDataStoreRepository(dataStore: DataStore<Preferences>): OperationRepository = DataStoreRepositoryImpl(dataStore)

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences = context.getSharedPreferences(
        PREFERENCES_NAME, Context.MODE_PRIVATE)


/*    @Provides
    @Singleton
    fun provideXLog() {
        val androidPrinter = AndroidPrinter() // Print logs to Logcat.
        val filePrinter = FilePrinter.Builder("/sdcard/xlog/") // Save logs to file.
            .fileNameGenerator(DateFileNameGenerator())       // Generate file names by date.
            .flattener(ClassicFlattener())                    // Flatten logs in classic format.
            .build()

        // Initialize XLog
        return XLog.init(
            LogLevel.ALL,    // Log level
            androidPrinter,  // Print logs to logcat
            filePrinter      // Print logs to a file
        )

    }*/

}
