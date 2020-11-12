package com.syfttnfdn.findme;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String BASE_URL = "https://rickandmortyapi.com/api/character/";
    private static String lastPageUrl = "https://rickandmortyapi.com/api/character/";
    private View vsSwipeCard;
    private DTOProfile activeProfile;
    private ArrayList<DTOProfile> profiles = new ArrayList<>();
    private MainActivity selfActivity;
    private Button btnNext;
    private Button btnPrev;
    public String pageNo;
    private TextView tvPageNo;
    private ImageButton btnRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        selfActivity = this;

        vsSwipeCard = ((ViewStub) findViewById(R.id.vsSwipeCard)).inflate();
        tvPageNo = findViewById(R.id.tvPageNo);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOnline())
                    jsonAsync(lastPageUrl);
                else {
                    Snackbar.make(view, "Lütfen İnternet bağlantınız olduğundan emin olun!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);


        View.OnClickListener buttonListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activeProfile != null) {
                    if (view.getId() == R.id.btnNext) {
                        if (activeProfile.NextUrl != null && !activeProfile.NextUrl.toLowerCase().equals("null")) {
                            jsonAsync(activeProfile.NextUrl);
                        }
                    } else if (view.getId() == R.id.btnPrev) {
                        if (activeProfile.PrevUrl != null && !activeProfile.PrevUrl.toLowerCase().equals("null")) {
                            jsonAsync(activeProfile.PrevUrl);
                        }
                    }
                } else {
                    Snackbar.make(view, "Lütfen İnternet bağlantınız olduğundan emin olun!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        };
        btnNext.setOnClickListener(buttonListener);
        btnPrev.setOnClickListener(buttonListener);

        if (isOnline())
            jsonAsync(BASE_URL);
        else {
            Toast.makeText(selfActivity, "Lütfen İnternet bağlantınız olduğundan emin olun!", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void jsonAsync(String jsonUrl) {
        if (!jsonUrl.equals(BASE_URL) && !jsonUrl.toLowerCase().equals("null")) {
            if (jsonUrl.substring(jsonUrl.length() - 2, jsonUrl.length() - 1).equals("="))
                pageNo = jsonUrl.substring(jsonUrl.length() - 1);
            else if (jsonUrl.substring(jsonUrl.length() - 3, jsonUrl.length() - 2).equals("="))
                pageNo = jsonUrl.substring(jsonUrl.length() - 2);
            else
                pageNo = "0";
            tvPageNo.setText(String.format("Sayfa %s", pageNo));
        }
        lastPageUrl = jsonUrl;

        final JsonAsyncTask jsonAsyncTask = new JsonAsyncTask(selfActivity, new JsonAsyncTask.JsonAsyncTaskListener() {
            @Override
            public void onSuccess(String result) {
                try {
                    profiles = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length() - 1; i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        DTOProfile profile = new DTOProfile();
                        profile.Id = (long) (int) obj.get("id");
                        profile.Name = obj.get("name").toString();
                        profile.Status = obj.get("status").toString();
                        profile.Gender = obj.get("gender").toString();
                        profile.LastLocation = obj.getJSONObject("location").get("name").toString();
                        profile.ImageUrl = obj.get("image").toString();
                        profile.NextUrl = jsonObject.getJSONObject("info").get("next").toString();
                        profile.PrevUrl = jsonObject.getJSONObject("info").get("prev").toString();
                        profiles.add(profile);
                    }
                    if (profiles != null && profiles.size() > 0) {
                        setActiveProfile(profiles.get(0));
                        SwipeCardView swipeCardView = new SwipeCardView(selfActivity, vsSwipeCard, profiles);
                        swipeCardView.show();
                    } else {
                        Toast.makeText(selfActivity, "Profil bilgileri alınamadı!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed() {
                Toast.makeText(selfActivity, "Profil bilgileri alınamadı!", Toast.LENGTH_SHORT).show();
            }
        });
        jsonAsyncTask.execute(jsonUrl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public DTOProfile getActiveProfile() {
        return activeProfile;
    }

    public void setActiveProfile(DTOProfile activeProfile) {
        this.activeProfile = activeProfile;
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
