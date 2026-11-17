package com.luxoft.bankapp.ai;

import static org.junit.Assert.assertEquals;

import com.luxoft.bankapp.ai.domain.Bank;
import com.luxoft.bankapp.ai.domain.CheckingAccount;
import com.luxoft.bankapp.ai.domain.Client;
import com.luxoft.bankapp.ai.domain.Gender;
import com.luxoft.bankapp.ai.domain.SavingAccount;
import com.luxoft.bankapp.ai.exceptions.ClientExistsException;
import com.luxoft.bankapp.ai.service.BankService;
import org.junit.Test;

public class Test2 {
  @Test
  public void testBank() throws ClientExistsException {
    Bank bank = new Bank();
    Client client1 = new Client("Smith John", Gender.MALE);
    client1.addAccount(new SavingAccount(1, 1000.0));
    client1.addAccount(new CheckingAccount(2, 1000.0, 100.0));
    Client client2 = new Client("Smith Michelle", Gender.FEMALE);
    client2.addAccount(new SavingAccount(3, 2000.0));
    client2.addAccount(new CheckingAccount(4, 1500.0, 200.0));
    BankService.addClient(bank, client1);
    BankService.addClient(bank, client2);
    assertEquals(2, bank.getClients().size());
    assertEquals(2, bank.getPrintedClients());
    assertEquals(2, bank.getEmailedClients());
    assertEquals(2, bank.getDebuggedClients());
  }
}
