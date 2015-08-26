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

package ca.appvelopers.mcgillmobile;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

import com.crashlytics.android.Crashlytics;
import com.instabug.library.Instabug;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.util.List;

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
import ca.appvelopers.mcgillmobile.util.Update;
import ca.appvelopers.mcgillmobile.util.background.BootReceiver;
import ca.appvelopers.mcgillmobile.util.storage.Load;
import ca.appvelopers.mcgillmobile.util.storage.Save;
import io.fabric.sdk.android.Fabric;

/**
 * Application implementation
 * @author Julien Guerinet
 * @version 2.0.0
 * @since 1.0.0
 */
public class App extends Application {
    //TODO
    //TODO Change these to Shared Prefs
    public static boolean forceReload = false;
    public static boolean forceUserReload = false;

    private static Context context;

    private static Typeface iconFont;

    private static Language language;
    private static DrawerItem homePage;
    private static Transcript transcript;
    private static List<Course> classes;
    private static Term defaultTerm;
    private static List<Statement> ebill;
    private static User userInfo;
    private static List<Course> wishlist;
    private static List<Place> places;
    private static List<Place> favoritePlaces;
    private static List<PlaceType> placeCategories;
    //List of semesters you can currently register in
    private static List<Term> registerTerms;
    
    @Override
    public void onCreate(){
        super.onCreate();

        //Set the static context
        context = this;
        
        //Run the update code, if any
        Update.update(this);

        //Load the transcript
        transcript = Load.transcript();
        //Load the schedule
        classes = Load.classes();
        //Load the ebill
        ebill = Load.ebill();
        //Load the user info
        userInfo = Load.user();
        //Load the user's chosen language and update the locale
        language = Load.language();
        //Load the user's chosen homepage
        homePage = Load.homepage();
        //Load the default term for the schedule
        defaultTerm = Load.defaultTerm();
        //Load the course wishlist
        wishlist = Load.wishlist();
        //Load the places
        places = Load.places();
        //Load the favorite places
        favoritePlaces = Load.favoritePlaces();
        //Load the place categories
        placeCategories = Load.placeTypes();
        //Load the register terms
        registerTerms = Load.registerTerms();

        //Set up The Fabric stuff: Twitter, Crashlytics
        TwitterAuthConfig authConfig = new TwitterAuthConfig(Constants.TWITTER_KEY,
                Constants.TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig), new TweetComposer(), new Crashlytics());

