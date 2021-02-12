package com.example.web3japps;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.TextView;

import com.example.web3japps.Adaptors.FileListAdaptor;
import com.example.web3japps.AsynTaskLoader.CheckAccessibilityBlockChain;
import com.example.web3japps.AsynTaskLoader.GetService;
import com.example.web3japps.AsynTaskLoader.PayService;
import com.example.web3japps.Interface.OnListItemSelectedListener;
import com.google.firebase.firestore.DocumentSnapshot;

import org.web3j.abi.datatypes.Bool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ServiceDetails#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServiceDetails extends Fragment implements OnListItemSelectedListener, LoaderManager.LoaderCallbacks, View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String serviceTitle, serviceIntro, price;
    private HashMap<String, String> cidNameMap = new HashMap<>();
    private TextView serviceTitleText, serviceIntroText, servicePriceText;
    private RecyclerView serviceDetailsRecyclerView;
    private FileListAdaptor fileListAdaptor;
    private String PRIVATE_KEY;
    private String key;
    private String contractAddress;
    private String serviceId;
    private static final int CHECKACCESS = 0;
    private static final int PAYSERVICE = 1;
    private static final int GET_SERVICE = 2;
    private boolean checkIfAccessInBlockChain = false;
    private Button buttonPayService;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ServiceDetails() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ServiceDetails.
     */
    // TODO: Rename and change types and number of parameters
    public static ServiceDetails newInstance(String param1, String param2) {
        ServiceDetails fragment = new ServiceDetails();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            serviceTitle = getArguments().getString("serviceTitle");
            serviceIntro = getArguments().getString("serviceIntro");
            cidNameMap = (HashMap<String, String>) getArguments().getSerializable("cidNameMap");
            price = getArguments().getString("price");

            PRIVATE_KEY = getArguments().getString("PRIVATE_KEY");
            key = getArguments().getString("key");
            contractAddress = getArguments().getString("contractAddress");
            serviceId = getArguments().getString("serviceId");
            getLoaderManager().restartLoader(CHECKACCESS, null, this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_service_details, container, false);
        serviceTitleText = root.findViewById(R.id.serviceTitleText);
        serviceTitleText.setText(serviceTitle);
        serviceIntroText = root.findViewById(R.id.serviceIntroText);
        serviceIntroText.setText(serviceIntro);
        servicePriceText = root.findViewById(R.id.servicePriceText);
        servicePriceText.setText(price);
        serviceDetailsRecyclerView = root.findViewById(R.id.serviceDetailsRecyclerView);
        Set<String> keySet = cidNameMap.keySet();
        ArrayList<String> fileNameList = new ArrayList<String>(keySet);
        fileListAdaptor = new FileListAdaptor(getContext(), fileNameList, this);
        serviceDetailsRecyclerView.setAdapter(fileListAdaptor);
        serviceDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        buttonPayService = root.findViewById(R.id.buttonPayService);
        buttonPayService.setOnClickListener(this);
        return root;
    }

    @Override
    public void onServiceSelected(DocumentSnapshot service) {

    }

    @Override
    public void onDocumentSelected(String document) {
        Bundle getServiceBundle = new Bundle();
        getServiceBundle.putString("cid", cidNameMap.get(document));
        getServiceBundle.putString("documentName", document);
        getLoaderManager().restartLoader(GET_SERVICE, getServiceBundle, this);
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        switch(id){
            case 0:
                return new CheckAccessibilityBlockChain(getContext(), contractAddress, PRIVATE_KEY, serviceId);
            case 1:
                return new PayService(getContext(), contractAddress, PRIVATE_KEY, serviceId);
            case 2:
                return new GetService(getContext(), contractAddress, PRIVATE_KEY,
                        serviceId, args.getString("documentName"), args.getString("cid"));
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        switch(loader.getId()){
            case 0:
                if (data != null){
                    checkIfAccessInBlockChain = (Boolean) data;
                    Log.d("checkIfAccessInBlockChain", Boolean.toString(checkIfAccessInBlockChain));
                    if (checkIfAccessInBlockChain){
                        buttonPayService.setVisibility(View.GONE);
                    }
                }
                break;
            case 1:
                if (data != null){
                    checkIfAccessInBlockChain = (Boolean) data;
                    Log.d("checkIfAccessInBlockChain", Boolean.toString(checkIfAccessInBlockChain));
                    if (checkIfAccessInBlockChain){
                        buttonPayService.setVisibility(View.GONE);
                    }
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.buttonPayService:
                getLoaderManager().restartLoader(PAYSERVICE, null, this);
                break;
        }
    }
}