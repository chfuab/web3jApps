package com.example.web3japps.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.web3japps.Interface.OnListItemSelectedListener;
import com.example.web3japps.R;

import java.util.ArrayList;

public class FileListAdaptor extends RecyclerView.Adapter<FileListAdaptor.WordViewHolder>  {
    private ArrayList<String> documentList;
    private final LayoutInflater mInflater;
    private OnListItemSelectedListener mListener;
    public FileListAdaptor(Context context, ArrayList<String> docList, OnListItemSelectedListener listener){
        mInflater = LayoutInflater.from(context);
        this.documentList = docList;
        this.mListener = listener;
    }
    @NonNull
    @Override
    public FileListAdaptor.WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.file_list, parent, false);
        return new FileListAdaptor.WordViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FileListAdaptor.WordViewHolder holder, int position) {
        //String docName = documentList.get(position);
        //holder.documentTitle.setText(docName);
        holder.bind(documentList.get(position), mListener);
    }

    @Override
    public int getItemCount() {
        return documentList.size();
    }

    public class WordViewHolder extends RecyclerView.ViewHolder {
        TextView documentTitle;
        CardView documentBody;
        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            documentBody = itemView.findViewById(R.id.documentBody);
            documentTitle = itemView.findViewById(R.id.documentTitle);
        }
        public void bind(String documentListAtPosition, OnListItemSelectedListener mListener){
            documentTitle.setText(documentListAtPosition);
            documentTitle.setOnClickListener(v -> {
                if (mListener != null){
                    mListener.onDocumentSelected(documentListAtPosition);
                }
            });
        }
    }
}
