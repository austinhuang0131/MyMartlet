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

package ca.appvelopers.mcgillmobile.model;

import org.joda.time.LocalDate;

import java.io.Serializable;

/**
 * One class term, consisting of a season and a year
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class Term implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * Term season
     */
    private Season season;
    /**
     * Term year
     */
    private int year;

    /**
     * Default Constructor
     *
     * @param season Term season
     * @param year   Term year
     */
    public Term(Season season, int year) {
        this.season = season;
        this.year = year;
    }

    /* GETTERS */

    /**
     * @return Term season
     */
    public Season getSeason() {
        return season;
    }

    /**
     * @return Term year
     */
    public int getYear() {
        return year;
    }

    /* HELPERS */

    /**
     * Checks if the current term is after the given term
     *
     * @param term The term to compare
     * @return True if the current term is after the given term, false otherwise
     */
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean isAfter(Term term) {
        if (year > term.getYear()) {
            //Year after
            return true;
        } else if (year < term.getYear()) {
            //Year Before
            return false;
        } else {
            //Same year: check the semesters
            return Integer.valueOf(season.getSeasonNumber()) >
                    Integer.valueOf(term.getSeason().getSeasonNumber());
        }
    }

    /**
     * @return Term Id, for parsing errors
     */
    public String getId() {
        return season.getId() + " " + year;
    }

    /**
     * @return String representation of the term
     */
    public String getString() {
        return season.toString() + " " + year;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Term)) {
            return false;
        }

        Term term = (Term) object;
        return season == term.getSeason() && year == term.getYear();
    }

    /* STATIC HELPERS */

    /**
     * Parses a term from a String
     *
     * @param term The term String
     * @return The parsed term
     */
    public static Term parseTerm(String term) {
        String[] termParts = term.split(" ");
        return new Term(Season.findSeason(termParts[0]), Integer.valueOf(termParts[1]));
    }

    /**
     * Gets the Term today falls in
     *
     * @return Today's corresponding term
     */
    public static Term getCurrentTerm() {
        LocalDate today = LocalDate.now();
        int month = today.getMonthOfYear();
        int year = today.getYear();

        if (month >= 9 && month <= 12) {
            return new Term(Season.FALL, year);
        } else if (month >= 1 && month <= 4) {
            return new Term(Season.WINTER, year);
        } else {
            return new Term(Season.SUMMER, year);
        }
    }
}
