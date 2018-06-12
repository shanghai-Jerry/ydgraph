package com.higgs.client.dgrpah;

public abstract class TxnException extends RuntimeException {
  TxnException(String message) {
    super(message);
  }
}