package com.luxoft.bankapp.ai.utils;

import com.luxoft.bankapp.ai.domain.Client;

public interface ClientRegistrationListener {
  void onClientAdded(Client client);
}
