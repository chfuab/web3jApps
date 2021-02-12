package com.example.web3japps.AsynTaskLoader;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.example.web3japps.ServiceContract;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;

public class IPFSAdd extends AsyncTaskLoader<String> {
    private ArrayList<String> fileNameList = new ArrayList<>();
    private ArrayList<String> cidFileNameList = new ArrayList<>();
    private HashMap<String, String> cidFileNameMap = new HashMap<>();
    private Web3j web3j;
    private String PRIVATE_KEY;
    private Credentials credentials;
    private String serviceId;
    private BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);
    private BigInteger GAS_PRICE = BigInteger.valueOf(20000000000L);
    private String price;
    private String key;
    private String contractAddress;
    private ServiceContract serviceContract;
    private CollectionReference serviceRef;
    private FirebaseFirestore mFirestore;
    public IPFSAdd(@NonNull Context context, ArrayList<String> fileNameList,
                   String key, String PRIVATE_KEY, String contractAddress, String serviceId, String price) {
        super(context);
        this.fileNameList = fileNameList;
        this.key = key;
        this.PRIVATE_KEY = PRIVATE_KEY;
        this.contractAddress = contractAddress;
        this.serviceId = serviceId;
        this.price = price;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Nullable
    @Override
    public String loadInBackground() {
        IPFS ipfs = new IPFS("/dnsaddr/ipfs.infura.io/tcp/5001/https");
        web3j = Web3j.build(new HttpService("http://192.168.0.108:7545"));
        credentials = Credentials.create(PRIVATE_KEY);
        mFirestore = FirebaseFirestore.getInstance();
        serviceRef = mFirestore.collection("Service");

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        for (String fileName : fileNameList){
            File file = new File(dir, fileName);
            File encryptedFile = new File(dir, "encrypted-" + fileName);

            encryptFileAES(file, encryptedFile, key, Cipher.ENCRYPT_MODE);
            NamedStreamable.FileWrapper temp = new NamedStreamable.FileWrapper(encryptedFile);
            try {
                MerkleNode addResult = ipfs.add(temp).get(0);
                String cidFileName = addResult.hash.toString();
                //cidFileNameList.add(cidFileName);
                cidFileNameMap.put(fileName, cidFileName);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        //update Firestore
        //serviceRef.document(serviceId).update("cidNameList", cidFileNameList);
        serviceRef.document(serviceId).update("cidNameMap", cidFileNameMap);
        //update to blockchain
        serviceContract = loadContract(web3j, credentials, contractAddress);
        try {
            TransactionReceipt transactionReceipt = serviceContract.createService(serviceId,
                    BigInteger.valueOf(Long.parseLong(price)), key).send();
        } catch (Exception e) {
            Log.d("FailedCreateService", e.toString());
        }
        return null;
    }

    private void encryptFileAES(File inputFile, File outputFile, String key, int cipherMode){
        Cipher cipher = null;
        try {
            Key secretKey = new SecretKeySpec(key.getBytes(), "AES");
            cipher = Cipher.getInstance("AES");
            cipher.init(cipherMode, secretKey);
            byte[] byteFile = Files.readAllBytes(inputFile.toPath());
            byte[] outputBytes = cipher.doFinal(byteFile);
            //assume create new output file:
            if (!outputFile.exists()){
                outputFile.createNewFile();
            }
            Log.d("outputExist", Boolean.toString(outputFile.exists()));
            FileOutputStream fos = new FileOutputStream(outputFile);
            fos.write(outputBytes);
            fos.close();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException invalidKeyException) {
            invalidKeyException.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }
    public ServiceContract loadContract(Web3j web3j, Credentials credentials, String contractAddress){
        return ServiceContract.load(contractAddress, web3j, credentials, GAS_PRICE, GAS_LIMIT);
    }
}
