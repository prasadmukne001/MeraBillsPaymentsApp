package com.merabills.android.merabillspaymentsapp.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.merabills.android.merabillspaymentsapp.model.Transaction;
import com.merabills.android.merabillspaymentsapp.repository.TransactionRepository;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PaymentViewModel extends ViewModel {
    private final TransactionRepository transactionRepository;
    private final MutableLiveData<HashMap<String, Transaction>> transactions;

    @Inject
    public PaymentViewModel(TransactionRepository repository) {
        this.transactionRepository = repository;
        transactions = new MutableLiveData<>();
        transactions.setValue(repository.getAllPayments());
    }

    public LiveData<HashMap<String, Transaction>> getTransactions() {
        return transactions;
    }

    public void addTransaction(Transaction transaction) {
        HashMap<String, Transaction> currentPayments = transactions.getValue();
        if (currentPayments == null) {
            currentPayments = new HashMap<>();
        }
        currentPayments.put(transaction.getType(), transaction);
        transactions.setValue(currentPayments);
    }

    public void removeTransaction(String type) {
        HashMap<String, Transaction> currentPayments = transactions.getValue();
        if (currentPayments != null) {
            currentPayments.remove(type);
            transactions.setValue(currentPayments);
        }
    }

    public void saveTransaction() {
        transactionRepository.savePayments(transactions.getValue());
    }

    public List<String> getAvailablePaymentTypes(HashMap<String, Transaction> existingPayments){
        return transactionRepository.getAvailablePaymentTypes(existingPayments);
    }
}