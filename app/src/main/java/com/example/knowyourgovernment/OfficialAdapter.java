package com.example.knowyourgovernment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class OfficialAdapter extends RecyclerView.Adapter<OfficialViewHolder> {

    private static final String TAG = "OfficialAdapter";

    private List<Official> officialList;
    private MainActivity mainActivity;

    public OfficialAdapter(List<Official> officialList, MainActivity mainActivity){
        this.officialList = officialList;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public OfficialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.office_list_row, parent, false);

        view.setOnClickListener(mainActivity);
        view.setOnLongClickListener(mainActivity);

        return new OfficialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfficialViewHolder holder, int position) {

        Official offi = officialList.get(position);
        holder.officeName.setText(offi.getOfficeName());

        holder.officialAndParty.
                setText(String.format(Locale.getDefault(),  "%s (%s)",offi.getOfficialName(),
                        offi.getPartyName().split(" ")[0]));

    }

    @Override
    public int getItemCount() {
        return officialList.size();
    }
}
