package client.dgrpah;

import client.dgrpah.TxnException;

public class TxnFinishedException extends TxnException {
  TxnFinishedException() {
    super("Transaction has already been committed or discarded");
  }
}
