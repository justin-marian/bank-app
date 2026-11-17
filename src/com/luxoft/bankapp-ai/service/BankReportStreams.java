package com.luxoft.bankapp.ai.service;

import com.luxoft.bankapp.ai.domain.Account;
import com.luxoft.bankapp.ai.domain.Bank;
import com.luxoft.bankapp.ai.domain.CheckingAccount;
import com.luxoft.bankapp.ai.domain.Client;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Computes bank statistics from the current clients and accounts using Java 8 streams. */
public final class BankReportStreams {
  private BankReportStreams() {}

  public static int getNumberOfClients(Bank bank) {
    return Math.toIntExact(bank.getClients().stream().count());
  }

  public static int getNumberOfAccounts(Bank bank) {
    return indexAccounts(bank).size();
  }

  public static SortedSet<Client> getClientsSorted(Bank bank) {
    return bank.getClients().stream()
        .collect(Collectors.toCollection(() -> new TreeSet<>(BankReport.BY_NAME)));
  }

  public static double getTotalSumInAccounts(Bank bank) {
    // Match the collection report's floating-point addition in encounter order.
    return indexAccounts(bank).values().stream()
        .mapToDouble(Account::getBalance)
        .reduce(0.0, Double::sum);
  }

  public static SortedSet<Account> getAccountsSortedBySum(Bank bank) {
    return indexAccounts(bank).values().stream()
        .collect(Collectors.toCollection(() -> new TreeSet<>(BankReport.BY_BALANCE)));
  }

  public static double getBankCreditSum(Bank bank) {
    return indexAccounts(bank).values().stream()
        .filter(CheckingAccount.class::isInstance)
        .mapToDouble(Account::getBalance)
        .filter(balance -> balance < 0.0)
        .map(balance -> -balance)
        .reduce(0.0, Double::sum);
  }

  public static Map<Client, Collection<Account>> getCustomerAccounts(Bank bank) {
    return bank.getClients().stream()
        .collect(
            LinkedHashMap<Client, Collection<Account>>::new,
            (customers, client) -> customers.put(client, new ArrayList<>(client.getAccounts())),
            Map::putAll);
  }

  public static Map<String, List<Client>> getClientsByCity(Bank bank) {
    return bank.getClients().stream()
        .collect(Collectors.groupingBy(Client::getCity, TreeMap::new, Collectors.toList()));
  }

  private static LinkedHashMap<Integer, Account> indexAccounts(Bank bank) {
    return bank.getClients().stream()
        .flatMap(client -> client.getAccounts().stream())
        .collect(
            Collectors.toMap(
                Account::getId,
                Function.identity(),
                (existing, duplicate) -> existing,
                LinkedHashMap::new));
  }
}
