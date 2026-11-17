package com.luxoft.bankapp.ai;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.luxoft.bankapp.ai.domain.Account;
import com.luxoft.bankapp.ai.domain.Bank;
import com.luxoft.bankapp.ai.domain.CheckingAccount;
import com.luxoft.bankapp.ai.domain.Client;
import com.luxoft.bankapp.ai.domain.Gender;
import com.luxoft.bankapp.ai.domain.SavingAccount;
import com.luxoft.bankapp.ai.exceptions.ClientExistsException;
import com.luxoft.bankapp.ai.service.BankReport;
import com.luxoft.bankapp.ai.service.BankReportStreams;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class TestBankReportAI {
  private static final double DELTA = 0.000001;

  @Test
  void computesAllEightReportsWithBothImplementations() throws ClientExistsException {
    Bank bank = new Bank();
    Client zoe = new Client("Zoe", Gender.FEMALE, "Iasi");
    Client ana = new Client("Ana", Gender.FEMALE, "Cluj-Napoca");
    Client radu = new Client("Radu", Gender.MALE);
    Account first = new SavingAccount(4, 100);
    Account tied = new SavingAccount(1, 100);
    Account credit = new CheckingAccount(3, -12.25, 50);
    Account duplicate = new CheckingAccount(4, -30, 50);
    zoe.addAccount(first);
    ana.addAccount(tied);
    ana.addAccount(credit);
    ana.addAccount(duplicate);
    bank.addClient(zoe);
    bank.addClient(ana);
    bank.addClient(radu);

    assertAll(
        () -> assertEquals(3, BankReport.getNumberOfClients(bank)),
        () -> assertEquals(3, BankReport.getNumberOfAccounts(bank)),
        () ->
            assertEquals(
                Arrays.asList(ana, radu, zoe), new ArrayList<>(BankReport.getClientsSorted(bank))),
        () -> assertEquals(187.75, BankReport.getTotalSumInAccounts(bank), DELTA),
        () ->
            assertEquals(
                Arrays.asList(credit, tied, first),
                new ArrayList<>(BankReport.getAccountsSortedBySum(bank))),
        () -> assertEquals(12.25, BankReport.getBankCreditSum(bank), DELTA),
        () -> assertEquals(Collections.emptyList(), BankReport.getCustomerAccounts(bank).get(radu)),
        () -> assertSame(first, BankReport.getAccountsSortedBySum(bank).last()),
        () ->
            assertSame(
                duplicate, new ArrayList<>(BankReport.getCustomerAccounts(bank).get(ana)).get(2)),
        () ->
            assertSame(
                duplicate,
                new ArrayList<>(BankReportStreams.getCustomerAccounts(bank).get(ana)).get(2)),
        () ->
            assertEquals(
                Arrays.asList("Cluj-Napoca", "Iasi", "Not Specified"),
                new ArrayList<>(BankReport.getClientsByCity(bank).keySet())),
        () ->
            assertEquals(
                Collections.singletonList(radu),
                BankReport.getClientsByCity(bank).get("Not Specified")),
        () -> assertSame(first, BankReportStreams.getAccountsSortedBySum(bank).last()));
    assertReportsAgree(bank);
  }

  @Test
  void keepsMissingCityClientsInRegistrationOrderAndCountsOnlyCheckingCredit()
      throws ClientExistsException {
    Bank bank = new Bank();
    Client zoe = new Client("Zoe", Gender.FEMALE, null);
    Client ana = new Client("Ana", Gender.FEMALE, "  ");
    Account negativeSavings = new SavingAccount(5, -20.0);
    Account negativeChecking = new CheckingAccount(3, -6.0, 30.0);
    Account positiveChecking = new CheckingAccount(1, 10.0, 30.0);
    zoe.addAccount(positiveChecking);
    zoe.addAccount(negativeSavings);
    ana.addAccount(negativeChecking);
    bank.addClient(zoe);
    bank.addClient(ana);

    assertReportsAgree(bank);
    assertAll(
        () -> assertEquals(-16.0, BankReportStreams.getTotalSumInAccounts(bank), DELTA),
        () -> assertEquals(6.0, BankReportStreams.getBankCreditSum(bank), DELTA),
        () ->
            assertEquals(
                Arrays.asList(negativeSavings, negativeChecking, positiveChecking),
                new ArrayList<>(BankReportStreams.getAccountsSortedBySum(bank))),
        () ->
            assertEquals(
                Arrays.asList(zoe, ana),
                new ArrayList<>(BankReportStreams.getCustomerAccounts(bank).keySet())),
        () ->
            assertEquals(
                Arrays.asList(positiveChecking, negativeSavings),
                new ArrayList<>(BankReportStreams.getCustomerAccounts(bank).get(zoe))),
        () ->
            assertEquals(
                Collections.singletonMap("Not Specified", Arrays.asList(zoe, ana)),
                BankReportStreams.getClientsByCity(bank)));
  }

  @Test
  void addsBalancesInAccountEncounterOrder() throws ClientExistsException {
    Bank bank = new Bank();
    Client first = new Client("Zoe", Gender.FEMALE);
    first.addAccount(new SavingAccount(15, 1e16));
    first.addAccount(new SavingAccount(1, -1e16));
    Client second = new Client("Ana", Gender.FEMALE);
    second.addAccount(new SavingAccount(8, 1.0));
    bank.addClient(first);
    bank.addClient(second);

    assertEquals(1.0, BankReport.getTotalSumInAccounts(bank));
    assertEquals(1.0, BankReportStreams.getTotalSumInAccounts(bank));
  }

  @Test
  void handlesAnEmptyBank() {
    Bank bank = new Bank();
    assertAll(
        () -> assertEquals(0, BankReport.getNumberOfClients(bank)),
        () -> assertEquals(0, BankReport.getNumberOfAccounts(bank)),
        () -> assertEquals(0.0, BankReport.getTotalSumInAccounts(bank), DELTA),
        () -> assertEquals(0.0, BankReport.getBankCreditSum(bank), DELTA),
        () -> assertTrue(BankReport.getClientsSorted(bank).isEmpty()),
        () -> assertTrue(BankReport.getAccountsSortedBySum(bank).isEmpty()),
        () -> assertTrue(BankReport.getCustomerAccounts(bank).isEmpty()),
        () -> assertTrue(BankReport.getClientsByCity(bank).isEmpty()));
    assertReportsAgree(bank);
  }

  private static void assertReportsAgree(Bank bank) {
    assertAll(
        () ->
            assertEquals(
                BankReport.getNumberOfClients(bank), BankReportStreams.getNumberOfClients(bank)),
        () ->
            assertEquals(
                BankReport.getNumberOfAccounts(bank), BankReportStreams.getNumberOfAccounts(bank)),
        () ->
            assertEquals(
                new ArrayList<>(BankReport.getClientsSorted(bank)),
                new ArrayList<>(BankReportStreams.getClientsSorted(bank))),
        () ->
            assertEquals(
                BankReport.getTotalSumInAccounts(bank),
                BankReportStreams.getTotalSumInAccounts(bank),
                DELTA),
        () ->
            assertEquals(
                new ArrayList<>(BankReport.getAccountsSortedBySum(bank)),
                new ArrayList<>(BankReportStreams.getAccountsSortedBySum(bank))),
        () ->
            assertEquals(
                BankReport.getBankCreditSum(bank), BankReportStreams.getBankCreditSum(bank), DELTA),
        () ->
            assertEquals(
                new ArrayList<>(BankReport.getCustomerAccounts(bank).entrySet()),
                new ArrayList<>(BankReportStreams.getCustomerAccounts(bank).entrySet())),
        () ->
            assertEquals(
                new ArrayList<>(BankReport.getClientsByCity(bank).entrySet()),
                new ArrayList<>(BankReportStreams.getClientsByCity(bank).entrySet())));
  }
}
