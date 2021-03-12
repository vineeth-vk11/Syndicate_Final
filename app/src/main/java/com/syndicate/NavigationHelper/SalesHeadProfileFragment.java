package com.syndicate.NavigationHelper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.syndicate.R;
import com.squareup.picasso.Picasso;


public class SalesHeadProfileFragment extends Fragment {

    ImageView profilePic;
    TextView name, email, number, address;

    String company, salesId, nameOfDealer;

    ImageButton caller;

    String phone;

    Button sendComplaint;

    ImageButton sort;
    ImageButton download;
    ImageButton share;
    ImageButton notifications;
    LottieAnimationView notificationsAnimation;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sales_head_profile, container, false);

        profilePic = view.findViewById(R.id.profilePic);
        name = view.findViewById(R.id.nameOfUser);
        email = view.findViewById(R.id.email);
        number = view.findViewById(R.id.phone);
        address = view.findViewById(R.id.address);
        caller = view.findViewById(R.id.callButton);
        sendComplaint = view.findViewById(R.id.sendComplaint);

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

        caller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+phone));
                startActivity(intent);
            }
        });

        Bundle bundle = getArguments();
        company = bundle.getString("company");
        salesId = bundle.getString("sales");
        nameOfDealer = bundle.getString("name");

        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();

        db.collection("Companies").document(company).collection("sales").document(salesId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                DocumentSnapshot documentSnapshot = task.getResult();

                if(documentSnapshot.getString("pic") != null){
                    Picasso.get().load(documentSnapshot.getString("pic")).into(profilePic);
                }
                else {
                    profilePic.setImageResource(R.drawable.ic_sales);
                }

                name.setText(documentSnapshot.getString("name"));
                email.setText(documentSnapshot.getString("email"));
                number.setText(documentSnapshot.getString("phoneNumber"));
                address.setText(documentSnapshot.getString("address"));

                phone = documentSnapshot.getString("phoneNumber");

            }
        });

        sendComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendComplaintDialog sendComplaintDialog = new SendComplaintDialog(company, salesId, nameOfDealer);
                sendComplaintDialog.show(getActivity().getSupportFragmentManager(), "Send Complaint");
            }
        });

        return view;
    }
}