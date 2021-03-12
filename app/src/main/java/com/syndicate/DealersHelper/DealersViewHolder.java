package com.syndicate.DealersHelper;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.syndicate.R;

public class DealersViewHolder extends RecyclerView.ViewHolder {

    TextView name;
    ImageView image;
    CardView dealer;
    ImageButton info;
    CheckBox attendance;
    ImageView flag, greenFlag;

    public DealersViewHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.nameOfUser);
        image = itemView.findViewById(R.id.imageView2);
        dealer = itemView.findViewById(R.id.dealer);
        info = itemView.findViewById(R.id.info);
        attendance = itemView.findViewById(R.id.attendance);
        flag = itemView.findViewById(R.id.flag);
        greenFlag = itemView.findViewById(R.id.greenFlag);

    }
}
