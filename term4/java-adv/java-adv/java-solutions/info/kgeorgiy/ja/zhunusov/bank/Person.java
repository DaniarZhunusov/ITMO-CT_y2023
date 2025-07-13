package info.kgeorgiy.ja.zhunusov.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface Person extends Remote {
    String getName() throws RemoteException;
    String getSurname() throws RemoteException;
    String getPassport() throws RemoteException;
    Account getAccount(String subId) throws RemoteException;
    Map<String, Account> getAccounts() throws RemoteException;
}
