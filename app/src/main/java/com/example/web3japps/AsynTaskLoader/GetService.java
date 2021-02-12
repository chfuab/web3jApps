package com.example.web3japps.AsynTaskLoader;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.example.web3japps.ServiceContract;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import io.ipfs.api.IPFS;
import io.ipfs.multihash.Multihash;

public class GetService extends AsyncTaskLoader<String> {
    private Web3j web3j;
    private String PRIVATE_KEY;
    private Credentials credentials;
    private BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);
    private BigInteger GAS_PRICE = BigInteger.valueOf(20000000000L);
    private ServiceContract serviceContract;
    private String contractAddress;
    private String cid;
    private String serviceId;
    private String documentName;
    public GetService(@NonNull Context context, String contractAddress,
                      String PRIVATE_KEY, String serviceId, String documentName, String cid) {
        super(context);
        this.contractAddress = contractAddress;
        this.PRIVATE_KEY = PRIVATE_KEY;
        this.serviceId = serviceId;
        this.cid = cid;
        this.documentName = documentName;
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
        IPFS ipfs = new IPFS("/dnsaddr/ipfs.infura.io/tcp/5001/https");
        credentials = Credentials.create(PRIVATE_KEY);
        serviceContract = loadContract(web3j, credentials, contractAddress);
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        String password = "";
        try {
            password = serviceContract.getService(serviceId).send();
        } catch (Exception e) {
            Log.d("FailedGettingPassword", e.toString());
        }
        Multihash filePointer = Multihash.fromBase58(cid);
        try {
            byte[] fileContents = ipfs.cat(filePointer);
            File outputFile = new File(dir, "Decrypted-" + documentName);
            decryptByte(password, fileContents, outputFile);
        } catch (IOException ioException) {
            Log.d("FailedGettingFile", ioException.toString());
        }
        return null;
    }
    public void decryptByte(String key, byte[] bytes, File outputFile){
        Key secretKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] outputBytes = cipher.doFinal(bytes);
            if (!outputFile.exists()){
                outputFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(outputFile);
            fos.write(outputBytes);
            fos.close();
        } catch (NoSuchAlgorithmException e) {
            Log.d("NoSuchAlgorithmException", e.toString());
        } catch (NoSuchPaddingException e) {
            Log.d("NoSuchPaddingException", e.toString());
        } catch (InvalidKeyException invalidKeyException) {
            Log.d("InvalidKeyException", invalidKeyException.toString());
        } catch (BadPaddingException e) {
            Log.d("BadPaddingException", e.toString());
        } catch (IllegalBlockSizeException e) {
            Log.d("IllegalBlockSizeException", e.toString());
        } catch (FileNotFoundException fileNotFoundException) {
            Log.d("FileNotFoundException", fileNotFoundException.toString());
        } catch (IOException ioException) {
            Log.d("IOException", ioException.toString());
        }

    }

    public ServiceContract loadContract(Web3j web3j, Credentials credentials, String contractAddress){
        return ServiceContract.load(contractAddress, web3j, credentials, GAS_PRICE, GAS_LIMIT);
    }
}
