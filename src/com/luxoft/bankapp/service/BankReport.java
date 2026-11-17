package com.luxoft.bankapp.service;

import com.luxoft.bankapp.domain.Account;
import com.luxoft.bankapp.domain.Bank;
import com.luxoft.bankapp.domain.CheckingAccount;
import com.luxoft.bankapp.domain.Client;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Computes bank statistics with Java collections. Bank-wide account reports keep the first account
 * encountered in client registration and account insertion order when accounts compare equal.
 * Customer account lists retain each client's accounts.
 */
public class BankReport {
  static final Comparator<Client> BY_NAME =
      Comparator.comparing(Client::getName, Comparator.nullsFirst(Comparator.naturalOrder()));
  static final Comparator<Account> BY_BALANCE =
      Comparator.comparingDouble(Account::getBalance).thenComparingInt(Account::getId);

  public static int getNumberOfClients(Bank bank) {
    return bank.getClients().size();
  }

  public static int getNumberOfAccounts(Bank bank) {
    return allAccounts(bank).size();
  }

  public static SortedSet<Client> getClientsSorted(Bank bank) {
    SortedSet<Client> clients = new TreeSet<>(BY_NAME);
    clients.addAll(bank.getClients());
    return clients;
  }

  public static double getTotalSumInAccounts(Bank bank) {
    double total = 0;
    for (Account account : allAccounts(bank)) {
      total += account.getBalance();
    }
    return total;
  }

  /**
   * Sorts by ascending balance, then account ID. The result holds live account objects, so request
   * a new report after changing balances.
   *
   * @param bank the bank to report on
   * @return the accounts ordered by their balances at report time
   */
  public static SortedSet<Account> getAccountsSortedBySum(Bank bank) {
    SortedSet<Account> sortedAccounts = new TreeSet<>(BY_BALANCE);
    sortedAccounts.addAll(allAccounts(bank));
    return sortedAccounts;
  }

  /**
   * Totals the magnitude of negative checking-account balances.
   *
   * @param bank the bank to report on
   * @return the amount of overdraft already used
   */
  public static double getBankCreditSum(Bank bank) {
    double total = 0;
    for (Account account : allAccounts(bank)) {
      if (account instanceof CheckingAccount && account.getBalance() < 0) {
        total -= account.getBalance();
      }
    }
    return total;
  }

  /**
   * Lists clients in registration order, including clients with no accounts.
   *
   * @param bank the bank to report on
   * @return a new map with a separate list of live account objects for each client
   */
  public static Map<Client, Collection<Account>> getCustomerAccounts(Bank bank) {
    Map<Client, Collection<Account>> accounts = new LinkedHashMap<>();
    for (Client client : bank.getClients()) {
      accounts.put(client, new ArrayList<>(client.getAccounts()));
    }
    return accounts;
  }

  /**
   * Groups clients by city, including {@code Not Specified} for clients without a city.
   *
   * @param bank the bank to report on
   * @return a new map in alphabetical city order, with clients in registration order per city
   */
  public static Map<String, List<Client>> getClientsByCity(Bank bank) {
    Map<String, List<Client>> cities = new TreeMap<>();
    for (Client client : bank.getClients()) {
      cities.computeIfAbsent(client.getCity(), city -> new ArrayList<>()).add(client);
    }
    return cities;
  }

  private static Set<Account> allAccounts(Bank bank) {
    Set<Account> accounts = new LinkedHashSet<>();
    for (Client client : bank.getClients()) {
      accounts.addAll(client.getAccounts());
    }
    return accounts;
  }
}
