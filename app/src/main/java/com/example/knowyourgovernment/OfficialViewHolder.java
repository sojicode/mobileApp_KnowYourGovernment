package com.example.knowyourgovernment;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class OfficialViewHolder extends RecyclerView.ViewHolder {

    TextView officeName;
    TextView officialAndParty;

    OfficialViewHolder(View view){
        super(view);
        officeName = view.findViewById(R.id.officeNamePText);
        officialAndParty = view.findViewById(R.id.officialAndPartyText);

    }
}
