package com.luxoft.bankapp.main;

import com.luxoft.bankapp.domain.Account;
import com.luxoft.bankapp.domain.Bank;
import com.luxoft.bankapp.domain.CheckingAccount;
import com.luxoft.bankapp.domain.Client;
import com.luxoft.bankapp.domain.Gender;
import com.luxoft.bankapp.domain.SavingAccount;
import com.luxoft.bankapp.exceptions.ClientExistsException;
import com.luxoft.bankapp.exceptions.NotEnoughFundsException;
import com.luxoft.bankapp.exceptions.OverdraftLimitExceededException;
import com.luxoft.bankapp.service.BankReport;
import com.luxoft.bankapp.service.BankService;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class BankApplication {
  private static Bank bank;

  public static void main(String[] args) {
    if (args.length == 1 && "-statistics".equals(args[0])) {
      runStatistics(createStatisticsBank());
      return;
    }
    bank = new Bank();
    modifyBank();
    printBalance();
    BankService.printMaximumAmountToWithdraw(bank);
  }

  private static Bank createStatisticsBank() {
    Bank sample = new Bank();
    Client john = new Client("John", Gender.MALE, "Bucharest");
    john.addAccount(new SavingAccount(1, 100));
    john.addAccount(new CheckingAccount(2, 40, 20));
    Client ana = new Client("Ana", Gender.FEMALE, "Cluj-Napoca");
    ana.addAccount(new CheckingAccount(3, -15, 100));
    Client maria = new Client("Maria", Gender.FEMALE, "Bucharest");
    maria.addAccount(new SavingAccount(4, 100));
    try {
      BankService.addClient(sample, john);
      BankService.addClient(sample, ana);
      BankService.addClient(sample, maria);
    } catch (ClientExistsException e) {
      throw new IllegalStateException("Duplicate client in the statistics sample", e);
    }
    return sample;
  }

  private static void runStatistics(Bank sample) {
    System.out.println("Enter 'display statistic' to view the bank report or 'exit' to quit.");
    Scanner console = new Scanner(System.in);
    while (console.hasNextLine()) {
      String command = console.nextLine().trim();
      if ("exit".equalsIgnoreCase(command)) {
        return;
      }
      if ("display statistic".equalsIgnoreCase(command)) {
        printStatistics(sample);
      } else if (!command.isEmpty()) {
        System.out.println("Enter 'display statistic' or 'exit'.");
      }
    }
  }

  private static void printStatistics(Bank bank) {
    System.out.printf("%nNumber of clients: %d%n", BankReport.getNumberOfClients(bank));
    System.out.printf("Number of accounts: %d%n", BankReport.getNumberOfAccounts(bank));
    System.out.println("Clients by name: " + BankReport.getClientsSorted(bank));
    System.out.printf("Total account balance: %.2f%n", BankReport.getTotalSumInAccounts(bank));
    System.out.printf("Total credit used: %.2f%n", BankReport.getBankCreditSum(bank));
    System.out.println("Accounts by balance:");
    for (Account account : BankReport.getAccountsSortedBySum(bank)) {
      System.out.printf("  Account %d: %.2f%n", account.getId(), account.getBalance());
    }
    System.out.println("Customer accounts:");
    for (Map.Entry<Client, Collection<Account>> entry :
        BankReport.getCustomerAccounts(bank).entrySet()) {
      System.out.println("  " + entry.getKey());
      for (Account account : entry.getValue()) {
        System.out.printf("    Account %d: %.2f%n", account.getId(), account.getBalance());
      }
    }
    System.out.println("Clients by city:");
    for (Map.Entry<String, List<Client>> entry : BankReport.getClientsByCity(bank).entrySet()) {
      System.out.printf("  %s: %s%n", entry.getKey(), entry.getValue());
    }
  }

  private static void modifyBank() {
    Client client1 = new Client("John", Gender.MALE);
    Account account1 = new SavingAccount(1, 100);
    Account account2 = new CheckingAccount(2, 100, 20);
    client1.addAccount(account1);
    client1.addAccount(account2);
    try {
      BankService.addClient(bank, client1);
    } catch (ClientExistsException e) {
      System.out.format("Cannot add an already existing client: %s%n", client1.getName());
    }
    account1.deposit(100);
    try {
      account1.withdraw(10);
    } catch (OverdraftLimitExceededException e) {
      System.out.format(
          "Not enough funds for account %d, balance: %.2f, overdraft: %.2f, tried to extract"
              + " amount: %.2f%n",
          e.getId(), e.getBalance(), e.getOverdraft(), e.getAmount());
    } catch (NotEnoughFundsException e) {
      System.out.format(
          "Not enough funds for account %d, balance: %.2f, tried to extract amount: %.2f%n",
          e.getId(), e.getBalance(), e.getAmount());
    }
    try {
      account2.withdraw(90);
    } catch (OverdraftLimitExceededException e) {
      System.out.format(
          "Not enough funds for account %d, balance: %.2f, overdraft: %.2f, tried to extract"
              + " amount: %.2f%n",
          e.getId(), e.getBalance(), e.getOverdraft(), e.getAmount());
    } catch (NotEnoughFundsException e) {
      System.out.format(
          "Not enough funds for account %d, balance: %.2f, tried to extract amount: %.2f%n",
          e.getId(), e.getBalance(), e.getAmount());
    }
    try {
      account2.withdraw(100);
    } catch (OverdraftLimitExceededException e) {
      System.out.format(
          "Not enough funds for account %d, balance: %.2f, overdraft: %.2f, tried to extract"
              + " amount: %.2f%n",
          e.getId(), e.getBalance(), e.getOverdraft(), e.getAmount());
    } catch (NotEnoughFundsException e) {
      System.out.format(
          "Not enough funds for account %d, balance: %.2f, tried to extract amount: %.2f%n",
          e.getId(), e.getBalance(), e.getAmount());
    }
    try {
      BankService.addClient(bank, client1);
    } catch (ClientExistsException e) {
      System.out.format("Cannot add an already existing client: %s%n", client1);
    }
  }

  private static void printBalance() {
    System.out.format("%nPrint balance for all clients%n");
    for (Client client : bank.getClients()) {
      System.out.println("Client: " + client);
      for (Account account : client.getAccounts()) {
        System.out.format("Account %d : %.2f%n", account.getId(), account.getBalance());
      }
    }
  }
}
