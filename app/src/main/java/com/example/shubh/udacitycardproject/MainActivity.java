package com.example.shubh.udacitycardproject;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private TextView name;
    private TextView email;
    private TextView bio;
    private TextView date;
    private CardView cardView;
    private TextView company;
    private ImageView avatar;
    private float x1, x2;
    private int index = 0;
    private int maxLength = -1;
    private JSONArray array;
    private static int MIN_DISTANCE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        initializeListeners();
        new CardTask().execute();
    }

    private void initializeViews() {
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        date = findViewById(R.id.date);
        company = findViewById(R.id.company);
        bio = findViewById(R.id.bio);
        avatar = findViewById(R.id.img);
        cardView = findViewById(R.id.card_view);
    }

    private void initializeListeners() {
        final Animation r2lOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_right_left);
        final Animation r2lInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right_left);
        final Animation l2rOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_left_right);
        final Animation l2rInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_left_right);
        r2lOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
               cardView.startAnimation(r2lInAnimation);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        l2rOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.startAnimation(l2rInAnimation);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        cardView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        return true;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        float deltaX = x2 - x1;
                        if (Math.abs(deltaX) > MIN_DISTANCE) {
                            if (deltaX < 0) {
                                if (index < maxLength && maxLength > 0) {
                                    index++;
                                    cardView.startAnimation(r2lOutAnimation);
                                }
                                fetchObjectDetails();

                            } else if (deltaX > 0) {
                                if (index > 0 && maxLength > 0) {
                                    index--;
                                    cardView.startAnimation(l2rOutAnimation);
                                }
                                fetchObjectDetails();
                            }
                            break;
                        }
                }

                return false;
            }
        });
    }

    private JSONArray populateData() {
        if (CommonUtils.isNetworkAvailable(getApplicationContext())) {
            return CommonUtils.downloadJSONArray(getString(R.string.url));
        } else {
            Toast.makeText(MainActivity.this, R.string.offline,
                    Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void fetchObjectDetails() {
        if (array == null) return;
        try {
            JSONObject jsonObject = array.getJSONObject(index);
            Glide.with(getApplicationContext())
                    .load(jsonObject.getString(getString(R.string.avatar)))
                    .into(avatar);
            String fullName = jsonObject.getString(getString(R.string.firstname)) + " " + jsonObject.getString(getString(R.string.lastname));
            name.setText(fullName.toUpperCase());
            String emailID = getString(R.string.email_camel) + jsonObject.getString(getString(R.string.email));
            email.setText(emailID);
            String info = getString(R.string.about_me) + jsonObject.getString(getString(R.string.bio));
            bio.setText(info);
            String startDate = getString(R.string.start_date) + jsonObject.getString(getString(R.string.start_date_camel));
            date.setText(startDate);
            String companyData = getString(R.string.company) + jsonObject.getString(getString(R.string.company_lower));
            company.setText(companyData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class CardTask extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... strings) {
            return populateData();
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            array = jsonArray;
            maxLength = jsonArray != null ? jsonArray.length() : -1;
            if (maxLength == 0) {
                Toast.makeText(MainActivity.this, R.string.api_fail,
                        Toast.LENGTH_SHORT).show();
            } else {
                fetchObjectDetails();
            }
        }
    }
}
