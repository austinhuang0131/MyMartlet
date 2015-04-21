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

package ca.appvelopers.mcgillmobile.ui.courses;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.thread.RegistrationThread;
import ca.appvelopers.mcgillmobile.ui.ChangeSemesterDialog;
import ca.appvelopers.mcgillmobile.ui.base.BaseFragment;
import ca.appvelopers.mcgillmobile.ui.view.DialogHelper;
import ca.appvelopers.mcgillmobile.util.Analytics;

/**
 * Shows the user all of the courses the user has taken or is currently registered in
 * @author Julien Guerinet
 * @author Joshua David Alfaro
 * @version 2.0
 * @since 1.0
 */
@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
public class CoursesFragment extends BaseFragment {
    /**
     * The ListView for the courses
     */
    private ListView mListView;
    /**
     * The button to unregister from a course
     */
    private TextView mUnregisterButton;
    /**
     * The ListView adapter
     */
    private CoursesAdapter mAdapter;
    /**
     * The current term shown
     */
    private Term mTerm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Fragment has a menu
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);

        View view = View.inflate(mActivity, R.layout.fragment_wishlist, container);

        //Lock the portrait mode for this section
        lockPortraitMode();

        Analytics.getInstance().sendScreen("View Courses");

        // Views
        mListView = (ListView)view.findViewById(R.id.courses_list);
        mListView.setEmptyView(view.findViewById(R.id.courses_empty));

        //Term
        mTerm = App.getDefaultTerm();

        //Register button
        mUnregisterButton = (TextView)view.findViewById(R.id.course_register);
        mUnregisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get checked courses from adapter
                final List<Course> unregisterCourses = mAdapter.getCheckedClasses();

                //Too many courses
                if (unregisterCourses.size() > 10) {
                    Toast.makeText(mActivity, getString(R.string.courses_too_many_courses),
                            Toast.LENGTH_SHORT).show();
                }
                //No courses
                else if (unregisterCourses.isEmpty()) {
                    Toast.makeText(mActivity, getString(R.string.courses_none_selected),
                            Toast.LENGTH_SHORT).show();
                }
                else if (unregisterCourses.size() > 0) {
                    //Ask for confirmation before unregistering
                    new AlertDialog.Builder(mActivity)
                            .setTitle(getString(R.string.unregister_dialog_title))
                            .setMessage(getString(R.string.unregister_dialog_message))
                            .setPositiveButton(getString(android.R.string.yes),
                                new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Execute unregistration of checked classes in a new thread
                                        unregister(unregisterCourses);
                                    }
                                })
                            .setNegativeButton(getString(android.R.string.cancel), null)
                            .create()
                            .show();
                }
            }
        });

        //Remove this button
        view.findViewById(R.id.course_wishlist).setVisibility(View.GONE);

        //Done loading the view
        hideLoadingIndicator();

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        loadInfo();
    }

    /**
     * Reloads all of the info in the view
     */
    private void loadInfo(){
        //Set the title
        mActivity.setTitle(mTerm.toString(mActivity));

        //User can unregister if the current term is in the list of terms to register for
        boolean canUnregister = App.getRegisterTerms().contains(mTerm);

        //Change the text and the visibility if we are in the list of currently registered courses
        if(canUnregister){
            mUnregisterButton.setVisibility(View.VISIBLE);
            mUnregisterButton.setText(getString(R.string.courses_unregister));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            mUnregisterButton.setLayoutParams(params);
        }
        else{
            mUnregisterButton.setVisibility(View.GONE);
        }

        mAdapter = new CoursesAdapter(mActivity, mTerm, canUnregister);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.refresh_change_semester, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Change Semester
        if(item.getItemId() == R.id.action_change_semester){
            final ChangeSemesterDialog dialog = new ChangeSemesterDialog(mActivity, false, mTerm);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    Term term = dialog.getTerm();

                    //Term selected: download the classes for the selected term
                    if(term != null){
                        mTerm = term;
                        boolean success = refreshCourses(mTerm);
                        if(success){
                            loadInfo();
                        }
                    }
                }
            });
            dialog.show();
            return true;
        }
        //Refresh
        else if(item.getItemId() == R.id.action_refresh){
            boolean success = refreshCourses(mTerm);
            if(success){
                loadInfo();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Tries to unregister from the given courses
     *
     * @param courses The list of courses to unregister from
     */
    private void unregister(List<Course> courses){
        //Show the user we are loading
        mActivity.showToolbarProgress(true);

        //Run the registration thread
        RegistrationThread thread = new RegistrationThread(mActivity, mTerm, courses, false);
        thread.start();

        //Wait until it's finished
        synchronized(thread){
            thread.waitEnd();
        }

        //Successful call
        if(thread.success()){
            //Get the errors from the thread
            Map<String, String> errors = thread.getErrors();

            //Success
            if(errors.isEmpty()){
                Toast.makeText(mActivity, R.string.unregistration_success, Toast.LENGTH_LONG)
                        .show();
            }
            //Display a message if a registration error has occurred
            else{
                String errorMessage = "";
                for(String crn : errors.keySet()){
                    //Find the corresponding course
                    for(Course course : courses){
                        if(course.getCRN() == Integer.valueOf(crn)){
                            //Add this class to the error message
                            errorMessage += course.getCode() +  " (" + course.getType() + ") - " +
                                    errors.get(crn) + "\n";
                            break;
                        }
                    }
                }

                //Show an alert dialog with the errors
                DialogHelper.showNeutralAlertDialog(mActivity,
                        getString(R.string.unregistration_error), errorMessage);
            }
        }

        //Refresh the courses
        refreshCourses(mTerm);
    }
}