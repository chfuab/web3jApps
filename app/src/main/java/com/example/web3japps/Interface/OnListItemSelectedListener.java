package com.example.web3japps.Interface;

import com.google.firebase.firestore.DocumentSnapshot;

public interface OnListItemSelectedListener {
    void onServiceSelected (DocumentSnapshot service);
    void onDocumentSelected (String document);
}
