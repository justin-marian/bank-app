package com.luxoft.bankapp.ai.main;

import com.luxoft.bankapp.ai.domain.Account;
import com.luxoft.bankapp.ai.domain.Bank;
import com.luxoft.bankapp.ai.domain.CheckingAccount;
import com.luxoft.bankapp.ai.domain.Client;
import com.luxoft.bankapp.ai.domain.Gender;
import com.luxoft.bankapp.ai.domain.SavingAccount;
import com.luxoft.bankapp.ai.exceptions.ClientExistsException;
import com.luxoft.bankapp.ai.service.BankReport;
import java.io.PrintStream;
import java.util.Locale;
import java.util.Scanner;

/** Runs the Exercise 1 reports from an interactive console. */
public final class BankApplication {
  private BankApplication() {}

  public static void main(String[] args) throws ClientExistsException {
    run(args, new Scanner(System.in), System.out);
  }

  static void run(String[] args, Scanner input, PrintStream output) throws ClientExistsException {
    if (args.length != 1 || !"-statistics".equals(args[0])) {
      output.println("Usage: BankApplication -statistics");
      return;
    }
    runStatistics(sampleBank(), input, output);
  }

  private static Bank sampleBank() throws ClientExistsException {
    Bank bank = new Bank();
    Client ana = new Client("Ana", Gender.FEMALE, "Bucharest");
    ana.addAccount(new SavingAccount(1, 100));
    Client radu = new Client("Radu", Gender.MALE, "Cluj-Napoca");
    radu.addAccount(new CheckingAccount(2, -15, 40));
    radu.addAccount(new SavingAccount(3, 100));
    bank.addClient(ana);
    bank.addClient(radu);
    return bank;
  }

  static void runStatistics(Bank bank, Scanner input, PrintStream output) {
    output.println("Enter 'display statistic' to view the bank report or 'exit' to quit.");
    while (input.hasNextLine()) {
      switch (input.nextLine().trim().toLowerCase(Locale.ROOT)) {
        case "display statistic":
          printStatistics(bank, output);
          break;
        case "exit":
          return;
        case "":
          break;
        default:
          output.println("Enter 'display statistic' or 'exit'.");
      }
    }
  }

  private static void printStatistics(Bank bank, PrintStream output) {
    output.printf("Number of clients: %d%n", BankReport.getNumberOfClients(bank));
    output.printf("Number of accounts: %d%n", BankReport.getNumberOfAccounts(bank));
    output.println("Clients by name: " + BankReport.getClientsSorted(bank));
    output.printf(
        Locale.ROOT, "Total account balance: %.2f%n", BankReport.getTotalSumInAccounts(bank));
    output.println("Accounts by balance:");
    BankReport.getAccountsSortedBySum(bank).forEach(account -> printAccount(output, "  ", account));
    output.printf(Locale.ROOT, "Total credit used: %.2f%n", BankReport.getBankCreditSum(bank));
    output.println("Customer accounts:");
    BankReport.getCustomerAccounts(bank)
        .forEach(
            (client, accounts) -> {
              output.println("  " + client);
              accounts.forEach(account -> printAccount(output, "    ", account));
            });
    output.println("Clients by city:");
    BankReport.getClientsByCity(bank)
        .forEach((city, clients) -> output.println("  " + city + ": " + clients));
  }

  private static void printAccount(PrintStream output, String indent, Account account) {
    output.printf(
        Locale.ROOT, "%sAccount %d: %.2f%n", indent, account.getId(), account.getBalance());
  }
}
