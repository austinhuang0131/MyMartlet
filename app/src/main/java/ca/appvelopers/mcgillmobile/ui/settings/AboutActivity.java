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

package ca.appvelopers.mcgillmobile.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.guerinet.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Person;
import ca.appvelopers.mcgillmobile.ui.BaseActivity;
import ca.appvelopers.mcgillmobile.util.Analytics;

/**
 * Displays information about the Appvelopers team
 * @author Rafi Uddin
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class AboutActivity extends BaseActivity {
    /**
     * The list view
     */
    @Bind(android.R.id.list)
    protected RecyclerView list;
    /**
     * The list of people
     */
    private List<Person> people;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        setUpToolbar(true);
        Analytics.get().sendScreen("About");

        people = new ArrayList<>();
        //Adnan
        people.add(new Person(R.string.about_adnan, R.drawable.about_adnan,
                R.string.about_adnan_role, R.string.about_adnan_description,
                R.string.about_adnan_linkedin, R.string.about_adnan_email));

        //Hernan
        people.add(new Person(R.string.about_hernan, R.drawable.about_hernan,
                R.string.about_hernan_role, R.string.about_hernan_description,
                R.string.about_hernan_linkedin, R.string.about_hernan_email));

        //Josh
        people.add(new Person(R.string.about_joshua, R.drawable.about_josh,
                R.string.about_joshua_role, R.string.about_joshua_description,
                R.string.about_joshua_linkedin, R.string.about_joshua_email));

        //Julia
        people.add(new Person(R.string.about_julia, R.drawable.about_julia,
                R.string.about_julia_role, R.string.about_julia_description,
                R.string.about_julia_linkedin, R.string.about_julia_email));

        //Julien
        people.add(new Person(R.string.about_julien, R.drawable.about_julien,
                R.string.about_julien_role, R.string.about_julien_description,
                R.string.about_julien_linkedin, R.string.about_julien_email));

        //Quang
        people.add(new Person(R.string.about_quang, R.drawable.about_quang,
                R.string.about_quang_role, R.string.about_quang_description,
                R.string.about_quang_linkedin, R.string.about_quang_email));

        //Ryan
        people.add(new Person(R.string.about_ryan, R.drawable.about_ryan,
                R.string.about_ryan_role, R.string.about_ryan_description,
                R.string.about_ryan_linkedin, R.string.about_ryan_email));

        //Selim
        people.add(new Person(R.string.about_selim, R.drawable.about_selim,
                R.string.about_selim_role, R.string.about_selim_description,
                R.string.about_selim_linkedin, R.string.about_selim_email));

        //Shabbir
        people.add(new Person(R.string.about_shabbir, R.drawable.about_shabbir,
                R.string.about_shabbir_role, R.string.about_shabbir_description,
                R.string.about_shabbir_linkedin, R.string.about_shabbir_email));

        //Xavier
        people.add(new Person(R.string.about_xavier, R.drawable.about_xavier,
                R.string.about_xavier_role, R.string.about_xavier_description,
                R.string.about_xavier_linkedin, R.string.about_xavier_email));

        //Yulric
        people.add(new Person(R.string.about_yulric, R.drawable.about_yulric,
                R.string.about_yulric_role, R.string.about_yulric_description,
                R.string.about_yulric_linkedin, R.string.about_yulric_email));

        //Set up the list
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(new PersonAdapter());
    }

    @OnClick(R.id.github)
    protected void gitHub() {
        Utils.openURL(this, "https://github.com/jguerinet/MyMartlet-Android/");
    }

    /**
     * Displays the developers in the About page
     */
    public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonHolder> {

        @Override
        public PersonHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new PersonHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_person, parent, false));
        }

        @Override
        public void onBindViewHolder(PersonHolder holder, int position) {
            holder.bind(people.get(position));
        }

        @Override
        public int getItemCount() {
            return people.size();
        }

        class PersonHolder extends RecyclerView.ViewHolder {
            /**
             * Person's name
             */
            @Bind(R.id.name)
            protected TextView name;
            /**
             * Person's picture
             */
            @Bind(R.id.picture)
            protected ImageView picture;
            /**
             * Person's role
             */
            @Bind(R.id.role)
            protected TextView role;
            /**
             * Person's description
             */
            @Bind(R.id.description)
            protected TextView description;
            /**
             * URL to person's LinkedIn
             */
            @Bind(R.id.linkedin)
            protected ImageView linkedIn;
            /**
             * Person's email
             */
            @Bind(R.id.email)
            protected ImageView email;

            public PersonHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bind(final Person person) {
                name.setText(person.getName());

                Picasso.with(AboutActivity.this)
                        .load(person.getPictureId())
                        .into(picture);

                role.setText(person.getRole());
                description.setText(person.getDescription());
                linkedIn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Analytics.get().sendEvent("About", "Linkedin", getString(person.getName()));
                        Utils.openURL(AboutActivity.this, getString(person.getLinkedIn()));
                    }
                });
                email.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Analytics.get().sendEvent("About", "Email", getString(person.getName()));

                        //Send an email :
                        Intent emailIntent = new Intent(Intent.ACTION_SEND)
                                .putExtra(Intent.EXTRA_EMAIL,
                                        new String[]{getString(person.getEmail())})
                                .setType("message/rfc822");
                        startActivity(Intent.createChooser(emailIntent,
                                getString(R.string.about_email_picker_title)));
                    }
                });
            }
        }
    }
}