package info.kgeorgiy.ja.zhunusov.bank;

import java.rmi.Naming;
import java.rmi.RemoteException;

public final class Client {
    public static void main(String... args) {
        if (args.length < 5) {
            System.out.println("Usage: <name> <surname> <passport> <subId> <amount>");
            return;
        }

        try {
            String name = args[0];
            String surname = args[1];
            String passport = args[2];
            String subId = args[3];
            int delta = Integer.parseInt(args[4]);

            Bank bank = (Bank) Naming.lookup("//localhost/bank");

            RemotePerson person = getOrCreatePerson(bank, name, surname, passport);
            Account account = getOrCreateAccount(bank, person, passport, subId);

            updateAccount(account, delta);

        } catch (Exception e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }

    private static RemotePerson getOrCreatePerson(Bank bank, String name, String surname, String passport) throws RemoteException {
        RemotePerson person = bank.getRemotePerson(passport);
        if (person == null) {
            System.out.println("Created new person");
            return bank.createPerson(name, surname, passport);
        }
        if (!person.getName().equals(name) || !person.getSurname().equals(surname)) {
            throw new IllegalArgumentException("Person data mismatch");
        }
        System.out.println("Person already exists");
        return person;
    }

    private static Account getOrCreateAccount(Bank bank, RemotePerson person, String passport, String subId) throws RemoteException {
        String accountId = passport + ":" + subId;
        Account account = bank.getAccount(accountId);
        if (account == null) {
            account = bank.createAccount(accountId);
            person.addAccount(subId, account);
            System.out.println("Created new account");
        }
        return account;
    }

    private static void updateAccount(Account account, int delta) throws RemoteException {
        int oldAmount = account.getAmount();
        account.setAmount(oldAmount + delta);
        System.out.printf("Updated account. Old: %d, Delta: %+d, New: %d%n", oldAmount, delta, account.getAmount());
    }
}
