package com.example.local1.foodvote;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;

public class EventCreateActivity extends AppCompatActivity /* implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener */ {

    // Variables
    String nameOfEvent;
    String bus;
    String loc;
    String[] noFriends = {"You currently have no friends."};

    /*
    String mLatitudeText;
    String mLongitudeText;
    */
    String eventId;

    /*
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    YelpAPI yAPI;
    */

    List<Boolean> clicked = new ArrayList<Boolean>();
    List<Integer> votes = new ArrayList<Integer>();
    List<String> userFriendIDs = new ArrayList<String>();
    List<String> userFriends = new ArrayList<String>();
    List<String> eventParticipants = new ArrayList<String>();
    /*
    List<String> restaurants = new ArrayList<String>();
    */

    // Widgets
    Button createButton;
    Button cancelButton;
    EditText eventName;
    EditText typeOfBusiness;
    EditText location;
    ListView listFriends;
    Toolbar toolbar;

    //RunVariable
    boolean isSetUp = false;

    /*
    // YELP API keys
    private static final String CONSUMER_KEY = "FQFe1MpY3PGvxKy-Aq702g";
    private static final String CONSUMER_SECRET = "u_ifYEaonk6W5sf24SCXiGPKx6I";
    private static final String TOKEN = "6YSX1I448VpE2WQ1rrQv0NJRNJ9E9rOX";
    private static final String TOKEN_SECRET = "_iN4GhZsdgojn1WYZTHOi-q2jzM";
    */

    // TAG
    private static final String TAG = "EventCreateActivity ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeView();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(isSetUp == false) {
            selectUserFriends();
            createEvent();
            cancelEvent();

