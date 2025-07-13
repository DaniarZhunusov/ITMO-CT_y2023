package info.kgeorgiy.ja.zhunusov.bank;

import java.io.Serializable;

public class LocalAccount implements Account, Serializable {
    private final String id;
    private int amount;

    public LocalAccount(String id, int amount) {
        this.id = id;
        this.amount = amount;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
    }
}
