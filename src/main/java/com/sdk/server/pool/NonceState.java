package com.sdk.server.pool;

public class NonceState {
    private String TranHash;
    private String TransactionHex;
    private long nonce;
    private long datetime;
    private int retey;

    public NonceState() {
    }

    public NonceState(String tranHash, String transactionHex, long nonce, long datetime, int retey) {
        TranHash = tranHash;
        TransactionHex = transactionHex;
        this.nonce = nonce;
        this.datetime = datetime;
        this.retey = retey;
    }

    public String getTranHash() {
        return TranHash;
    }

    public void setTranHash(String tranHash) {
        TranHash = tranHash;
    }

    public String getTransactionHex() {
        return TransactionHex;
    }

    public void setTransactionHex(String transactionHex) {
        TransactionHex = transactionHex;
    }

    public long getNonce() {
        return nonce;
    }

    public void setNonce(long nonce) {
        this.nonce = nonce;
    }

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }

    public int getRetey() {
        return retey;
    }

    public void setRetey(int retey) {
        this.retey = retey;
    }
}
