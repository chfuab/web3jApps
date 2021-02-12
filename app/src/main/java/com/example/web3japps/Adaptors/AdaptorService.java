package com.example.web3japps.Adaptors;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.web3japps.Interface.OnListItemSelectedListener;
import com.example.web3japps.Models.Service;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.example.web3japps.R;

public class AdaptorService extends FirestoreAdaptor<AdaptorService.ViewHolder>  {
    private OnListItemSelectedListener mListener;
    public AdaptorService(Query query, OnListItemSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @NonNull
    @Override
    public AdaptorService.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new AdaptorService.ViewHolder(inflater.inflate(R.layout.item_service, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptorService.ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameOfService, dateOfPost;
        ImageView imageService;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameOfService = itemView.findViewById(R.id.nameOfService);
            dateOfPost = itemView.findViewById(R.id.dateOfPost);
            imageService = itemView.findViewById(R.id.imageService);
        }

        public void bind(DocumentSnapshot snapshot, OnListItemSelectedListener mListener) {
            Service service = snapshot.toObject(Service.class);

            Glide.with(imageService.getContext())
                    .load(service.getPhoto())
                    .into(imageService);
            nameOfService.setText(service.getServiceTitle());
            dateOfPost.setText(service.getDateOfPost());
            itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onServiceSelected(snapshot);
                }
            });
        }
    }
}
