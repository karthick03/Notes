package com.khf.inferno.notes;

import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.io.InputStream;

public class MainActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

        private static final int RC_SIGN_IN = 0;
        // Logcat tag
        private static final String TAG = "MainActivity";

        // Profile pic image size in pixels
        private static final int PROFILE_PIC_SIZE = 400;

        // Google client to interact with Google API
        private GoogleApiClient mGoogleApiClient;
        private static final int TIME_INTERVAL = 2000;
        private long mBackPressed;

        /**
         * A flag indicating that a PendingIntent is in progress and prevents us
         * from starting further intents.
         */
        private boolean mIntentInProgress;

        private boolean mSignInClicked;

        private ConnectionResult mConnectionResult;
        String uname;
        private SignInButton btnSignIn;
        private Button btnSignOut,proceed;
        private ImageView imgProfilePic;
        private TextView txtName, txtEmail,tx1,skip;
        private LinearLayout llProfileLayout;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);
                getSupportActionBar().setLogo(R.mipmap.ic_launcher);
                getSupportActionBar().setDisplayUseLogoEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xff006064));
                getSupportActionBar().setTitle("Google Login");
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
                btnSignOut = (Button) findViewById(R.id.btn_sign_out);
                proceed = (Button) findViewById(R.id.proceed);
                imgProfilePic = (ImageView) findViewById(R.id.imgProfilePic);
                txtName = (TextView) findViewById(R.id.txtName);
                txtEmail = (TextView) findViewById(R.id.txtEmail);
                tx1 = (TextView) findViewById(R.id.textView1);
                skip = (TextView) findViewById(R.id.skip);
                skip.setPaintFlags(skip.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                llProfileLayout = (LinearLayout) findViewById(R.id.llProfile);

                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(Plus.API, new Plus.PlusOptions.Builder().build())
                        .addScope(Plus.SCOPE_PLUS_LOGIN).build();
                if(!isNetworkAvailable())
                {Toast.makeText(getApplicationContext(),"Network not available/Enable data connection for first time.. ",Toast.LENGTH_SHORT).show();

                }
                btnSignIn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                if (!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected() && isNetworkAvailable()) {
                                        mSignInClicked = true;
                                        if (mConnectionResult != null) {
                                                resolveSignInError();
                                        } else {
                                                // If we don't have one though, we can start connect in
                                                // order to retrieve one.
                                                mGoogleApiClient.connect();
                                        }
                                }
                                else
                                {
                                        mGoogleApiClient.connect();
                                        if (!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected() && isNetworkAvailable()) {
                                                mSignInClicked = true;
                                                resolveSignInError();
                                        }
                                }
                        }
                });
                btnSignOut.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                if (mGoogleApiClient.isConnected() && isNetworkAvailable()) {
                                        Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                                        mGoogleApiClient.disconnect();
                                        updateUI(false);
                                        //mGoogleApiClient.connect();

                                }
                        }
                });
                proceed.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                Intent i = new Intent(MainActivity.this, Mainclass.class);
                                i.putExtra("user",uname);
                                startActivity(i);
                                overridePendingTransition(R.anim.slide3, R.anim.slide4);
                                finish();
                        }
                });
        }
        private boolean isNetworkAvailable() {
                ConnectivityManager connectivityManager
                        = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

        protected void onStart() {
                super.onStart();
                mGoogleApiClient.connect();
        }

        protected void onStop() {
                super.onStop();
                if (mGoogleApiClient.isConnected()) {
                        mGoogleApiClient.disconnect();
                }
        }

        /**
         * Method to resolve any signin errors
         * */
        private void resolveSignInError() {
                if (mConnectionResult.hasResolution()) {
                        try {
                                mIntentInProgress = true;
                                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
                        } catch (SendIntentException e) {
                                mIntentInProgress = false;
                                mGoogleApiClient.connect();
                        }
                } else {

                }
        }
        @Override
        public void onConnectionFailed(ConnectionResult result) {
                if (!result.hasResolution()) {
                        GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                                0).show();
                        return;
                }

                if (!mIntentInProgress) {
                        // Store the ConnectionResult for later usage
                        mConnectionResult = result;

                        if (mSignInClicked) {
                                // The user has already clicked 'sign-in' so we attempt to
                                // resolve all
                                // errors until the user is signed in, or they cancel.
                                resolveSignInError();
                        }
                }

        }

        @Override
        protected void onActivityResult(int requestCode, int responseCode,
                                        Intent intent) {
                if (requestCode == RC_SIGN_IN) {
                        if (responseCode != RESULT_OK) {
                                mSignInClicked = false;
                        }

                        mIntentInProgress = false;

                        if (!mGoogleApiClient.isConnecting()) {
                                mGoogleApiClient.connect();
                        }
                }
        }

        @Override
        public void onConnected(Bundle arg0) {
                mSignInClicked = false;

                // Get user's information
                getProfileInformation();

                // Update the UI after signin


        }

        /**
         * Updating the UI, showing/hiding buttons and profile layout
         * */
        private void updateUI(boolean isSignedIn) {
                if (isSignedIn) {
                        btnSignIn.setVisibility(View.GONE);
                        tx1.setVisibility(View.GONE);
                        skip.setVisibility(View.GONE);
                        btnSignOut.setVisibility(View.VISIBLE);
                        proceed.setVisibility(View.VISIBLE);
                        llProfileLayout.setVisibility(View.VISIBLE);
                } else {
                        btnSignIn.setVisibility(View.VISIBLE);
                        tx1.setVisibility(View.VISIBLE);
                        skip.setVisibility(View.VISIBLE);
                        btnSignOut.setVisibility(View.GONE);
                        proceed.setVisibility(View.GONE);
                        llProfileLayout.setVisibility(View.GONE);
                }
        }

        /**
         * Fetching user's information name, email, profile pic
         * */
        private void getProfileInformation() {
                try {
                        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                                Person currentPerson = Plus.PeopleApi
                                        .getCurrentPerson(mGoogleApiClient);
                                String personName = currentPerson.getDisplayName();
                                String personPhotoUrl = currentPerson.getImage().getUrl();
                                String personGooglePlusProfile = currentPerson.getUrl();
                                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                                uname=personName;
                                Log.e(TAG, "Name: " + personName + ", plusProfile: "
                                        + personGooglePlusProfile + ", email: " + email
                                        + ", Image: " + personPhotoUrl);

                                txtName.setText(personName);
                                txtEmail.setText(email);

                                // by default the profile url gives 50x50 px image only
                                // we can replace the value with whatever dimension we want by
                                // replacing sz=X
                                personPhotoUrl = personPhotoUrl.substring(0,
                                        personPhotoUrl.length() - 2)
                                        + PROFILE_PIC_SIZE;

                                new LoadProfileImage(imgProfilePic).execute(personPhotoUrl);
                                updateUI(true);
                        } else {
                                Toast.makeText(getApplicationContext(),
                                        "Person information is null", Toast.LENGTH_LONG).show();
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        @Override
        public void onConnectionSuspended(int arg0) {
                mGoogleApiClient.connect();
                updateUI(false);
        }
        /**
         * Background Async task to load user profile picture from url
         * */
        private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
                ImageView bmImage;

                public LoadProfileImage(ImageView bmImage) {
                        this.bmImage = bmImage;
                }

                protected Bitmap doInBackground(String... urls) {
                        String urldisplay = urls[0];
                        Bitmap mIcon11 = null;
                        try {
                                InputStream in = new java.net.URL(urldisplay).openStream();
                                mIcon11 = BitmapFactory.decodeStream(in);
                        } catch (Exception e) {
                                Log.e("Error", e.getMessage());
                                e.printStackTrace();
                        }
                        return mIcon11;
                }

                protected void onPostExecute(Bitmap result) {
                        bmImage.setImageBitmap(result);
                }
        }
        public void skipLogin(View view)
        {
                Intent intent=new Intent(MainActivity.this,Mainclass.class);
                intent.putExtra("user","Android");
                startActivity(intent);
                overridePendingTransition(R.anim.slide3, R.anim.slide4);
                finish();
        }
        @Override
        public void onBackPressed()
        {
                if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
                {
                        super.onBackPressed();
                        return;
                }
                else { Toast.makeText(getBaseContext(), "Tap back button in order to exit", Toast.LENGTH_SHORT).show(); }

                mBackPressed = System.currentTimeMillis();
        }
}