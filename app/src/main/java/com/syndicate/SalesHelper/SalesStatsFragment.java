package com.syndicate.SalesHelper;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.syndicate.R;

import java.util.ArrayList;
import java.util.Objects;


public class SalesStatsFragment extends Fragment {

    String[] flags = {"Flagged", "Not Flagged"};
    String[] health = {"Good","Ok","Bad"};

    FirebaseFirestore db;
    ArrayList<Integer> flagData = new ArrayList<>();;
    ArrayList<Integer> colors = new ArrayList<>();
    ArrayList<Integer> colors1 = new ArrayList<>();

    String company, sales;

    PieChart pieChart, healthChart;

    ImageView noDataFlag, noDataHealth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sales_stats, container, false);

        Bundle bundle = getArguments();
        company = bundle.getString("company");
        sales = bundle.getString("sales");

        colors.add(Color.RED);
        colors.add(Color.rgb(34,139,34));

        colors1.add(Color.rgb(34,139,34));
        colors1.add(Color.rgb(100, 149, 237));
        colors1.add(Color.RED);

        pieChart = view.findViewById(R.id.flagsChart);
        healthChart = view.findViewById(R.id.healthChart);
        noDataFlag = view.findViewById(R.id.noFlagImage);
        noDataHealth = view.findViewById(R.id.noHealthImage);

        db = FirebaseFirestore.getInstance();
        db.collection("Companies").document(company).collection("sales").document(sales).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();

                ArrayList<PieEntry> flagDataPie = new ArrayList<>();
                ArrayList<PieEntry> healthDataPie = new ArrayList<>();

                if(documentSnapshot.getString("flagged") != null && documentSnapshot.getString("notFlagged") != null){

                    if(Objects.equals(documentSnapshot.getString("flagged"), "0") && Objects.equals(documentSnapshot.getString("notFlagged"), "0")){
                        pieChart.setVisibility(View.INVISIBLE);
                        noDataFlag.setVisibility(View.VISIBLE);
                    }
                    else {
                        flagData.add(Integer.parseInt(documentSnapshot.getString("flagged")));
                        flagData.add(Integer.parseInt(documentSnapshot.getString("notFlagged")));

                        flagDataPie.add(new PieEntry(Integer.parseInt(documentSnapshot.getString("flagged")), "Flag"));
                        flagDataPie.add(new PieEntry(Integer.parseInt(documentSnapshot.getString("notFlagged")), "No Flag"));

                        PieDataSet pieDataSet = new PieDataSet(flagDataPie, "");
                        pieDataSet.setColors(colors);
                        pieDataSet.setValueTextColor(Color.WHITE);
                        pieDataSet.setValueTextSize(16f);

                        PieData pieData = new PieData(pieDataSet);

                        pieChart.setData(pieData);
                        pieChart.getDescription().setEnabled(false);
                        pieChart.setCenterText("Flags");
                        pieChart.animate();
                        pieChart.invalidate();
                    }
                }
                else {
                    pieChart.setVisibility(View.INVISIBLE);
                    noDataFlag.setVisibility(View.VISIBLE);
                }

                if(documentSnapshot.getString("GoodHealth") != null && documentSnapshot.getString("OkHealth") != null
                && documentSnapshot.getString("BadHealth") != null ){
                    if(documentSnapshot.getString("GoodHealth").equals("0") &&
                            documentSnapshot.getString("OkHealth").equals("0") && documentSnapshot.getString("BadHealth").equals("0")){

                        healthChart.setVisibility(View.INVISIBLE);
                        noDataHealth.setVisibility(View.VISIBLE);

                    }
                    else {

                        healthDataPie.add(new PieEntry(Integer.parseInt(documentSnapshot.getString("GoodHealth")), "Good"));
                        healthDataPie.add(new PieEntry(Integer.parseInt(documentSnapshot.getString("OkHealth")), "Ok"));
                        healthDataPie.add(new PieEntry(Integer.parseInt(documentSnapshot.getString("BadHealth")), "Bad"));

                        PieDataSet pieDataSet1 = new PieDataSet(healthDataPie,"" );
                        pieDataSet1.setColors(colors1);
                        pieDataSet1.setValueTextColor(Color.WHITE);
                        pieDataSet1.setValueTextSize(16f);

                        PieData pieData1 = new PieData(pieDataSet1);

                        healthChart.setData(pieData1);
                        healthChart.getDescription().setEnabled(false);
                        healthChart.setCenterText("Health");
                        healthChart.animate();
                        healthChart.invalidate();

                    }

                }
                else {
                    healthChart.setVisibility(View.INVISIBLE);
                    noDataHealth.setVisibility(View.VISIBLE);
                }
            }
        });

        return view;
    }
}