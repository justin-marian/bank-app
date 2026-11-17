package com.luxoft.bankapp.ai.domain;

import com.luxoft.bankapp.ai.exceptions.NotEnoughFundsException;

public interface Account {
  public void deposit(double amount);

  public void withdraw(double amount) throws NotEnoughFundsException;

  public int getId();

  public double getBalance();

  public double maximumAmountToWithdraw();
}
