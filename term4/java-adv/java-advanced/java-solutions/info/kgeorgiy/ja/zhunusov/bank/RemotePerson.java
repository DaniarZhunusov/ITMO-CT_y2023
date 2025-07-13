package info.kgeorgiy.ja.zhunusov.bank;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RemotePerson implements Person {
    private final String name;
    private final String surname;
    private final String passport;
    private final ConcurrentMap<String, Account> accounts = new ConcurrentHashMap<>();

    public RemotePerson(final String name, final String surname, final String passport) {
        this.name = name;
        this.surname = surname;
        this.passport = passport;
    }

    @Override
    public String getName() throws RemoteException {
        return name;
    }

    @Override
    public String getSurname() throws RemoteException {
        return surname;
    }

    @Override
    public String getPassport() throws RemoteException {
        return passport;
    }

    @Override
    public Account getAccount(String subId) throws RemoteException {
        return accounts.get(subId);
    }

    @Override
    public Map<String, Account> getAccounts() throws RemoteException {
        return accounts;
    }

    public void addAccount(String subId, Account account) {
        accounts.putIfAbsent(subId, account);
    }
}
