package info.kgeorgiy.ja.zhunusov.bank;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RemoteBank implements Bank {
    private final int port;
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();
    private final Map<String, RemotePerson> people = new ConcurrentHashMap<>();

    public RemoteBank(int port) {
        this.port = port;
    }

    @Override
    public Account createAccount(String id) throws RemoteException {
        Account account = new RemoteAccount(id);
        if (accounts.putIfAbsent(id, account) == null) {
            UnicastRemoteObject.exportObject(account, port);
        }
        return accounts.get(id);
    }

    @Override
    public Account getAccount(String id) throws RemoteException {
        return accounts.get(id);
    }

    @Override
    public RemotePerson createPerson(String name, String surname, String passport) throws RemoteException {
        people.computeIfAbsent(passport, p -> {
            try {
                RemotePerson person = new RemotePerson(name, surname, passport);
                UnicastRemoteObject.exportObject(person, port);
                return person;
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
        return people.get(passport);
    }

    @Override
    public RemotePerson getRemotePerson(String passport) throws RemoteException {
        return people.get(passport);
    }

    @Override
    public LocalPerson getLocalPerson(String passport) throws RemoteException {
        RemotePerson remote = people.get(passport);
        if (remote == null) return null;
        return new LocalPerson(remote.getName(), remote.getSurname(), remote.getPassport(), remote.getAccounts());
    }
}