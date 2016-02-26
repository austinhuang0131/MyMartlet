/*
 * Copyright 2014-2016 Appvelopers
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

package ca.appvelopers.mcgillmobile.util.storage;

import android.content.SharedPreferences;

import com.guerinet.utils.prefs.BooleanPreference;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.Place;
import ca.appvelopers.mcgillmobile.model.PlaceType;
import ca.appvelopers.mcgillmobile.model.Statement;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.model.prefs.PasswordPreference;
import ca.appvelopers.mcgillmobile.model.prefs.PrefsModule;
import ca.appvelopers.mcgillmobile.model.prefs.UsernamePreference;
import ca.appvelopers.mcgillmobile.util.manager.HomepageManager;
import ca.appvelopers.mcgillmobile.util.manager.ScheduleManager;
import ca.appvelopers.mcgillmobile.util.manager.TranscriptManager;

/**
 * Clears objects from internal storage or {@link SharedPreferences}
 * @author Julien Guerinet
 * @since 1.0.0
 */
@Singleton
public class ClearManager {
    /**
     * {@link UsernamePreference} instance
     */
    private final UsernamePreference usernamePref;
    /**
     * {@link PasswordPreference} instance
     */
    private final PasswordPreference passwordPref;
    /**
     * Remember Username {@link BooleanPreference}
     */
    private final BooleanPreference rememberUsernamePref;
    /**
     * {@link HomepageManager}
     */
    private final HomepageManager homepageManager;
    /**
     * {@link TranscriptManager} instance
     */
    private final TranscriptManager transcriptManager;
    /**
     * {@link ScheduleManager} instance
     */
    private final ScheduleManager scheduleManager;

    /**
     * Default Injectable Constructor
     *
     * @param usernamePref         {@link UsernamePreference} instance
     * @param passwordPref         {@link PasswordPreference} instance
     * @param rememberUsernamePref Remember Username {@link BooleanPreference}
     * @param homepageManager      {@link HomepageManager} instance
     * @param transcriptManager    {@link TranscriptManager} instance
     * @param scheduleManager      {@link ScheduleManager} instance
     */
    @Inject
    protected ClearManager(UsernamePreference usernamePref, PasswordPreference passwordPref,
            @Named(PrefsModule.REMEMBER_USERNAME) BooleanPreference rememberUsernamePref,
            HomepageManager homepageManager, TranscriptManager transcriptManager,
            ScheduleManager scheduleManager) {
        this.rememberUsernamePref = rememberUsernamePref;
        this.usernamePref = usernamePref;
        this.passwordPref = passwordPref;
        this.homepageManager = homepageManager;
        this.transcriptManager = transcriptManager;
        this.scheduleManager = scheduleManager;
    }

    /**
     * Clears all of the user's info
     */
    public void all() {
        //If the user had not chosen to remember their username, clear it
        if (!rememberUsernamePref.get()) {
            usernamePref.clear();
        }

        //Password
        passwordPref.clear();

        //Schedule
        scheduleManager.clear();

        //Transcript
        transcriptManager.clear();

        //Ebill
        App.setEbill(new ArrayList<Statement>());

        //HomepageManager
        homepageManager.clear();

        //Default Term
        App.setDefaultTerm(null);

        //Wishlist
        App.setWishlist(new ArrayList<Course>());

        //Favorite places
        App.setFavoritePlaces(new ArrayList<Place>());

        //TODO Clear internal storage
    }

    /**
     * Clears all of the config info
     */
    public void config() {
        App.setPlaces(new ArrayList<Place>());
        App.setPlaceTypes(new ArrayList<PlaceType>());
        App.setRegisterTerms(new ArrayList<Term>());
    }
}
