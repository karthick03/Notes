package com.khf.inferno.notes;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by karthick on 15/08/15.
 */
public class Mainclass extends ActionBarActivity{
        Button but;
        private static final int TIME_INTERVAL = 2000;
        private long mBackPressed;
        @Override
        public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_notes);
                getSupportActionBar().setLogo(R.mipmap.ic_launcher);
                getSupportActionBar().setDisplayUseLogoEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xff006064));
                getSupportActionBar().setTitle("#Notes");
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                but=(Button)findViewById(R.id.button);
                but.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                Toast.makeText(Mainclass.this,"Button temporarily disabled!!!",Toast.LENGTH_SHORT).show();
                        }
                });
        }
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {                return super.onOptionsItemSelected(item);
        }
        @Override
        public void onBackPressed() {
                if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
                {
                        super.onBackPressed();
                        Mainclass.this.finish();
                }
                else { Toast.makeText(getBaseContext(), "Tap back button in order to exit", Toast.LENGTH_SHORT).show(); }
                mBackPressed = System.currentTimeMillis();

        }
}
