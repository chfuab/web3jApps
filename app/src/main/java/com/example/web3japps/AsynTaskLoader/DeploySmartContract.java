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

public class DeploySmartContract extends AsyncTaskLoader<String> {
    private Web3j web3j;
    private String PRIVATE_KEY;
    private Credentials credentials;
    private BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);
    private BigInteger GAS_PRICE = BigInteger.valueOf(20000000000L);
    public DeploySmartContract(@NonNull Context context, String PRIVATE_KEY) {
        super(context);
        this.PRIVATE_KEY = PRIVATE_KEY;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Nullable
    @Override
    public String loadInBackground() {
        web3j = Web3j.build(new HttpService("http://192.168.0.108:7545"));
        credentials = Credentials.create(PRIVATE_KEY);
        try {
            return deployContract(web3j, credentials);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public String deployContract(Web3j web3j, Credentials credentials) throws Exception {
        return ServiceContract.deploy(web3j, credentials, GAS_PRICE, GAS_LIMIT)
                .send()
                .getContractAddress();
    }
}
