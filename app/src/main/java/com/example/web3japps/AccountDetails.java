package com.example.web3japps;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.web3japps.AsynTaskLoader.AccountBalance;
import com.example.web3japps.AsynTaskLoader.InvestAccount;
import com.google.android.gms.common.internal.AccountType;

import org.w3c.dom.Text;

import java.math.BigInteger;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountDetails#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountDetails extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView accountBalance;
    private EditText investAmount;
    private Button investAccount, checkAccountBalance;
    private String PRIVATE_KEY = "88b7efd0ea9484698c042af2d2bc6e560ffa14973f137c7cd9972baf8cfb0225";
    private String contractAddress = "0x6393e174e8ee21a90f977128dbeaef2e10c2bfdf";
    private static final int INVEST_ACCOUNT = 0;
    private static final int ACCOUNT_BALANCE = 1;
    private Long investAmountValue;

    public AccountDetails() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountDetails.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountDetails newInstance(String param1, String param2) {
        AccountDetails fragment = new AccountDetails();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_account_details, container, false);;
        accountBalance = root.findViewById(R.id.accountBalance);
        investAmount = root.findViewById(R.id.investAmount);
        investAccount = root.findViewById(R.id.investAccount);
        checkAccountBalance = root.findViewById(R.id.checkAccountBalance);
        investAccount.setOnClickListener(this);
        checkAccountBalance.setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.investAccount:
                investAmountValue = Long.parseLong(investAmount.getText().toString());
                getLoaderManager().restartLoader(INVEST_ACCOUNT, null, this);
                break;
            case R.id.checkAccountBalance:
                getLoaderManager().restartLoader(ACCOUNT_BALANCE, null, this);
                break;
        }
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        switch(id){
            case 0:
                return new InvestAccount(getContext(), contractAddress, PRIVATE_KEY, investAmountValue);
            case 1:
                return new AccountBalance(getContext(), contractAddress, PRIVATE_KEY);
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        switch(loader.getId()){
            case 0:
                BigInteger balanceOfAccount = (BigInteger) data;
                accountBalance.setText(balanceOfAccount.toString());
                break;
            case 1:
                BigInteger balanceOfAccount1 = (BigInteger) data;
                accountBalance.setText(balanceOfAccount1.toString());
                break;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }
}