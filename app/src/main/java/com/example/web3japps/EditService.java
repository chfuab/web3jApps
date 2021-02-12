package com.example.web3japps;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.web3japps.Adaptors.AddFileListAdaptor;
import com.example.web3japps.AsynTaskLoader.DeploySmartContract;
import com.example.web3japps.AsynTaskLoader.IPFSAdd;
import com.example.web3japps.Models.Service;
import com.example.web3japps.Utils.Utils;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditService#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditService extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FirebaseFirestore mFirestore;
    private StorageReference storageRef, imageStorageRef;
    private FirebaseStorage storage;
    private CollectionReference serviceRef;
    private String uploadLink;
    private String downloadUrl;
    private EditText editServiceTitle, editServiceIntro, editServiceBody, editPrice;
    private CardView addDocuments, addPhoto;
    private RecyclerView addDocumentsRecyclerView, addPhotoRecyclerView;
    private Button submitService, deployContract;
    private HashMap<String, Uri> fileNameAndURI = new HashMap<>();
    private ArrayList<String> fileNameList = new ArrayList<>();
    private HashMap<String, Uri> photoNameAndURI = new HashMap<>();
    private ArrayList<String> photoNameList = new ArrayList<>();
    private AddFileListAdaptor mAdaptorDocument, mAdaptorPhoto;
    private String contractAddress = "0x6393e174e8ee21a90f977128dbeaef2e10c2bfdf";
    private static final int PICK_PDF_FILE = 0;
    private static final int PICK_IMAGE_JPEG = 1;
    private static final int IPFSADD = 0;
    private static final int DEPLOYCONTRACT = 1;
    private static final int ASYNTEST = 2;
    private String PRIVATE_KEY = "88b7efd0ea9484698c042af2d2bc6e560ffa14973f137c7cd9972baf8cfb0225";
    private String key = "fuckfuckfuckfuck";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EditService() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditService.
     */
    // TODO: Rename and change types and number of parameters
    public static EditService newInstance(String param1, String param2) {
        EditService fragment = new EditService();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    private void initFirestore(){
        mFirestore = FirebaseFirestore.getInstance();
        serviceRef = mFirestore.collection("Service");
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        initFirestore();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_edit_service, container, false);
        editServiceTitle = root.findViewById(R.id.editServiceTitle);
        editServiceIntro = root.findViewById(R.id.editServiceIntro);
        //editServiceBody = root.findViewById(R.id.editServiceBody);
        editPrice = root.findViewById(R.id.editPrice);
        addDocuments = root.findViewById(R.id.addDocuments);
        addDocuments.setOnClickListener(this);
        addPhoto = root.findViewById(R.id.addPhoto);
        addPhoto.setOnClickListener(this);
        addDocumentsRecyclerView = root.findViewById(R.id.addDocumentsRecyclerView);
        addPhotoRecyclerView = root.findViewById(R.id.addPhotoRecyclerView);
        mAdaptorDocument = new AddFileListAdaptor(getContext(), fileNameList);
        mAdaptorPhoto = new AddFileListAdaptor(getContext(), photoNameList);
        addDocumentsRecyclerView.setAdapter(mAdaptorDocument);
        addPhotoRecyclerView.setAdapter(mAdaptorPhoto);
        addDocumentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        addPhotoRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        submitService = root.findViewById(R.id.submitService);
        submitService.setOnClickListener(this);
        deployContract = root.findViewById(R.id.deployContract);
        deployContract.setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.addDocuments:
                Intent intentDocument = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intentDocument.addCategory(Intent.CATEGORY_OPENABLE);
                intentDocument.setType("*/*");
                startActivityForResult(intentDocument, PICK_PDF_FILE);
                break;
            case R.id.addPhoto:
                Intent intentPhoto = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intentPhoto.addCategory(Intent.CATEGORY_OPENABLE);
                intentPhoto.setType("image/jpeg");
                startActivityForResult(intentPhoto, PICK_IMAGE_JPEG);
                break;
            case R.id.submitService:
                //getLoaderManager().restartLoader(IPFSADD, null, this);
                for (Uri uri : photoNameAndURI.values()){
                    String pathString = "coverImage/" + uri.getLastPathSegment();
                    imageStorageRef = storageRef.child(pathString);
                    imageStorageRef.putFile(uri).addOnSuccessListener(taskSnapshot -> {
                        uploadLink = taskSnapshot.getUploadSessionUri().toString();
                        Log.d("uploadLink", uploadLink);
                        storageRef.child(pathString).getDownloadUrl().addOnSuccessListener(uri1 -> {
                            downloadUrl = uri1.toString();
                            Log.d("downloadUrl", downloadUrl);
                            String serviceTitle = editServiceTitle.getText().toString();
                            String serviceIntro = editServiceIntro.getText().toString();
                            String servicePhoto = downloadUrl;
                            String price = editPrice.getText().toString();
                            Calendar calendar = Calendar.getInstance();
                            String dateOfPost = String.format("%04d/%02d/%02d %02d:%02d",
                                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
                            Service service = new Service(serviceTitle, serviceIntro, servicePhoto, dateOfPost, calendar.getTimeInMillis(), price);
                            serviceRef.add(service).addOnSuccessListener(documentReference -> {
                                String serviceId = documentReference.getId();
                                serviceRef.document(serviceId).update("serviceId", serviceId);
                                Bundle tempBundle = new Bundle();
                                tempBundle.putString("serviceId", serviceId);
                                tempBundle.putString("contractAddress", contractAddress);
                                tempBundle.putString("privateKey", PRIVATE_KEY);
                                tempBundle.putStringArrayList("fileNameList", fileNameList);
                                tempBundle.putString("key", key);
                                tempBundle.putString("price", price);
                                getLoaderManager().restartLoader(IPFSADD, tempBundle, this);
                            });
                        }).addOnFailureListener(e -> {
                            Log.d("FailedGettingUrl", e.toString());
                        });
                    }).addOnFailureListener(e -> {
                        Log.d("fuckFailedToUpload", e.toString());
                    });
                }
                break;
            case R.id.deployContract:
                getLoaderManager().restartLoader(DEPLOYCONTRACT, null, this);
                break;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == PICK_PDF_FILE
                && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.

            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                String parseFileName = Utils.queryName(getContext().getContentResolver(), uri);
                fileNameAndURI.put(parseFileName, uri);
                fileNameList.add(parseFileName);
                int listSize = fileNameList.size();
                addDocumentsRecyclerView.getAdapter().notifyItemInserted(listSize);
                addDocumentsRecyclerView.smoothScrollToPosition(listSize);
            }
        } else if (requestCode == PICK_IMAGE_JPEG
                && resultCode == Activity.RESULT_OK){
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                String parseFileName = Utils.queryName(getContext().getContentResolver(), uri);
                photoNameAndURI.put(parseFileName, uri);
                photoNameList.add(parseFileName);
                int listSize = photoNameList.size();
                addPhotoRecyclerView.getAdapter().notifyItemInserted(listSize);
                addPhotoRecyclerView.smoothScrollToPosition(listSize);
            }
        }
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        switch(id){
            case 0:
                return new IPFSAdd(getContext(), args.getStringArrayList("fileNameList"),
                        args.getString("key"), args.getString("privateKey"),
                        args.getString("contractAddress"), args.getString("serviceId"),
                        args.getString("price"));
            case 1:
                return new DeploySmartContract(getContext(), PRIVATE_KEY);
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        switch(loader.getId()){
            case 1:
                if (data != null){
                    contractAddress = (String) data;
                    Log.d("contractAddress", contractAddress);
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }
}