package com.syfttnfdn.findme;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.HashMap;

public class SwipeCardView{

    private Fragment activeFragment;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private HashMap<Long, Fragment> fragmentMap = new HashMap<Long, Fragment>();
    private int currentProfileIndex = -1;
    private Context context;
    private View rootView;
    private MainActivity mainActivity;
    private ArrayList<DTOProfile> profiles;

    public SwipeCardView(Context context, View rootView, ArrayList<DTOProfile> profiles) {
        this.context = context;
        this.rootView = rootView;
        this.profiles = profiles;
        initUI(rootView);
    }

    public Activity getActivity() {
        if (context != null)
            if (context instanceof MainActivity)
                return (MainActivity) context;
        return null;
    }

    private void initUI(View rootView) {
        if (getActivity() instanceof MainActivity) {
            mainActivity = (MainActivity) getActivity();
        }
        if (mPager == null)
            mPager = (ViewPager) rootView.findViewById(R.id.pager);

        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int lastPosition = -1;

            @Override
            public void onPageSelected(int position) {
                MainActivity activity = null;

                if (context instanceof MainActivity) {
                    activity = (MainActivity) context;
                }
                DTOProfile profile = null;
                if (currentProfileIndex != -1) {
                    activity.setActiveProfile(profiles.get(currentProfileIndex));
                }
                if (activity != null)
                    profile = activity.getActiveProfile();

                if (currentProfileIndex != -1 && currentProfileIndex < position) { //Slide Next Page
                    if (profile != null) {
                        DTOProfile nextProfile = profiles.get(position);

                        DTOProfile temp = profile;
                        activity.setActiveProfile(nextProfile);
                        profile = activity.getActiveProfile();
                        if (profile != null) {
                            Fragment frag = fragmentMap.get(profile.Id);
                            refreshProfileFragment(frag);
                            int index = profiles.indexOf(nextProfile);
                            mPager.setCurrentItem(index);
                        } else {
                            activity.setActiveProfile(temp);
                            Fragment frag = fragmentMap.get(temp.Id);
                            refreshProfileFragment(frag);
                            int index = profiles.indexOf(temp);
                            mPager.setCurrentItem(index);
                        }
                    } else {
                        mPager.setCurrentItem(currentProfileIndex);
                    }
                } else if (currentProfileIndex != -1 && currentProfileIndex > position) {

                    if (profile != null) {
                        DTOProfile previousProfile = profiles.get(position);
                        activity.setActiveProfile(previousProfile);
                        Fragment frag = fragmentMap.get(profile.Id);
                        refreshProfileFragment(frag);
                        int index = profiles.indexOf(previousProfile);
                        mPager.setCurrentItem(index);
                    } else {
                        mPager.setCurrentItem(currentProfileIndex);
                    }
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                DTOProfile profile = profiles.get(position);
                Fragment frag = fragmentMap.get(profile.Id);
                refreshProfileFragment(frag);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    currentProfileIndex = mPager.getCurrentItem();
                    lastPosition = mPager.getCurrentItem();
                    DTOProfile profile = profiles.get(lastPosition);
                    Fragment frag = fragmentMap.get(profile.Id);
                    refreshProfileFragment(frag);
                } else if (state == ViewPager.SCROLL_STATE_IDLE && lastPosition != -1) {
                    lastPosition = mPager.getCurrentItem();
                    DTOProfile profile = profiles.get(lastPosition);
                    Fragment frag = fragmentMap.get(profile.Id);
                    refreshProfileFragment(frag);
                }
                lastPosition = -1;
            }
        });
        fillUI(profiles);
    }

    private void fillUI(ArrayList<DTOProfile> profileList) {
        int profileIndex = -1;
        if (getActivity() instanceof MainActivity) {
            MainActivity activity = (MainActivity) getActivity();
            DTOProfile dtoProfile = activity.getActiveProfile();
            if (dtoProfile != null) {
                int idx = profileList.indexOf(dtoProfile);
                profileIndex = idx;
                mPager.setCurrentItem(idx);
            }
        }
        if (mainActivity.getActiveProfile() == null)
            mainActivity.setActiveProfile(profileList.get(0));

        if (getActivity() instanceof FragmentActivity) {
            FragmentActivity baseFragmentActivity = (FragmentActivity) getActivity();

            FragmentManager fragmentManager = baseFragmentActivity.getSupportFragmentManager();
            if (mPagerAdapter == null) {
                mPagerAdapter = new ScreenSlidePagerAdapter(fragmentManager, profileList);
                mPager.setAdapter(mPagerAdapter);
            }
        }
        if (profileIndex != -1)
            mPager.setCurrentItem(profileIndex);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<DTOProfile> profileList;

        public ScreenSlidePagerAdapter(FragmentManager fm, ArrayList<DTOProfile> profileList) {
            super(fm);
            this.profileList = profileList;
        }

        @Override
        public Fragment getItem(int position) {
            activeFragment = getFragment(position);
            refreshProfileFragment(activeFragment);
            return activeFragment;
        }

        @Override
        public int getCount() {
            return profileList.size();
        }
    }

    public void refreshProfileFragment(Fragment fragment) {
        if (mPagerAdapter != null)
            mPagerAdapter.notifyDataSetChanged();
    }

    public Fragment getFragment(int position) {
        Fragment fragment = null;
        if (position < profiles.size()) {
            DTOProfile profile = profiles.get(position);
            fragment = getFragment(profile);
            fragmentMap.put(profile.Id, fragment);
        } else
            fragment = getFragment(profiles.get(position - 1));
        return fragment;
    }

    public Fragment getFragment(DTOProfile profile) {
        Fragment fragment = SwipeCardItemFragment.create(profile);
        fragmentMap.put(profile.Id, fragment);
        return fragment;
    }

    public HashMap<Long, Fragment> getFragmentMap() {
        return fragmentMap;
    }
    public void hideFragmentPager() {
        if (mPager != null)
            mPager.setVisibility(View.GONE);
    }

    public void showFragmentPager() {
        if (mPager != null)
            mPager.setVisibility(View.VISIBLE);
    }

    public void show() {
        if (rootView != null) {
            rootView.setVisibility(View.VISIBLE);
        }
    }

}