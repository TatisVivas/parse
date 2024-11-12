package com.example.parse;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.parse.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "GREETING_APP";
    public static final String USER_CN = "SmartUser";
    EditText name, lastName;
    Button save;
    LinearLayout listUsers;

    //Parse live query
    ParseLiveQueryClient parseLiveQueryClient;
    ParseQuery<ParseObject> parseQuery; //suscription to changes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
        parseQuery = ParseQuery.getQuery(USER_CN);
        SubscriptionHandling<ParseObject> subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);
        subscriptionHandling.handleEvents(new SubscriptionHandling.HandleEventsCallback<ParseObject>() {
            @Override
            public void onEvents(ParseQuery<ParseObject> query, SubscriptionHandling.Event event, ParseObject object) {

                dataChanged(query);
            }
        });
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, new SubscriptionHandling.HandleEventCallback<ParseObject>() {
            @Override
            public void onEvent(ParseQuery<ParseObject> query, ParseObject object) {
                dataCreated(object);
            }
        });
        name = findViewById(R.id.name);
        lastName = findViewById(R.id.lastName);
        save = findViewById(R.id.save);
        listUsers = findViewById(R.id.listUsers);
        loadUsers();
    }

    private void loadUsers(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery(USER_CN);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(objects!=null) {
                    listUsers.removeAllViews();
                    for (ParseObject row : objects) {
                        String name = (String) row.get("name");
                        String lastName = (String) row.get("lastName");
                        TextView listItem = new TextView(getApplicationContext());
                        listItem.setText(name + " " + lastName);
                        listItem.setTextSize(20);
                        listItem.setHeight(200);
                        listUsers.addView(listItem);
                    }
                }
            }
        });
    }

    private void dataChanged(ParseQuery<ParseObject> query) {
        Log.i(TAG, "An event happened!!");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                listUsers.removeAllViews();
                for(ParseObject row : objects){
                    String name = (String) row.get("name");
                    String lastName = (String) row.get("lastName");
                    TextView listItem = new TextView(getApplicationContext());
                    listItem.setText(name+" "+lastName);
                    listItem.setTextSize(20);
                    listItem.setHeight(200);
                    listUsers.addView(listItem);
                }
            }
        });

    }

    private void dataCreated(ParseObject user) {
        Log.i(TAG, "A create happened!! \n" + user.get("name"));
    }

    public void saveData(View v) {
        if(validateForm()) {
            Log.i(TAG, "Attempt to write on parse");
            //ParseObject firstObject = new  ParseObject("FirstClass");
            ParseObject firstObject = new ParseObject(USER_CN);
            String name = this.name.getText().toString();
            String lastName = this.lastName.getText().toString();

            firstObject.put("name", name);
            firstObject.put("lastName", lastName);
            firstObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, e.getLocalizedMessage());
                    } else {
                        Log.d(TAG, "Object saved.");
                    }
                }
            });
        }
        this.name.setText("");
        this.lastName.setText("");
    }

    private boolean validateForm(){
        if("".equals(this.name.getText().toString()) || "".equals(this.lastName.getText().toString()))
            return false;
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (parseQuery != null)
            parseLiveQueryClient.unsubscribe(parseQuery);
    }
}