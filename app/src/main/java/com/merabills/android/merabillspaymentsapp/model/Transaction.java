package com.merabills.android.merabillspaymentsapp.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Transaction {
    private String type;
    private double amount;
    private String provider;
    private String reference;

    public Transaction(String type, double amount, String provider, String reference) {
        this.type = type;
        this.amount = amount;
        this.provider = provider;
        this.reference = reference;
    }

    public String getType() { return type; }
    public double getAmount() { return amount; }
}
