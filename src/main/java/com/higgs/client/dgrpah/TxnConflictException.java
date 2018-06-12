package com.higgs.client.dgrpah;

public class TxnConflictException extends TxnException {
  public TxnConflictException(String msg) {
    super(msg);
  }
}
