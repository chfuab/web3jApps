package com.example.web3japps.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.web3japps.R;

import java.util.ArrayList;

public class AddFileListAdaptor extends RecyclerView.Adapter<AddFileListAdaptor.WordViewHolder>  {
    private ArrayList<String> documentList;
    private final LayoutInflater mInflater;
    public AddFileListAdaptor(Context context, ArrayList<String> docList){
        mInflater = LayoutInflater.from(context);
        this.documentList = docList;
    }
    @NonNull
    @Override
    public AddFileListAdaptor.WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.item_added_files, parent, false);
        return new WordViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull AddFileListAdaptor.WordViewHolder holder, int position) {
        String docName = documentList.get(position);
        holder.contentBodyPreviewDocumentTitle.setText(docName);
    }
    public void setDocumentList(ArrayList<String> documentList1){
        documentList = documentList1;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return documentList.size();
    }
    class WordViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{
        TextView contentBodyPreviewDocumentTitle;
        CardView contentBodyPreviewDocument;
        ImageView contentBodyPreviewDocumentDeleteIcon;
        AddFileListAdaptor mAdaptor;

        public WordViewHolder(@NonNull View itemView, AddFileListAdaptor adaptor) {
            super(itemView);
            contentBodyPreviewDocumentTitle = itemView.findViewById(R.id.contentBodyPreviewDocumentTitle);
            contentBodyPreviewDocument = itemView.findViewById(R.id.contentBodyPreviewDocument);
            contentBodyPreviewDocumentDeleteIcon = itemView.findViewById(R.id.contentBodyPreviewDocumentDeleteIcon);
            contentBodyPreviewDocumentDeleteIcon.setOnClickListener(this);
            contentBodyPreviewDocument.setOnClickListener(this);
            this.mAdaptor = adaptor;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.contentBodyPreviewDocumentDeleteIcon:
                    int mPosition = getLayoutPosition();
                    String element = documentList.get(mPosition);
                    //Delete teh document & the uri stored
                    documentList.remove(documentList.get(mPosition));
                    mAdaptor.notifyDataSetChanged();
                    break;
                case R.id.contentBodyPreviewDocument:

                    break;
            }
        }
    }
}