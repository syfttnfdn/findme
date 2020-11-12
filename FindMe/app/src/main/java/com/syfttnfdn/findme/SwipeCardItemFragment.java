package com.syfttnfdn.findme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.io.InputStream;

public class SwipeCardItemFragment extends Fragment{
    private DTOProfile profile;
    private TextView tvProfilName;
    private TextView tvProfilStatus;
    private TextView tvProfileLastLocation;
    private ImageView ivProfilePhoto;
    private MainActivity mainActivity;
    private Context context;
    private LayoutInflater inflater;
    private ViewGroup container;
    private Bundle savedInstanceState;

    InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                int type = Character.getType(source.charAt(i));
                if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                    return "";
                }
            }
            return null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        this.container = container;
        this.savedInstanceState = savedInstanceState;
        ViewGroup rootView = null;
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_swipe_card_item, container, false);
        if (getActivity() instanceof MainActivity) {
            mainActivity = (MainActivity) getActivity();
        }
        initUI(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(this.toString(), "onResume()");
    }

    public static Fragment create(DTOProfile profile) {
        SwipeCardItemFragment fragment = new SwipeCardItemFragment();
        fragment.setProfile(profile);
        return fragment;
    }

    private void setProfile(DTOProfile profile) {
        this.profile = profile;
    }

    public void initUI(ViewGroup viewGroup) {
        tvProfilName = (TextView) viewGroup.findViewById(R.id.tvProfilName);
        tvProfilStatus = (TextView) viewGroup.findViewById(R.id.tvProfilStatus);
        tvProfileLastLocation = (TextView) viewGroup.findViewById(R.id.tvProfileLastLocation);
        ivProfilePhoto = viewGroup.findViewById(R.id.ivProfilePhoto);

        fillUI(profile);
    }

    public void fillUI(DTOProfile profile) {
        tvProfilName.setText(profile.Name);
        tvProfilStatus.setText(profile.Status);
        tvProfileLastLocation.setText(profile.LastLocation);
        new DownloadImageTask(ivProfilePhoto).execute(profile.ImageUrl);
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    public ViewGroup getContainer() {
        return container;
    }

    public Bundle getSavedInstanceState() {
        return savedInstanceState;
    }

    public void refreshAdapters() {

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
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
}