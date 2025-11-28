package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MedicineDexAdapter extends RecyclerView.Adapter<MedicineDexAdapter.ViewHolder> {

    // 클릭 콜백
    public interface OnItemClickListener {
        void onItemClick(MedicineDex item);
    }

    private final Context context;
    private List<MedicineDex> medicineList = new ArrayList<>();
    private OnItemClickListener listener;

    public MedicineDexAdapter(Context context, List<MedicineDex> medicineList) {
        this.context = context;
        if (medicineList != null) {
            this.medicineList = medicineList;
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<MedicineDex> list) {
        this.medicineList = (list != null) ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_medicine_dex, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MedicineDex item = medicineList.get(position);

        holder.tvName.setText(item.getMedicineName());
        holder.tvDates.setText("등록 날짜: " + item.getAddedDates());
        holder.ivThumb.setImageResource(R.drawable.ic_pill);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumb;
        TextView tvName, tvDates;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumb = itemView.findViewById(R.id.ivThumb);
            tvName = itemView.findViewById(R.id.tvName);
            tvDates = itemView.findViewById(R.id.tvDates);
        }
    }
}