        //Set up Instabug
        Instabug.initialize(this, Constants.INSTABUG_KEY)
                .enableEmailField(true, false)
                .setCommentPlaceholder(getString(R.string.bug_prompt))
                .setDefaultEmail(Load.fullUsername())
                .setEmailPlaceholder(getString(R.string.bug_email_prompt))
                .setInvalidCommentAlertText(getString(R.string.bug_comment_invalid))
                .setSubmitButtonText(getString(R.string.submit))
                .setCommentIsRequired(true)
                .setDebugEnabled(BuildConfig.DEBUG)
                .setInvocationEvent(Instabug.IBGInvocationEvent.IBGInvocationEventNone)
                .setIsTrackingCrashes(false)
                .setIsTrackingUserSteps(false)
                .setShowIntroDialog(false)
                .setPostFeedbackMessage(getString(R.string.success))
                .setWillShowFeedbackSentAlert(true)
                .setUserData("Email: " + Load.fullUsername() + "\n" +
                        "App Language: " + App.getLanguage().toString());
    }

    /* GETTERS */

    /**
     * @return The app {@link Context}
     */
    public static Context getContext(){
        return context;
    }

    //TODO Phase this out
    public static Typeface getIconFont(){
        if(iconFont == null){
            iconFont = Typeface.createFromAsset(context.getAssets(), "icon-font.ttf");
        }

        return iconFont;
    }

    /**
     * @return The app language
     */
    public static Language getLanguage(){
        return language;
    }

    /**
     * @return The list of {@link Place}s
     */
    public static List<Place> getPlaces(){
        return places;
    }

    /**
     * @return The list of {@link PlaceType}s
     */
    public static List<PlaceType> getPlaceTypes(){
        return placeCategories;
    }

    /**
     * @return The list of {@link Term} the user can currently register in
     */
    public static List<Term> getRegisterTerms(){
        return registerTerms;
    }

    /**
     * @return The user's {@link Transcript}
     */
    public static Transcript getTranscript(){
        synchronized(Constants.TRANSCRIPT_LOCK){
            return transcript;
        }
    }

    /**
     * @return The user's list of {@link Course}s
     */
    public static List<Course> getCourses(){
        return classes;
    }

    /**
     * @return The user's ebill {@link Statement}s
     */
    public static List<Statement> getEbill(){
        return ebill;
    }

    /**
     * @return The {@link User} info
     */
    public static User getUser(){
        return userInfo;
    }

    /**
     * @return The user's chosen homepage
     */
    public static DrawerItem getHomepage(){
        return homePage;
    }

    /**
     * @return The user's chosen default {@link Term}
     */
    public static Term getDefaultTerm(){
        return defaultTerm;
    }

    /**
     * @return The user's wishlist
     */
    public static List<Course> getWishlist() {
        return wishlist;
    }

    /**
     * @return The user's list of favorite {@link Place}s
     */
    public static List<Place> getFavoritePlaces(){
        return favoritePlaces;
    }

    /* SETTERS */

    /**
     * @param language The app language
     */
    public static void setLanguage(Language language){
        App.language = language;

        //Save it to internal storage when this is set
        Save.language();
    }

    /**
     * @param places The list of {@link Place}s
     */
    public static void setPlaces(List<Place> places){
        App.places = places;
        //Save it to internal storage
        Save.places();
    }

    /**
     * @param placeTypes The list of {@link PlaceType}s
     */
    public static void setPlaceTypes(List<PlaceType> placeTypes){
        App.placeCategories = placeTypes;
        //Save it to internal storage
        Save.placeTypes();
    }

    /**
     * @param terms The list of {@link Term}s the user can currently register in
     */
    public static void setRegisterTerms(List<Term> terms){
        App.registerTerms = terms;
        //Save it to internal storage
        Save.registerTerms();
    }

    /**
     * @param transcript The user's {@link Transcript}
     */
    public static void setTranscript(Transcript transcript){
        synchronized (Constants.TRANSCRIPT_LOCK){
            App.transcript = transcript;

            //Save it to internal storage when this is set
            Save.transcript();
        }
    }

    /**
     * @param courses The user's {@link Course}s
     */
    public static void setCourses(List<Course> courses){
        App.classes = courses;

        //Save it to internal storage when this is set
        Save.courses();
    }

    /**
     * @param ebill The user's ebill {@link Statement}s
     */
    public static void setEbill(List<Statement> ebill){
        App.ebill = ebill;

        //Save it to internal storage when this is set
        Save.ebill();
    }

    /**
     * @param user The {@link User} info
     */
    public static void setUser(User user){
        App.userInfo = user;

        //Save it to internal storage when this is set
        Save.user();
    }

    /**
     * @param homepage The user's chosen homepage
     */
    public static void setHomepage(DrawerItem homepage){
        App.homePage = homepage;

        //Save it to internal storage when this is set
        Save.homepage();
    }

    /**
     * @param term The user's chosen default {@link Term}
     */
    public static void setDefaultTerm(Term term){
        App.defaultTerm = term;

        //Save it to internal storage when this is set
        Save.defaultTerm();
    }

    /**
     * @param wishlist The user's wishlist
     */
    public static void setWishlist(List<Course> wishlist) {
        App.wishlist = wishlist;
        //Save it to internal storage when this is set
        Save.wishlist();
    }

    /**
     * @param places The user's list of favorite {@link Place}s
     */
    public static void setFavoritePlaces(List<Place> places){
        App.favoritePlaces = places;
        //Save it to internal storage
        Save.favoritePlaces();
    }

    /* HELPER METHODS */
    
    //to be set after successful login
    public static void SetAlarm(Context context){
        BootReceiver.setAlarm(context);
    }
    public static void UnsetAlarm(Context context){
        BootReceiver.cancelAlarm(context);
    }
}
