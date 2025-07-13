package info.kgeorgiy.ja.zhunusov.bank;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

public class LocalPerson implements Person, Serializable {
    private final String name;
    private final String surname;
    private final String passport;
    private final Map<String, Account> accounts;

    public LocalPerson(String name, String surname, String passport, Map<String, Account> sourceAccounts) throws RemoteException {
        this.name = name;
        this.surname = surname;
        this.passport = passport;
        this.accounts = new HashMap<>();

        for (Map.Entry<String, Account> entry : sourceAccounts.entrySet()) {
            String id = entry.getKey();
            Account sourceAccount = entry.getValue();
            Account localAccount = new LocalAccount(sourceAccount.getId(), sourceAccount.getAmount());
            this.accounts.put(id, localAccount);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSurname() {
        return surname;
    }

    @Override
    public String getPassport() {
        return passport;
    }

    @Override
    public Account getAccount(String subId) {
        return accounts.get(subId);
    }

    @Override
    public Map<String, Account> getAccounts() {
        return accounts;
    }
}