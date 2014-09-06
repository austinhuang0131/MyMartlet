package ca.appvelopers.mcgillmobile.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.base.BaseActivity;
import ca.appvelopers.mcgillmobile.activity.walkthrough.WalkthroughActivity;
import ca.appvelopers.mcgillmobile.object.HelpItem;

/**
 * Author : Julien
 * Date :  2014-06-17 10:15 PM
 * Copyright (c) 2014 Julien Guerinet. All rights reserved.
 */
public class HelpActivity extends BaseActivity{
    private ArrayList<HelpItem> mHelpItemList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        //Set up the email walkthrough and walkthrough buttons
        TextView emailWalkthrough = (TextView)findViewById(R.id.help_email);
        emailWalkthrough.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        TextView walkthrough = (TextView)findViewById(R.id.help_walkthrough);
        walkthrough.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpActivity.this, WalkthroughActivity.class);
                startActivity(intent);
            }
        });
        
        //FAQ ListView
        ListView helpListView = (ListView) findViewById(R.id.helpListView);

        populateList();

        HelpAdapter adapter = new HelpAdapter(this, mHelpItemList);
        helpListView.setAdapter(adapter);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateList(){
        //FAQ
        mHelpItemList = new ArrayList<HelpItem>();
        mHelpItemList.add(new HelpItem(getResources().getString(R.string.help_question1),getResources().getString(R.string.help_answer1)));
        mHelpItemList.add(new HelpItem(getResources().getString(R.string.help_question2), getResources().getString(R.string.help_answer2)));
        mHelpItemList.add(new HelpItem(getResources().getString(R.string.help_question3), getResources().getString(R.string.help_answer3)));
    }

    public class HelpAdapter extends BaseAdapter {
        private List<HelpItem> mHelp;
        private Context mContext;

        public HelpAdapter (Context context, List<HelpItem> help){
            this.mContext = context;
            this.mHelp = help;
        }
        @Override
        public int getCount() {
            return mHelp.size();
        }

        @Override
        public HelpItem getItem(int i) {
            return mHelp.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null){
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.item_faq,null);
            }

            assert (view != null);

            HelpItem helpItem = getItem(i);

            TextView question = (TextView) view.findViewById(R.id.about_question);
            question.setText(helpItem.getQuestion());

            TextView answer = (TextView) view.findViewById(R.id.about_answer);
            answer.setText(helpItem.getAnswer());

            return view;
        }
    }
}

