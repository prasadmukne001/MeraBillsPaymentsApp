package com.merabills.android.merabillspaymentsapp.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.paymentapp.ui.PaymentDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.merabills.android.merabillspaymentsapp.R;
import com.merabills.android.merabillspaymentsapp.model.Transaction;

import java.util.HashMap;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PaymentActivity extends AppCompatActivity implements com.example.paymentapp.ui.PaymentDialog.PaymentListener {

    private Button saveButton;
    private TextView addPaymentButton;
    private TextView totalAmountTextView;
    private ChipGroup chipGroupPayments;
    private PaymentViewModel paymentViewModel;

    private HashMap<String, Transaction> payments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        initialiseUI();

        setupViewModel();

        setupObservers();

        setOnClickListeners();
    }

    private void setOnClickListeners() {
        addPaymentButton.setOnClickListener(v -> {
            if (paymentViewModel.getAvailablePaymentTypes(payments).size() > 0) {
                openPaymentDialog();
            } else {
                Toast.makeText(PaymentActivity.this, getResources().getString(R.string.all_payment_types_are_utilised), Toast.LENGTH_SHORT).show();
            }
        });
        saveButton.setOnClickListener(v -> {
            paymentViewModel.saveTransaction();
            Toast.makeText(PaymentActivity.this, getResources().getString(R.string.payment_saved_successfully), Toast.LENGTH_SHORT).show();
        });
    }

    private void setupObservers() {
        paymentViewModel.getTransactions().observe(this, new Observer<HashMap<String, Transaction>>() {
            @Override
            public void onChanged(HashMap<String, Transaction> payments) {
                PaymentActivity.this.payments = payments;
                updateUI(payments);
            }
        });
    }

    private void setupViewModel() {
        paymentViewModel = new ViewModelProvider(this).get(PaymentViewModel.class);
    }

    private void initialiseUI() {
        addPaymentButton = findViewById(R.id.addPaymentTextView);
        saveButton = findViewById(R.id.btnSave);
        totalAmountTextView = findViewById(R.id.totalAmountTextView);
        chipGroupPayments = findViewById(R.id.paymentsHistoryChipGroup);
        addPaymentButton.setText(HtmlCompat.fromHtml("<u>Add payment</u>", HtmlCompat.FROM_HTML_MODE_LEGACY));
        addPaymentButton.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
    }

    private void openPaymentDialog() {
        PaymentDialog dialog = new PaymentDialog(this);
        dialog.show(getSupportFragmentManager(), "PaymentDialog");
    }

    @Override
    public void onPaymentAdded(Transaction transaction) {
        paymentViewModel.addTransaction(transaction);
    }

    private void updateUI(HashMap<String, Transaction> payments) {
        chipGroupPayments.removeAllViews();
        double totalAmount = 0;

        for (Transaction transaction : payments.values()) {
            totalAmount += transaction.getAmount();
            Chip chip = new Chip(this);
            chip.setText(transaction.getType() + " = ₹" + transaction.getAmount());
            chip.setCloseIconVisible(true);
            chip.setOnCloseIconClickListener(v -> paymentViewModel.removeTransaction(transaction.getType()));
            chipGroupPayments.addView(chip);
        }
        totalAmountTextView.setText(" ₹" + totalAmount);
    }
}
