package com.example.paymentapp.ui;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.merabills.android.merabillspaymentsapp.R;
import com.merabills.android.merabillspaymentsapp.model.Transaction;
import com.merabills.android.merabillspaymentsapp.ui.PaymentViewModel;
import com.merabills.android.merabillspaymentsapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class PaymentDialog extends DialogFragment {

    private final PaymentListener paymentListener;
    private Spinner paymentTypeSpinner;
    private EditText amountEditText, providerEditText, transactionEditText;
    private TextView providerLabelTextView, transactionLabelTextView;
    private Button okButton, cancelButton;
    private PaymentViewModel paymentViewModel;

    public PaymentDialog(PaymentListener listener) {
        this.paymentListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setupViewModel();

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_payment, null);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        initialiseUI(view);

        setupPaymentTypeSpinner();

        setOnClickListeners();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = (int) (3 * getResources().getDisplayMetrics().widthPixels) / 4;
            getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }

    private void setOnClickListeners() {
        cancelButton.setOnClickListener(v -> dismiss());

        okButton.setOnClickListener(v -> {
            try {
                String selectedPayment = paymentTypeSpinner.getSelectedItem().toString();
                String amountText = amountEditText.getText().toString().trim();
                String provider = providerEditText.getText().toString().trim();
                String transactionRef = transactionEditText.getText().toString().trim();
                if (amountText.isEmpty() || amountText.equals("0")) {
                    amountEditText.setError("Amount can not be 0 or empty.");
                    return;
                }

                double amount = Double.parseDouble(amountText);
                if (amount == 0) {
                    amountEditText.setError("Amount can not be 0.");
                    return;
                } else if (providerEditText.getVisibility() == View.VISIBLE && provider.isEmpty()) {
                    providerEditText.setError("Provider Bank is required");
                    return;
                } else if (transactionEditText.getVisibility() == View.VISIBLE && transactionRef.isEmpty()) {
                    transactionEditText.setError("Transaction reference number is required");
                    return;
                }

                Transaction transaction = new Transaction(selectedPayment, amount, provider, transactionRef);
                paymentListener.onPaymentAdded(transaction);
                Toast.makeText(getContext(), getResources().getString(R.string.payment_added_successfully), Toast.LENGTH_SHORT).show();
                dismiss();
            } catch (Exception e) {
                Toast.makeText(getContext(), getResources().getString(R.string.some_error), Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }

    private void initialiseUI(View view) {
        paymentTypeSpinner = view.findViewById(R.id.paymentTypeSpinner);
        amountEditText = view.findViewById(R.id.amountEditText);
        providerEditText = view.findViewById(R.id.providerEditText);
        transactionEditText = view.findViewById(R.id.transactionEditText);
        providerLabelTextView = view.findViewById(R.id.providerLabelTextView);
        transactionLabelTextView = view.findViewById(R.id.transactionLabelTextView);
        okButton = view.findViewById(R.id.addButton);
        cancelButton = view.findViewById(R.id.cancelButton);
    }

    private void setupViewModel() {
        paymentViewModel = new ViewModelProvider(requireActivity()).get(PaymentViewModel.class);
    }

    private void setupPaymentTypeSpinner() {
        List<String> availablePayments = new ArrayList<>();
        availablePayments.add(Constants.CASH);
        availablePayments.add(Constants.BANK_TRANSFER);
        availablePayments.add(Constants.CREDIT_CARD);

        paymentViewModel.getTransactions().observe(this, payments -> {
            for (Transaction t : payments.values()) {
                availablePayments.remove(t.getType());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, availablePayments);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            paymentTypeSpinner.setAdapter(adapter);
        });

        paymentTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                boolean showExtraFields = selected.equals(Constants.BANK_TRANSFER) || selected.equals(Constants.CREDIT_CARD);

                providerLabelTextView.setVisibility(showExtraFields ? View.VISIBLE : View.GONE);
                providerEditText.setVisibility(showExtraFields ? View.VISIBLE : View.GONE);
                transactionLabelTextView.setVisibility(showExtraFields ? View.VISIBLE : View.GONE);
                transactionEditText.setVisibility(showExtraFields ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public interface PaymentListener {
        void onPaymentAdded(Transaction transaction);
    }
}
