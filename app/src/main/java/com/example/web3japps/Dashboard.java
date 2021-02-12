package com.example.web3japps;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.web3japps.Adaptors.AdaptorService;
import com.example.web3japps.Interface.OnListItemSelectedListener;
import com.example.web3japps.Models.Service;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Dashboard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Dashboard extends Fragment implements OnListItemSelectedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FirebaseFirestore mFirestore;
    private CollectionReference serviceRef;
    private Query mQueryService;
    private RecyclerView serviceRecyclerView;
    private AdaptorService mAdaptorService;
    private static final int LIMIT = 20;
    private String PRIVATE_KEY = "88b7efd0ea9484698c042af2d2bc6e560ffa14973f137c7cd9972baf8cfb0225";
    private String key = "fuckfuckfuckfuck";
    private String contractAddress = "0x6393e174e8ee21a90f977128dbeaef2e10c2bfdf";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Dashboard() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Dashboard.
     */
    // TODO: Rename and change types and number of parameters
    public static Dashboard newInstance(String param1, String param2) {
        Dashboard fragment = new Dashboard();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private void initFirestore(){
        mFirestore = FirebaseFirestore.getInstance();
        serviceRef = mFirestore.collection("Service");
        mQueryService = serviceRef.orderBy("date", Query.Direction.DESCENDING).limit(LIMIT);
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
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        serviceRecyclerView = root.findViewById(R.id.serviceRecyclerView);
        mAdaptorService = new AdaptorService(mQueryService, this);
        mAdaptorService.startListening();
        serviceRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        serviceRecyclerView.setAdapter(mAdaptorService);
        return root;
    }

    @Override
    public void onServiceSelected(DocumentSnapshot service) {
        Service s = service.toObject(Service.class);
        Bundle tempBundle = new Bundle();
        tempBundle.putString("serviceTitle", s.getServiceTitle());
        tempBundle.putString("serviceIntro", s.getServiceIntro());
        tempBundle.putSerializable("cidNameMap", s.getCidNameMap());
        tempBundle.putString("price", s.getPrice());
        tempBundle.putString("serviceId", s.getServiceId());

        tempBundle.putString("PRIVATE_KEY", PRIVATE_KEY);
        tempBundle.putString("key", key);
        tempBundle.putString("contractAddress", contractAddress);
        Navigation.findNavController(serviceRecyclerView).navigate(R.id.serviceDetails, tempBundle);
    }

    @Override
    public void onDocumentSelected(String document) {

    }
}