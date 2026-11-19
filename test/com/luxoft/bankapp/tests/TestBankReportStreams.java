package com.luxoft.bankapp.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.luxoft.bankapp.domain.Account;
import com.luxoft.bankapp.domain.Bank;
import com.luxoft.bankapp.domain.CheckingAccount;
import com.luxoft.bankapp.domain.Client;
import com.luxoft.bankapp.domain.Gender;
import com.luxoft.bankapp.domain.SavingAccount;
import com.luxoft.bankapp.exceptions.ClientExistsException;
import com.luxoft.bankapp.exceptions.NotEnoughFundsException;
import com.luxoft.bankapp.service.BankReport;
import com.luxoft.bankapp.service.BankReportStreams;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestBankReportStreams {
  private static final double DELTA = 0.000001;

  private Bank bank;
  private Client zoe;
  private Client alice;
  private Client bob;
  private Client noCity;
  private Account saving;
  private Account positiveChecking;
  private Account negativeChecking;
  private Account sameBalance;
  private Account negativeSaving;

  @BeforeEach
  void setUp() throws ClientExistsException {
    bank = new Bank();
    zoe = new Client("Zoe", Gender.FEMALE, "Timisoara");
    alice = new Client("Alice", Gender.FEMALE, "Bucharest");
    bob = new Client("Bob", Gender.MALE, "Timisoara");
    noCity = new Client("Daniel", Gender.MALE);

    saving = new SavingAccount(1, 75.50);
    positiveChecking = new CheckingAccount(2, 10, 30);
    negativeChecking = new CheckingAccount(3, -12.25, 40);
    sameBalance = new SavingAccount(4, 75.50);
    negativeSaving = new SavingAccount(5, -2);

    zoe.addAccount(saving);
    zoe.addAccount(positiveChecking);
    alice.addAccount(negativeChecking);
    bob.addAccount(sameBalance);
    bob.addAccount(negativeSaving);

    bank.addClient(zoe);
    bank.addClient(alice);
    bank.addClient(bob);
    bank.addClient(noCity);
  }

  @Test
  void countsClientsAndDistinctAccounts() {
    bob.addAccount(saving);

    assertEquals(4, BankReportStreams.getNumberOfClients(bank));
    assertEquals(5, BankReportStreams.getNumberOfAccounts(bank));
    assertEquals(146.75, BankReportStreams.getTotalSumInAccounts(bank), DELTA);
  }

  @Test
  void sortsClientsByName() {
    assertEquals(
        Arrays.asList(alice, bob, noCity, zoe),
        new ArrayList<>(BankReportStreams.getClientsSorted(bank)));
  }

  @Test
  void sortsAccountsByBalanceAndKeepsEqualBalances() {
    assertEquals(
        Arrays.asList(negativeChecking, negativeSaving, positiveChecking, saving, sameBalance),
        new ArrayList<>(BankReportStreams.getAccountsSortedBySum(bank)));
  }

  @Test
  void sumsOnlyOverdraftAlreadyUsedOnCheckingAccounts() {
    assertEquals(12.25, BankReportStreams.getBankCreditSum(bank), DELTA);
  }

  @Test
  void mapsEachClientToAnIndependentAccountListIncludingClientsWithoutAccounts() {
    Map<Client, Collection<Account>> accounts = BankReportStreams.getCustomerAccounts(bank);

    assertEquals(4, accounts.size());
    assertEquals(Arrays.asList(saving, positiveChecking), accounts.get(zoe));
    assertEquals(Collections.singletonList(negativeChecking), accounts.get(alice));
    assertEquals(Arrays.asList(sameBalance, negativeSaving), accounts.get(bob));
    assertEquals(Collections.emptyList(), accounts.get(noCity));

    accounts.get(zoe).clear();
    assertEquals(2, zoe.getAccounts().size());
  }

  @Test
  void groupsClientsByCityAndOrdersCityKeys() {
    Map<String, List<Client>> cities = BankReportStreams.getClientsByCity(bank);

    assertEquals(
        Arrays.asList("Bucharest", "Not Specified", "Timisoara"), new ArrayList<>(cities.keySet()));
    assertEquals(Collections.singletonList(alice), cities.get("Bucharest"));
    assertEquals(Arrays.asList(zoe, bob), cities.get("Timisoara"));
    assertEquals(Collections.singletonList(noCity), cities.get("Not Specified"));
  }

  @Test
  void handlesAnEmptyBank() {
    Bank empty = new Bank();

    assertEquals(0, BankReportStreams.getNumberOfClients(empty));
    assertEquals(0, BankReportStreams.getNumberOfAccounts(empty));
    assertEquals(0, BankReportStreams.getTotalSumInAccounts(empty), DELTA);
    assertEquals(0, BankReportStreams.getBankCreditSum(empty), DELTA);
    assertTrue(BankReportStreams.getClientsSorted(empty).isEmpty());
    assertTrue(BankReportStreams.getAccountsSortedBySum(empty).isEmpty());
    assertTrue(BankReportStreams.getCustomerAccounts(empty).isEmpty());
    assertTrue(BankReportStreams.getClientsByCity(empty).isEmpty());
  }

  @Test
  void agreesWithTheCollectionsImplementationAfterAnUpdate() throws NotEnoughFundsException {
    negativeChecking.withdraw(7.75);
    saving.deposit(20);
    alice.setCity("Timisoara");

    assertEquals(BankReport.getNumberOfClients(bank), BankReportStreams.getNumberOfClients(bank));
    assertEquals(BankReport.getNumberOfAccounts(bank), BankReportStreams.getNumberOfAccounts(bank));
    assertEquals(
        BankReport.getTotalSumInAccounts(bank),
        BankReportStreams.getTotalSumInAccounts(bank),
        DELTA);
    assertEquals(
        BankReport.getBankCreditSum(bank), BankReportStreams.getBankCreditSum(bank), DELTA);
    assertEquals(
        new ArrayList<>(BankReport.getClientsSorted(bank)),
        new ArrayList<>(BankReportStreams.getClientsSorted(bank)));
    assertEquals(
        new ArrayList<>(BankReport.getAccountsSortedBySum(bank)),
        new ArrayList<>(BankReportStreams.getAccountsSortedBySum(bank)));
    assertEquals(BankReport.getCustomerAccounts(bank), BankReportStreams.getCustomerAccounts(bank));
    assertEquals(BankReport.getClientsByCity(bank), BankReportStreams.getClientsByCity(bank));
  }

  @Test
  void keepsFirstAccountForSharedIdButPreservesEachCustomerAccount() {
    Account conflicting = new CheckingAccount(saving.getId(), -50, 80);
    alice.addAccount(conflicting);

    assertEquals(5, BankReportStreams.getNumberOfAccounts(bank));
    assertEquals(146.75, BankReportStreams.getTotalSumInAccounts(bank), DELTA);
    assertEquals(12.25, BankReportStreams.getBankCreditSum(bank), DELTA);
    assertSame(
        saving,
        BankReportStreams.getAccountsSortedBySum(bank).stream()
            .filter(account -> account.getId() == saving.getId())
            .findFirst()
            .get());
    assertSame(
        conflicting,
        new ArrayList<>(BankReportStreams.getCustomerAccounts(bank).get(alice)).get(1));
  }

  @Test
  void sortsCloseBalancesAndBreaksTiesByIdAcrossClients() {
    bob.addAccount(new SavingAccount(6, 75.49));
    bob.addAccount(new SavingAccount(0, 75.50));
    List<Integer> expected = Arrays.asList(3, 5, 2, 6, 0, 1, 4);

    assertEquals(
        expected,
        BankReportStreams.getAccountsSortedBySum(bank).stream()
            .map(Account::getId)
            .collect(Collectors.toList()));
    assertEquals(
        expected,
        BankReport.getAccountsSortedBySum(bank).stream()
            .map(Account::getId)
            .collect(Collectors.toList()));
  }

  @Test
  void updatesCreditAsCheckingAccountsCrossZero() throws NotEnoughFundsException {
    negativeChecking.deposit(12.25);
    assertEquals(0.0, BankReportStreams.getBankCreditSum(bank), DELTA);

    positiveChecking.withdraw(15);
    assertEquals(5.0, BankReportStreams.getBankCreditSum(bank), DELTA);

    positiveChecking.deposit(7);
    assertEquals(0.0, BankReportStreams.getBankCreditSum(bank), DELTA);
  }

  @Test
  void groupsMissingCitiesAndTrimsCityUpdates() {
    zoe.setCity(null);
    bob.setCity("  ");
    alice.setCity("  Iasi  ");
    Map<String, List<Client>> cities = BankReportStreams.getClientsByCity(bank);

    assertEquals(Arrays.asList("Iasi", "Not Specified"), new ArrayList<>(cities.keySet()));
    assertEquals(Collections.singletonList(alice), cities.get("Iasi"));
    assertEquals(Arrays.asList(zoe, bob, noCity), cities.get("Not Specified"));
    assertEquals(cities, BankReport.getClientsByCity(bank));
  }

  @Test
  void existingUnmodifiableViewsReflectLaterRegistrationAndAccountAddition()
      throws ClientExistsException {
    Set<Client> clients = bank.getClients();
    Set<Account> accounts = noCity.getAccounts();
    Client newClient = new Client("Eve", Gender.FEMALE);
    Account newAccount = new SavingAccount(6, 0);

    bank.addClient(newClient);
    noCity.addAccount(newAccount);

    assertSame(newClient, new ArrayList<>(clients).get(4));
    assertSame(newAccount, accounts.iterator().next());
    assertThrows(UnsupportedOperationException.class, () -> clients.remove(newClient));
    assertThrows(UnsupportedOperationException.class, () -> accounts.remove(newAccount));
  }

  @Test
  void usesStableSetIdentityAndRejectsExternalModification() {
    Client sameName = new Client("Zoe", Gender.MALE, "Iasi");
    Account sameId = new SavingAccount(1, 200);

    saving.deposit(1);
    zoe.setCity("Iasi");
    zoe.addAccount(sameId);

    assertThrows(ClientExistsException.class, () -> bank.addClient(sameName));
    assertEquals(4, bank.getPrintedClients());
    assertTrue(bank.getClients().contains(zoe));
    assertTrue(zoe.getAccounts().contains(sameId));
    assertEquals(Arrays.asList(saving, positiveChecking), new ArrayList<>(zoe.getAccounts()));
    assertThrows(UnsupportedOperationException.class, () -> bank.getClients().clear());
    assertThrows(UnsupportedOperationException.class, () -> zoe.getAccounts().clear());
  }
}
