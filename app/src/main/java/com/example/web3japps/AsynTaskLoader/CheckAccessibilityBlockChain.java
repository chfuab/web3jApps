package com.example.web3japps.AsynTaskLoader;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.example.web3japps.ServiceContract;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;

public class CheckAccessibilityBlockChain extends AsyncTaskLoader<Boolean> {
    private Web3j web3j;
    private String PRIVATE_KEY;
    private Credentials credentials;
    private BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);
    private BigInteger GAS_PRICE = BigInteger.valueOf(20000000000L);
    private ServiceContract serviceContract;

    private String contractAddress;
    private String serviceId;
    public CheckAccessibilityBlockChain(@NonNull Context context, String contractAddress,
                                        String PRIVATE_KEY, String serviceId) {
        super(context);
        this.PRIVATE_KEY = PRIVATE_KEY;
        this.contractAddress = contractAddress;
        this.serviceId = serviceId;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Nullable
    @Override
    public Boolean loadInBackground() {
        Log.d("CHECKACCESS", "Loaded");
        web3j = Web3j.build(new HttpService("http://192.168.0.108:7545"));
        credentials = Credentials.create(PRIVATE_KEY);

        serviceContract = loadContract(web3j, credentials, contractAddress);
        boolean checkIfAccessInBlockChain = false;
        try {
            checkIfAccessInBlockChain = serviceContract.checkIfAccessInService(serviceId).send();
        } catch (Exception e) {
            Log.d("FailedToCheckAccess", e.toString());
        }
        return checkIfAccessInBlockChain;
    }
    public ServiceContract loadContract(Web3j web3j, Credentials credentials, String contractAddress){
        return ServiceContract.load(contractAddress, web3j, credentials, GAS_PRICE, GAS_LIMIT);
    }
}
