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

public class AccountBalance extends AsyncTaskLoader<BigInteger> {
    private Web3j web3j;
    private String PRIVATE_KEY;
    private Credentials credentials;
    private BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);
    private BigInteger GAS_PRICE = BigInteger.valueOf(20000000000L);
    private ServiceContract serviceContract;

    private String contractAddress;
    public AccountBalance(@NonNull Context context, String contractAddress,
                          String PRIVATE_KEY) {
        super(context);
        this.contractAddress = contractAddress;
        this.PRIVATE_KEY = PRIVATE_KEY;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Nullable
    @Override
    public BigInteger loadInBackground() {
        web3j = Web3j.build(new HttpService("http://192.168.0.108:7545"));
        credentials = Credentials.create(PRIVATE_KEY);
        serviceContract = loadContract(web3j, credentials, contractAddress);

        BigInteger balance = BigInteger.valueOf(0L);
        try {
            balance = serviceContract.balanceOfAccount().send();
        } catch (Exception e) {
            Log.d("FailedToGetBalance", e.toString());
        }
        return balance;
    }

    public ServiceContract loadContract(Web3j web3j, Credentials credentials, String contractAddress){
        return ServiceContract.load(contractAddress, web3j, credentials, GAS_PRICE, GAS_LIMIT);
    }
}
