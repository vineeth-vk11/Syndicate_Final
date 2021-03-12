package com.syndicate.SalesHelper;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.syndicate.R;


public class SalesValuesFragment extends Fragment {

    TextView collectionTarget, salesTarget;

    String company, sales;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_sales_values, container, false);

        Bundle bundle = getArguments();
        company = bundle.getString("company");
        sales = bundle.getString("sales");

        collectionTarget = view.findViewById(R.id.collectionTarget);
        salesTarget = view.findViewById(R.id.salesTarget);

        final FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();

        db.collection("Companies").document(company).collection("sales").document(sales)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                DocumentSnapshot documentSnapshot = task.getResult();

                if(documentSnapshot.getString("collectionTarget") != null){
                    collectionTarget.setText(documentSnapshot.getString("collectionTarget"));
                }
                else {
                    collectionTarget.setText("None");
                }

                if(documentSnapshot.getString("salesTarget") != null){
                    salesTarget.setText(documentSnapshot.getString("salesTarget"));
                }
                else {
                    salesTarget.setText("None");
                }


            }
        });

        return view;
    }
}