/*
 * Copyright 2014-2018 Julien Guerinet
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
 */

package com.guerinet.mymartlet.util.service

import android.content.Intent
import androidx.core.app.JobIntentService
import com.guerinet.mymartlet.util.Prefs
import com.guerinet.mymartlet.util.prefs.RegisterTermsPref
import com.guerinet.mymartlet.util.retrofit.ConfigService
import com.guerinet.mymartlet.util.room.daos.CategoryDao
import com.guerinet.mymartlet.util.room.daos.MapDao
import com.guerinet.suitcase.date.NullDatePref
import com.guerinet.suitcase.date.extensions.rfc1123String
import com.guerinet.suitcase.prefs.IntPref
import com.guerinet.suitcase.util.extensions.isConnected
import org.koin.android.ext.android.inject
import org.threeten.bp.ZonedDateTime
import retrofit2.Call
import timber.log.Timber

/**
 * Downloads the latest version of the config info from the server
 * @author Julien Guerinet
 * @since 2.4.0
 */
class ConfigDownloadService : JobIntentService() {

    private val configService by inject<ConfigService>()

    private val imsConfigPref by inject<NullDatePref>(Prefs.IMS_CONFIG)

    private val imsPlacesPref by inject<NullDatePref>(Prefs.IMS_PLACES)

    private val imsCategoriesPref by inject<NullDatePref>(Prefs.IMS_CATEGORIES)

    private val imsRegistrationPref by inject<NullDatePref>(Prefs.IMS_REGISTRATION)

    private val minVersionPref by inject<IntPref>(Prefs.MIN_VERSION)

    private val registerTermsPref by inject<RegisterTermsPref>()

    private val categoryDao by inject<CategoryDao>()

    private val mapDao by inject<MapDao>()

    override fun onHandleWork(intent: Intent) {
        if (!isConnected) {
            // If we're not connected to the internet, don't continue
            return
        }

        // Config
        val config = executeRequest(configService.config(getIMS(imsConfigPref)), imsConfigPref)
        config?.apply { minVersionPref.value = androidMinVersion }

        // Places
        val places = executeRequest(configService.places(getIMS(imsPlacesPref)), imsPlacesPref)
        if (places != null) {
            mapDao.updatePlaces(places)
        }

        // Categories
        val categories = executeRequest(configService.categories(getIMS(imsCategoriesPref)),
                imsCategoriesPref)
        if (categories != null) {
            categoryDao.update(categories)
        }

        // Registration Terms
        val registerTerms = executeRequest(configService.registrationTerms(
                getIMS(imsRegistrationPref)), imsRegistrationPref)
        registerTerms?.apply { registerTermsPref.terms = toMutableList() }
    }

    /**
     * Returns the ims String based on the [pref] to use for the call
     */
    private fun getIMS(pref: NullDatePref): String? = pref.date.rfc1123String

    /**
     * Executes the [call] and updates the [imsPref] if successful. Returns the response object,
     * null if there was an error
     */
    private fun <T> executeRequest(call: Call<T>, imsPref: NullDatePref): T? {
        return try {
            val response = call.execute()
            if (response.isSuccessful) {
                imsPref.date = ZonedDateTime.now()
            }
            response.body()
        } catch (e: Exception) {
            Timber.e(e, "Error downloading config section")
            null
        }
    }

    /**
     * Config skeleton class
     */
    class Config {

        /**
         * Minimum version of the app that the user needs
         */
        var androidMinVersion = -1
    }
}
