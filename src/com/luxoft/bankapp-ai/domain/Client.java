package com.luxoft.bankapp.ai.domain;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/** A bank customer whose immutable name determines equality. */
public class Client {

  private final String name;
  private final Gender gender;
  private String city;
  private final Set<Account> accounts = new LinkedHashSet<>();

  public Client(String name, Gender gender) {
    this(name, gender, "Unknown");
  }

  public Client(String name, Gender gender, String city) {
    this.name = name;
    this.gender = gender;
    this.city = normalizeCity(city);
  }

  /**
   * Adds an account while keeping the first account if an equal account is already present.
   *
   * @param account the account to add
   * @throws NullPointerException if the account is null
   */
  public void addAccount(final Account account) {
    accounts.add(Objects.requireNonNull(account, "account"));
  }

  public String getName() {
    return name;
  }

  public Gender getGender() {
    return gender;
  }

  public String getCity() {
    return city;
  }

  /**
   * Stores the trimmed city name, using {@code Unknown} for a null or blank value.
   *
   * @param city the city name to store
   */
  public void setCity(String city) {
    this.city = normalizeCity(city);
  }

  private static String normalizeCity(String city) {
    String trimmed = city == null ? "" : city.trim();
    return trimmed.isEmpty() ? "Unknown" : trimmed;
  }

  /**
   * @return an unmodifiable live view of accounts in insertion order
   */
  public Set<Account> getAccounts() {
    return Collections.unmodifiableSet(accounts);
  }

  public String getClientGreeting() {
    if (gender != null) {
      return gender.getGreeting() + " " + name;
    } else {
      return name;
    }
  }

  @Override
  public String toString() {
    return getClientGreeting();
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof Client)) {
      return false;
    }
    Client client = (Client) other;
    return Objects.equals(name, client.name);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(name);
  }
}
