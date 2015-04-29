package edu.sjsu.cmpe295.parket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import edu.sjsu.cmpe295.parket.util.AuthUtil;


public class Settings extends Activity {

    AuthUtil authUtil;
    String[] titles;
    String[] values;
    int[] images;
    SettingsAdapter adapter;
    ListView listview;
    Activity host;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_listview);
        host = this;
        // Set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        toolbar.setTitle(getResources().getString(R.string.title_activity_settings));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        authUtil = new AuthUtil(this);
        String emailAddress = authUtil.getUserEmail();
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("parket", Context.MODE_PRIVATE);
        String userName = sharedPref.getString(getString(R.string.shared_pref_key_user_name),"No value");
        titles = new String[]{
                "Name","Email Address"
        };
        values = new String[]{
                userName, emailAddress
        };
        images = new int[]{
          R.drawable.ic_person,R.drawable.ic_email, R.drawable.ic_phone
        };
        // Set list view adapter
        adapter = new SettingsAdapter(getApplicationContext(),titles,values,images);
        listview = (ListView) findViewById(R.id.listViewSettings);
        listview.setAdapter(adapter);

        // Get Button
        Button signOut = (Button) findViewById(R.id.signOut);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign out, go back to LoginActivity
                // TODO: Use Google API Client to sign out
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
            }
        });

    }

    private class SettingsAdapter extends BaseAdapter {
        Context context;
        String[] titles;
        String[] values;
        int [] images;
        private LayoutInflater inflater = null;

        public SettingsAdapter(Context context,
                                       String[] data_titles, String[] data_values, int[] data_images) {
            this.context = context;
            this.titles = data_titles;
            this.values = data_values;
            this.images = data_images;
            this.inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.listitem_settings, null);
            ImageView icon = (ImageView) convertView
                    .findViewById(R.id.list_item_settings_icon);
            TextView title = (TextView) convertView
                    .findViewById(R.id.list_item_settings_title);
            TextView value = (TextView) convertView
                    .findViewById(R.id.list_item_settings_value);
            switch (position)
            {
                case 0:
                    icon.setImageResource(images[0]);
                    icon.setColorFilter(Color.argb(255, 255, 160, 0));
                    title.setText(titles[0]);
                    value.setText(values[0]);
                    break;
                case 1:
                    icon.setImageResource(images[1]);
                    icon.setColorFilter(Color.argb(255, 255, 160, 0));
                    title.setText(titles[1]);
                    value.setText(values[1]);
                    break;
            }
            return convertView;
        }
    }
}
