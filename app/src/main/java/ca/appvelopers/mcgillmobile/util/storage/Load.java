/*
 * Copyright 2014-2015 Appvelopers
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
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.DrawerItem;
import ca.appvelopers.mcgillmobile.model.Language;
import ca.appvelopers.mcgillmobile.model.Place;
import ca.appvelopers.mcgillmobile.model.PlaceType;
import ca.appvelopers.mcgillmobile.model.Statement;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.model.Transcript;
import ca.appvelopers.mcgillmobile.model.User;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.Encryption;

/**
 * Loads objects from internal storage or {@link SharedPreferences}
 * @author Julien Guerinet
 * @version 2.0.0
 * @since 1.0.0
 */
@SuppressWarnings("unchecked")
public class Load {
    private static final String TAG = "Load";

    /* SHARED PREFS */

    /**
     * @return The app version code stored, -1 if none
     */
    public static int versionCode(){
        return Constants.PREFS.getInt(Constants.VERSION, -1);
    }

    /**
     * @return True if the app has been previously opened, false otherwise
     */
    public static boolean firstOpen(){
        return Constants.PREFS.getBoolean(Constants.FIRST_OPEN, true);
    }

    /**
     * @return The user's chosen language, defaults to English
     */
    public static Language language(){
        return Language.values()[(Constants.PREFS.getInt(Constants.LANGUAGE, 0))];
    }

    /**
     * @return The chosen home page, defaults to the schedule
     */
    public static DrawerItem homepage(){
        return DrawerItem.values()[
                Constants.PREFS.getInt(Constants.HOMEPAGE, DrawerItem.SCHEDULE.ordinal())];
    }

    /**
     * @return True if the user has opted out of parser errors, false otherwise
     */
    public static boolean parserErrorDoNotShow(){
        return Constants.PREFS.getBoolean(Constants.PARSER_ERROR_DO_NOT_SHOW, false);
    }

    /**
     * @return True if the user has opted out of the loading screen, false otherwise
     */
    public static boolean loadingDoNotShow(){
        return Constants.PREFS.getBoolean(Constants.LOADING_DO_NOT_SHOW, false);
    }

    /**
     * @return True if the user has opted into anonymous usage statistics, false otherwise
     */
    public static boolean statistics(){
        return Constants.PREFS.getBoolean(Constants.STATISTICS, true);
    }

    /**
     * @return The user's full username
     */
    public static String fullUsername(){
        return username() + App.getContext().getString(R.string.login_email);
    }

    /**
     * @return The user's username (name only, no email suffix)
     */
    public static String username(){
        return Constants.PREFS.getString(Constants.USERNAME, null);
    }

    /**
     * @return The user's password
     */
    public static String password(){
        return Encryption.decode(Constants.PREFS.getString(Constants.PASSWORD, null));
    }

    /**
     * @return True if the username should be remembered when logging out, false otherwise
     */
    public static boolean rememberUsername(){
        return Constants.PREFS.getBoolean(Constants.REMEMBER_USERNAME, true);
    }

    /**
     * @return True if the user has enabled seat checking, false otherwise
     */
    public static boolean seatChecker(){
        return Constants.PREFS.getBoolean(Constants.SEAT_CHECKER, false);
    }

    /**
     * @return True if the user has enabled grade checking, false otherwise
     */
    public static boolean gradeChecker(){
        return Constants.PREFS.getBoolean(Constants.GRADE_CHECKER, false);
    }

    /**
     * @return The last date the WS was queried
     */
    public static String ifModifiedSince(){
        return Constants.PREFS.getString(Constants.IF_MODIFIED_SINCE, null);
    }

    /**
     * @return True if the user has accepted the EULA, false otherwise
     */
    public static boolean eula(){
        return Constants.PREFS.getBoolean(Constants.EULA, false);
    }

    /* INTERNAL STORAGE */

    /**
     * Loads the object at the given file name
     *
     * @param tag      The tag to use in case of failure
     * @param fileName The file name
     * @return The object loaded, null if none
     */
    private static Object loadObject(String tag, String fileName){
        try{
            FileInputStream fis = App.getContext().openFileInput(fileName);
            ObjectInputStream in = new ObjectInputStream(fis);
            return in.readObject();
        } catch(FileNotFoundException e){
            Log.e(TAG, "File not found: " + tag);
        } catch(Exception e){
            Log.e(TAG, "Failure: " + tag, e);
        }

        return null;
    }

    /**
     * @return The list of places, an empty list if none
     */
    public static List<Place> places(){
        List<Place> places = (List<Place>)loadObject("Places", Constants.PLACES_FILE);
        return places == null ? new ArrayList<Place>() : places;
    }

    /**
     * @return The list of place types, an empty list if none
     */
    public static List<PlaceType> placeTypes(){
        List<PlaceType> types = (List<PlaceType>)loadObject("Place Types",
                Constants.PLACE_TYPES_FILE);
        return types == null ? new ArrayList<PlaceType>() : types;
    }

    /**
     * @return The list of terms that the user can currently register in, an empty list if none
     */
    public static List<Term> registerTerms(){
        List<Term> terms = (List<Term>)loadObject("Register Terms", Constants.REGISTER_TERMS_FILE);
        return terms == null ? new ArrayList<Term>() : terms;
    }

    /**
     * @return The user's transcript, null if none
     */
    public static Transcript transcript(){
        return (Transcript)loadObject("Transcript", Constants.TRANSCRIPT_FILE);
    }

    /**
     * @return The user's classes, an empty list if none
     */
    public static List<Course> classes(){
        List<Course> courses = (List<Course>)loadObject("Classes", Constants.COURSES_FILE);
        return courses == null ? new ArrayList<Course>() : courses;
    }

    /**
     * @return The user's Ebill statements, and empty list if none
     */
    public static List<Statement> ebill(){
        List<Statement> statements = (List<Statement>)loadObject("Ebill", Constants.EBILL_FILE);
        return statements == null ? new ArrayList<Statement>() : statements;
    }

    /**
     * @return The user's info, null if none
     */
    public static User user(){
        return (User)loadObject("User", Constants.USER_FILE);
    }

    /**
     * @return The user's chosen default term, null if none
     */
    public static Term defaultTerm(){
        return (Term)loadObject("Default Term", Constants.DEFAULT_TERM_FILE);
    }

    /**
     * @return The user's class wishlist, an empty list if none
     */
    public static List<Course> wishlist(){
        List<Course> wishlist = (List<Course>)loadObject("Wishlist", Constants.WISHLIST_FILE);
        return wishlist == null ? new ArrayList<Course>() : wishlist;
    }

    /**
     * @return The user's favorite places, an empty list if none
     */
    public static List<Place> favoritePlaces(){
        List<Place> places = (List<Place>)loadObject("Favorite Places",
                Constants.FAVORITE_PLACES_FILE);
        return places == null ? new ArrayList<Place>() : places;
    }
}
