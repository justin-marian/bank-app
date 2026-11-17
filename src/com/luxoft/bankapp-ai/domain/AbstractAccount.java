package com.luxoft.bankapp.ai.domain;

import com.luxoft.bankapp.ai.exceptions.NotEnoughFundsException;

/** Base class for accounts identified by their immutable ID. */
public abstract class AbstractAccount implements Account {

  private final int id;
  protected double balance;

  public AbstractAccount(int id, double balance) {
    this.id = id;
    this.balance = balance;
  }

  @Override
  public void deposit(final double amount) {
    if (amount < 0) {
      throw new IllegalArgumentException("Cannot deposit a negative amount");
    }
    this.balance += amount;
  }

  @Override
  public void withdraw(final double amount) throws NotEnoughFundsException {
    if (amount < 0) {
      throw new IllegalArgumentException("Cannot withdraw a negative amount");
    }

    if (amount > maximumAmountToWithdraw()) {
      throw new NotEnoughFundsException(
          id, balance, amount, "Requested amount exceeds the maximum amount to withdraw");
    }

    this.balance -= amount;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public double getBalance() {
    return balance;
  }

  @Override
  public final boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof AbstractAccount)) {
      return false;
    }
    AbstractAccount account = (AbstractAccount) other;
    return id == account.id;
  }

  @Override
  public final int hashCode() {
    return Integer.hashCode(id);
  }
}
