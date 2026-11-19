package com.luxoft.bankapp.service;

import com.luxoft.bankapp.domain.Account;
import com.luxoft.bankapp.domain.Bank;
import com.luxoft.bankapp.domain.CheckingAccount;
import com.luxoft.bankapp.domain.Client;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Computes the same statistics as {@link BankReport} using sequential streams. Bank-wide account
 * reports keep the first occurrence of equal accounts in encounter order.
 */
public class BankReportStreams {
  public static int getNumberOfClients(Bank bank) {
    return Math.toIntExact(bank.getClients().stream().count());
  }

  public static int getNumberOfAccounts(Bank bank) {
    return Math.toIntExact(accounts(bank).count());
  }

  public static SortedSet<Client> getClientsSorted(Bank bank) {
    return bank.getClients().stream()
        .collect(Collectors.toCollection(() -> new TreeSet<>(BankReport.BY_NAME)));
  }

  public static double getTotalSumInAccounts(Bank bank) {
    return accounts(bank).mapToDouble(Account::getBalance).reduce(0.0, Double::sum);
  }

  /**
   * @see BankReport#getAccountsSortedBySum(Bank)
   */
  public static SortedSet<Account> getAccountsSortedBySum(Bank bank) {
    return accounts(bank).collect(Collectors.toCollection(() -> new TreeSet<>(BankReport.BY_BALANCE)));
  }

  /**
   * @see BankReport#getBankCreditSum(Bank)
   */
  public static double getBankCreditSum(Bank bank) {
    return accounts(bank)
        .filter(CheckingAccount.class::isInstance)
        .mapToDouble(Account::getBalance)
        .filter(balance -> balance < 0)
        .map(balance -> -balance)
        .reduce(0.0, Double::sum);
  }

  /**
   * @see BankReport#getCustomerAccounts(Bank)
   */
  public static Map<Client, Collection<Account>> getCustomerAccounts(Bank bank) {
    return bank.getClients().stream()
        .collect(
            Collectors.toMap(
                client -> client,
                client -> new ArrayList<>(client.getAccounts()),
                (first, second) -> first,
                LinkedHashMap::new));
  }

  /**
   * @see BankReport#getClientsByCity(Bank)
   */
  public static Map<String, List<Client>> getClientsByCity(Bank bank) {
    return bank.getClients().stream()
        .collect(Collectors.groupingBy(Client::getCity, TreeMap::new, Collectors.toList()));
  }

  private static Stream<Account> accounts(Bank bank) {
    return bank.getClients().stream().flatMap(client -> client.getAccounts().stream()).distinct();
  }
}