            isSetUp = true;
            /*
            mGoogleApiClient.connect();
            */
        }
    }

    /*
    public void onConnected(Bundle bundle) {
        loc = location.getText().toString();

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (loc.toLowerCase().equals("current location")) {
            // Use if developing on an android phone.
            if (mLastLocation != null) {
                mLatitudeText = String.valueOf(mLastLocation.getLatitude());
                mLongitudeText = String.valueOf(mLastLocation.getLongitude());
                String location = mLatitudeText + ", " + mLongitudeText;
                String YelpJSON = setUpAPIRet(yAPI, location);
                parseAndDisplayRestaurantOutput(YelpJSON);
                mGoogleApiClient.disconnect();
            }
        }

        // Use if developing on an emulator.
        if (mLastLocation == null) {
            mLatitudeText = "32.8810";
            mLongitudeText = "-117.2380";
            String location = mLatitudeText + ", " + mLongitudeText;
            String YelpJSON = setUpAPIRet(yAPI, location);
            parseAndDisplayRestaurantOutput(YelpJSON);
            mGoogleApiClient.disconnect();
        }
    }
    */

    /*
    @Override
    public void onConnectionSuspended(int x) {
        Log.e(TAG, "In onConnectionSuspended().");
        Log.e(TAG, "Connection suspended.");

        Toast.makeText(getApplicationContext(), "Connection suspended.", Toast.LENGTH_SHORT).show();
    }
    */

    /*
    @Override
    public void onConnectionFailed(ConnectionResult c) {
        Log.e(TAG, "In onConnectionFailed().");
        Log.e(TAG, "Connection failed.");

        Toast.makeText(getApplicationContext(), "Connection failed.", Toast.LENGTH_SHORT).show();
    }
    */

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(EventCreateActivity.this, FragmentContainer.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            ParseUser.getCurrentUser().logOut();
            Intent intent = new Intent(EventCreateActivity.this, LoginSignUpActivity.class);
            startActivity(intent);
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeView() {

        // Set view from event_create.xml.
        setContentView(R.layout.event_create);

        // Find widgets from event_create.xml.
        createButton = (Button) findViewById(R.id.createButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        eventName = (EditText) findViewById(R.id.eventName);
        typeOfBusiness = (EditText) findViewById(R.id.typeOfBusiness);
        location = (EditText) findViewById(R.id.location);
        listFriends = (ListView) findViewById(R.id.friendsList);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        // Set yelpAPI keys.
        yAPI = new YelpAPI(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);
        */

        /*
        // Build googleAPI client.
        if (checkPlayServices()) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        */
    }

    private void selectUserFriends() {
        try {
            // Get a list of user IDs from current user's friends list.
            userFriendIDs = ParseUser.getCurrentUser().getList("friendsList");

            // If user IDs were found, convert to usernames else print msg saying no friends.
            if (userFriendIDs == null) {
                // Display msg that current user has no friends.
                ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.listview_friends, noFriends);
                listFriends.setAdapter(adapter);
            } else {
                // Convert the user IDs to usernames and add to another list.
                for (int i = 0; i < userFriendIDs.size(); i++) {
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    userFriends.add(query.get(userFriendIDs.get(i)).getString("username"));
                }

                // Print out current user's friends.
                ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.listview_friends,
                        userFriends);
                listFriends.setAdapter(adapter);

                // Set clicked (selected) flag to false.
                for (int i = 0; i < userFriendIDs.size(); i++) {
                    clicked.add(false);
                }
            }
        } catch (NullPointerException e) {
            // Display msg that current user has no friends.
            ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.listview_friends,
                    noFriends);
            listFriends.setAdapter(adapter);
        } catch (ParseException e) {
            Log.e(TAG, "Unable to convert userIDs to usernames.");
        }

        // On item click, highlight friend and add them to list of event participants.
        listFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (userFriendIDs == null) {
                    Toast.makeText(getApplicationContext(), "Add friends.",
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(EventCreateActivity.this, FragmentContainer.class);
                    startActivity(intent);
                    finish();
                } else {
                    // If false, highlight friends and add to event, else do the opposite.
                    if (clicked.get(position)) {
                        eventParticipants.remove(userFriendIDs.get(position));
                        listFriends.getChildAt(position).setBackgroundColor(Color.TRANSPARENT);
                        clicked.set(position, false);
                    } else {
                        eventParticipants.add(userFriendIDs.get(position));
                        listFriends.getChildAt(position).setBackgroundColor(0xffecfeea);
                        clicked.set(position, true);
                    }
                }
            }
        });
    }

    private void createEvent() {
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameOfEvent = eventName.getText().toString();
                bus = typeOfBusiness.getText().toString();
                loc = location.getText().toString();

                if (nameOfEvent.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter name of event.",
                            Toast.LENGTH_LONG).show();
                } else {
                    final ParseObject event = new ParseObject("Event");
                    event.put("eventName", nameOfEvent);
                    event.put("eventOwner", ParseUser.getCurrentUser().getObjectId());

                    event.addAll("eventParticipants", eventParticipants);
                    event.add("eventParticipants", ParseUser.getCurrentUser().getObjectId());

                    for (int i = 0; i < 10; i++) {
                        votes.add(0);
                    }
                    event.addAll("votes", votes);

                    ParseACL newACL = new ParseACL();
                    newACL.setPublicReadAccess(true);
                    newACL.setPublicWriteAccess(true);
                    event.setACL(newACL);
                    event.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                // Add event to current user.
                                ParseUser.getCurrentUser().add("eventsList",
                                        event.getObjectId());

                                eventId = event.getObjectId();

                                // Send a request to each added participant.
                                for (int i = 0; i < eventParticipants.size(); i++) {
                                    ParseObject request = new
                                            ParseObject("EventRequest");
                                    request.put("requestFrom",
                                            ParseUser.getCurrentUser().getObjectId());
                                    request.put("requestTo",
                                            eventParticipants.get(i));
                                    request.put("action", "add");
                                    request.put("status", "pending");
                                    request.put("eventId", eventId);
                                    ParseACL newACL = new ParseACL();
                                    newACL.setPublicReadAccess(true);
                                    newACL.setPublicWriteAccess(true);
                                    request.setACL(newACL);
                                    request.saveInBackground();
                                }

                                ParseUser.getCurrentUser().saveInBackground(
                                        new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    Intent intent = new Intent(
                                                            EventCreateActivity.this,
                                                            YelpDataActivity.class);
                                                    intent.putExtra("eventId",
                                                            eventId);
                                                    intent.putExtra("businessType",
                                                            bus);
                                                    intent.putExtra("location",
                                                            loc);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }
                                        });

                                /*
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                                query.whereEqualTo("eventName", nameOfEvent);
                                query.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> list, ParseException e) {
                                        if (e == null) {
                                            // Add event to current user.
                                            ParseUser.getCurrentUser().add("eventsList",
                                                    list.get(0).getObjectId());

                                            eventId = list.get(0).getObjectId();

                                            // Send a request to each added participant.
                                            for (int i = 0; i < eventParticipants.size(); i++) {
                                                ParseObject request = new
                                                        ParseObject("EventRequest");
                                                request.put("requestFrom",
                                                        ParseUser.getCurrentUser().getObjectId());
                                                request.put("requestTo",
                                                        eventParticipants.get(i));
                                                request.put("action", "add");
                                                request.put("status", "pending");
                                                request.put("eventId", eventId);
                                                ParseACL newACL = new ParseACL();
                                                newACL.setPublicReadAccess(true);
                                                newACL.setPublicWriteAccess(true);
                                                request.setACL(newACL);
                                                request.saveInBackground();
                                            }

                                            ParseUser.getCurrentUser().saveInBackground(
                                                    new SaveCallback() {
                                                        @Override
                                                        public void done(ParseException e) {
                                                            if (e == null) {
                                                                Intent intent = new Intent(
                                                                        EventCreateActivity.this,
                                                                        YelpDataActivity.class);
                                                                intent.putExtra("eventId",
                                                                        eventId);
                                                                intent.putExtra("businessType",
                                                                        bus);
                                                                intent.putExtra("location",
                                                                        loc);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                                */
                            }
                        }
                    });
                    /*
                    // Create the event/set all parameters for the event.
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                    query.whereEqualTo("eventName", nameOfEvent);
                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, ParseException e) {
                            if (parseObject == null) {
                                ParseObject event = new ParseObject("Event");
                                event.put("eventName", nameOfEvent);
                                event.put("eventOwner", ParseUser.getCurrentUser().getObjectId());

                                event.addAll("eventParticipants", eventParticipants);
                                event.add("eventParticipants", ParseUser.getCurrentUser().getObjectId());

                                for (int i = 0; i < 10; i++) {
                                    votes.add(0);
                                }
                                event.addAll("votes", votes);

                                ParseACL newACL = new ParseACL();
                                newACL.setPublicReadAccess(true);
                                newACL.setPublicWriteAccess(true);
                                event.setACL(newACL);
                                event.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                                            query.whereEqualTo("eventName", nameOfEvent);
                                            query.findInBackground(new FindCallback<ParseObject>() {
                                                @Override
                                                public void done(List<ParseObject> list, ParseException e) {
                                                    if (e == null) {
                                                        // Add event to current user.
                                                        ParseUser.getCurrentUser().add("eventsList",
                                                                list.get(0).getObjectId());

                                                        eventId = list.get(0).getObjectId();

                                                        // Send a request to each added participant.
                                                        for (int i = 0; i < eventParticipants.size(); i++) {
                                                            ParseObject request = new
                                                                    ParseObject("EventRequest");
                                                            request.put("requestFrom",
                                                                    ParseUser.getCurrentUser().getObjectId());
                                                            request.put("requestTo",
                                                                    eventParticipants.get(i));
                                                            request.put("action", "add");
                                                            request.put("status", "pending");
                                                            request.put("eventId", eventId);
                                                            ParseACL newACL = new ParseACL();
                                                            newACL.setPublicReadAccess(true);
                                                            newACL.setPublicWriteAccess(true);
                                                            request.setACL(newACL);
                                                            request.saveInBackground();
                                                        }

                                                        ParseUser.getCurrentUser().saveInBackground(
                                                                new SaveCallback() {
                                                            @Override
                                                            public void done(ParseException e) {
                                                                if (e == null) {
                                                                    Intent intent = new Intent(
                                                                            EventCreateActivity.this,
                                                                            YelpDataActivity.class);
                                                                    intent.putExtra("eventId",
                                                                            eventId);
                                                                    intent.putExtra("businessType",
                                                                            bus);
                                                                    intent.putExtra("location",
                                                                            loc);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Please enter a different name.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    */
                }
            }
        });
    }

    private void cancelEvent() {
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventCreateActivity.this, FragmentContainer.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
