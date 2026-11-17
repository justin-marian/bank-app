package com.luxoft.bankapp.ai.service;

import com.luxoft.bankapp.ai.domain.Account;
import com.luxoft.bankapp.ai.domain.Bank;
import com.luxoft.bankapp.ai.domain.CheckingAccount;
import com.luxoft.bankapp.ai.domain.Client;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/** Builds the eight lab reports using collections and loops. */
public final class BankReport {
  static final Comparator<Client> BY_NAME =
      Comparator.comparing(Client::getName, Comparator.nullsFirst(Comparator.naturalOrder()));
  static final Comparator<Account> BY_BALANCE =
      Comparator.comparingDouble(Account::getBalance).thenComparingInt(Account::getId);

  private BankReport() {}

  public static int getNumberOfClients(Bank bank) {
    return bank.getClients().size();
  }

  public static int getNumberOfAccounts(Bank bank) {
    return accountsById(bank).size();
  }

  public static SortedSet<Client> getClientsSorted(Bank bank) {
    SortedSet<Client> sorted = new TreeSet<>(BY_NAME);
    sorted.addAll(bank.getClients());
    return sorted;
  }

  public static double getTotalSumInAccounts(Bank bank) {
    double total = 0.0;
    for (Account account : accountsById(bank).values()) {
      total += account.getBalance();
    }
    return total;
  }

  public static SortedSet<Account> getAccountsSortedBySum(Bank bank) {
    SortedSet<Account> sorted = new TreeSet<>(BY_BALANCE);
    sorted.addAll(accountsById(bank).values());
    return sorted;
  }

  public static double getBankCreditSum(Bank bank) {
    double used = 0.0;
    for (Account account : accountsById(bank).values()) {
      if (account instanceof CheckingAccount && account.getBalance() < 0.0) {
        used -= account.getBalance();
      }
    }
    return used;
  }

  public static Map<Client, Collection<Account>> getCustomerAccounts(Bank bank) {
    Map<Client, Collection<Account>> accounts = new LinkedHashMap<>();
    for (Client client : bank.getClients()) {
      accounts.put(client, new ArrayList<>(client.getAccounts()));
    }
    return accounts;
  }

  public static Map<String, List<Client>> getClientsByCity(Bank bank) {
    Map<String, List<Client>> cities = new TreeMap<>();
    for (Client client : bank.getClients()) {
      cities.computeIfAbsent(client.getCity(), city -> new ArrayList<>()).add(client);
    }
    return cities;
  }

  /** Keeps the first account for each ID in registration and account insertion order. */
  private static Map<Integer, Account> accountsById(Bank bank) {
    Map<Integer, Account> accounts = new LinkedHashMap<>();
    for (Client client : bank.getClients()) {
      for (Account account : client.getAccounts()) {
        accounts.putIfAbsent(account.getId(), account);
      }
    }
    return accounts;
  }
}
