package info.kgeorgiy.ja.zhunusov.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Bank extends Remote {
    Account createAccount(String id) throws RemoteException;
    Account getAccount(String id) throws RemoteException;
    RemotePerson getRemotePerson(String passport) throws RemoteException;
    LocalPerson getLocalPerson(String passport) throws RemoteException;
    RemotePerson createPerson(String name, String surname, String passport) throws RemoteException;
}