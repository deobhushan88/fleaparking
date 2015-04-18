package edu.sjsu.cmpe295.parket;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;


public class Settings extends Activity {

    Context context;
    ListView lvSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        Toolbar mainToolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        mainToolbar.setTitle(getResources().getString(R.string.title_activity_settings));
        mainToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        mainToolbar.setNavigationIcon(R.drawable.ic_back);
        mainToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        String[] settings1 = new String[]{"Account Information","Email", "Phone no","Payment Information","VISA", "Notification","Push Notification", "Email Notification"};

        String[] settings2 = new String[]{"me@outlook.com","222-333-4444","Ending in 2284"};

        context = this;

        lvSettings = (ListView) findViewById(R.id.listViewSettings);

        lvSettings.setAdapter(new SettingsAdapter(this, settings1, settings2));
        lvSettings.setItemsCanFocus(true);

        /*lvSettings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {

                Log.d("****(((()))))","ttttt");

                String selected = ((TextView) parent.findViewById(R.id.tv2a1)).getText().toString();

                Toast toast=Toast.makeText(getApplicationContext(), selected, Toast.LENGTH_SHORT);
                toast.show();

            }
        });*/

    }

}
