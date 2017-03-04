/*
 * Copyright 2014-2017 Julien Guerinet
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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.guerinet.utils.prefs.BooleanPreference;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.ui.BaseActivity;
import ca.appvelopers.mcgillmobile.util.dagger.prefs.PrefsModule;

/**
 * Displays the EULA
 * @author Julien Guerinet
 * @author Joshua David Alfaro
 * @since 1.0.0
 */
public class AgreementActivity extends BaseActivity {
    /**
     * Container with the buttons for the initial EULA agreement
     */
    @BindView(R.id.buttons_container)
    LinearLayout buttons;
    /**
     * EULA {@link BooleanPreference}
     */
    @Inject
    @Named(PrefsModule.EULA)
    BooleanPreference eulaPref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);
        ButterKnife.bind(this);
        App.component(this).inject(this);

        boolean required = getIntent().getBooleanExtra(PrefsModule.EULA, false);
        setUpToolbar(!required);

        // Check if we need to display the buttons
        if (required) {
            buttons.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.button_agree)
    void agree() {
        eulaPref.set(true);
        setResult(RESULT_OK);
        finish();
    }

    @OnClick(R.id.button_decline)
    void decline() {
        eulaPref.set(false);
        setResult(RESULT_CANCELED);
        finish();
    }
}