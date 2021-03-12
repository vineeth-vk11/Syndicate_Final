package com.syndicate.NavigationHelper;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.syndicate.R;


public class CompanyProfileFragment extends Fragment {

    TextView fragment;

    private FirebaseAnalytics firebaseAnalytics;

    ImageButton sort;
    ImageButton download;
    ImageButton share;
    ImageButton notifications;
    LottieAnimationView notificationsAnimation;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        firebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        notificationsAnimation = getActivity().findViewById(R.id.animationView);
        notifications = getActivity().findViewById(R.id.notificationButton);
        sort = getActivity().findViewById(R.id.sort);
        download = getActivity().findViewById(R.id.download);
        share = getActivity().findViewById(R.id.share);

        notificationsAnimation.setVisibility(View.INVISIBLE);
        notifications.setVisibility(View.INVISIBLE);
        sort.setVisibility(View.INVISIBLE);
        download.setVisibility(View.INVISIBLE);
        share.setVisibility(View.INVISIBLE);

        fragment = getActivity().findViewById(R.id.nameOfUser);

        fragment.setText("Company Profile");

        return inflater.inflate(R.layout.fragment_company_profile, container, false);
    }
}