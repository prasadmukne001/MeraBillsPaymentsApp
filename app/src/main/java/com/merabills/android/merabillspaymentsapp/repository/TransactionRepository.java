package com.merabills.android.merabillspaymentsapp.repository;

import android.content.Context;

import com.google.gson.Gson;
import com.merabills.android.merabillspaymentsapp.model.Transaction;
import com.merabills.android.merabillspaymentsapp.utils.Constants;
import com.merabills.android.merabillspaymentsapp.utils.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TransactionRepository {
    private static final String FILE_NAME = "LastPayment.txt";
    private final Context context;

    @Inject
    public TransactionRepository(Context context) {
        this.context = context;
    }

    public HashMap<String, Transaction> getAllPayments() {
        HashMap<String, Transaction> payments = new HashMap<>();
        try {
            String content = Utility.readFromFile(context, FILE_NAME);
            if (content.isEmpty()) return payments;

            JSONArray jsonArray = new JSONArray(content);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                Transaction transaction = new Gson().fromJson(obj.toString(), Transaction.class);
                payments.put(transaction.getType(), transaction);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return payments;
    }

    public void savePayments(HashMap<String, Transaction> transactions) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (Transaction transaction : transactions.values()) {
                jsonArray.put(new JSONObject(new Gson().toJson(transaction)));
            }
            Utility.writeToFile(context, FILE_NAME, jsonArray.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getAvailablePaymentTypes(HashMap<String, Transaction> existingPayments) {
        List<String> allTypes = Arrays.asList(Constants.CASH, Constants.BANK_TRANSFER, Constants.CREDIT_CARD);
        List<String> availableTypes = new ArrayList<>();
        for (String type : allTypes) {
            if (!existingPayments.containsKey(type)) {
                availableTypes.add(type);
            }
        }
        return availableTypes;
    }
}
