package com.syndicate.SalesHelper.AttendanceHelper;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.syndicate.R;

import java.io.IOException;
import java.util.Calendar;

public class SortAttendanceDialog extends AppCompatDialogFragment implements DatePickerDialog.OnDateSetListener{

    Button starDate;
    Button endDate;

    String type;


    public interface OnDatesSelected{
        void sendInput(String startDate, String endDate) throws IOException;
    }

    public OnDatesSelected onDatesSelected;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_sort_attendance, null);

        starDate = view.findViewById(R.id.startDate);
        endDate = view.findViewById(R.id.endDate);

        starDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
                type = "1";
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
                type = "2";
            }
        });

        builder.setView(view)
                .setTitle("Download Attendance")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Download", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String start = starDate.getText().toString();
                        String end = endDate.getText().toString();

                        if(start.equals("Select start date")){
                        }
                        else if(end.equals("Select end date")){
                        }
                        else {
                            try {
                                onDatesSelected.sendInput(start,end);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        return builder.create();
    }

    private void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONDAY),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        int correctedMonth = month+1;

        String selectedDate = dayOfMonth + "-" + correctedMonth + "-" + year;

        switch (type){
            case "1":
                starDate.setText(selectedDate);
                break;
            case "2":
                endDate.setText(selectedDate);
                break;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        onDatesSelected = (OnDatesSelected) getTargetFragment();

    }
}
