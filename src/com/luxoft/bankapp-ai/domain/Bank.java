package com.luxoft.bankapp.ai.domain;

import com.luxoft.bankapp.ai.exceptions.ClientExistsException;
import com.luxoft.bankapp.ai.utils.ClientRegistrationListener;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Bank {

  private final Set<Client> clients = new LinkedHashSet<>();
  private final List<ClientRegistrationListener> listeners = new ArrayList<>();

  private int printedClients = 0;
  private int emailedClients = 0;
  private int debuggedClients = 0;

  public Bank() {
    listeners.add(new PrintClientListener());
    listeners.add(new EmailNotificationListener());
    listeners.add(new DebugListener());
  }

  public int getPrintedClients() {
    return printedClients;
  }

  public int getEmailedClients() {
    return emailedClients;
  }

  public int getDebuggedClients() {
    return debuggedClients;
  }

  /**
   * Registers a client and notifies the registration listeners.
   *
   * @param client the client to register
   * @throws ClientExistsException if a client with the same name is already registered
   * @throws NullPointerException if the client is null
   */
  public void addClient(final Client client) throws ClientExistsException {
    Objects.requireNonNull(client, "client");
    if (!clients.add(client)) {
      throw new ClientExistsException("Client already exists in the bank: " + client.getName());
    }
    notify(client);
  }

  private void notify(Client client) {
    for (ClientRegistrationListener listener : listeners) {
      listener.onClientAdded(client);
    }
  }

  /**
   * @return an unmodifiable live view of clients in registration order
   */
  public Set<Client> getClients() {
    return Collections.unmodifiableSet(clients);
  }

  class PrintClientListener implements ClientRegistrationListener {
    @Override
    public void onClientAdded(Client client) {
      System.out.println("Client added: " + client.getName());
      printedClients++;
    }
  }

  class EmailNotificationListener implements ClientRegistrationListener {
    @Override
    public void onClientAdded(Client client) {
      System.out.println("Notification email for client " + client.getName() + " to be sent");
      emailedClients++;
    }
  }

  class DebugListener implements ClientRegistrationListener {
    @Override
    public void onClientAdded(Client client) {
      System.out.println(
          "Client "
              + client.getName()
              + " added on: "
              + DateFormat.getDateInstance(DateFormat.FULL).format(new Date()));
      debuggedClients++;
    }
  }
}
