package com.higgs.client.dgrpah;

public class TxnFinishedException extends TxnException {
  TxnFinishedException() {
    super("Transaction has already been committed or discarded");
  }
}
