package info.kgeorgiy.ja.zhunusov.bank;

import org.junit.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.*;

import static org.junit.Assert.*;

public class BankTest {
    private static final int PORT = 8888;
    private static final String BANK_ID = "TestBank";
    private static Bank bank;

    @BeforeClass
    public static void init() throws Exception {
        Registry registry = LocateRegistry.createRegistry(PORT);
        bank = new RemoteBank(PORT);
        UnicastRemoteObject.exportObject(bank, PORT);
        registry.rebind(BANK_ID, bank);
    }

    @AfterClass
    public static void cleanup() throws Exception {
        LocateRegistry.getRegistry(PORT).unbind(BANK_ID);
    }

    @Test
    public void testRemoteAndLocalIndependence() throws RemoteException {
        String passport = "2000";
        bank.createPerson("Jane", "Smith", passport);
        RemotePerson remote = bank.getRemotePerson(passport);
        Account remoteAccount = bank.createAccount(passport + ":main");
        remoteAccount.setAmount(500);
        remote.addAccount("main", remoteAccount);

        LocalPerson local = bank.getLocalPerson(passport);
        assertEquals(500, local.getAccount("main").getAmount());

        remote.getAccount("main").setAmount(1500);
        assertEquals(1500, remote.getAccount("main").getAmount());
        assertEquals(500, local.getAccount("main").getAmount());
    }

    @Test
    public void testMultipleAccountsPerPerson() throws RemoteException {
        String passport = "3000";
        bank.createPerson("Alice", "Wonderland", passport);
        RemotePerson person = bank.getRemotePerson(passport);
        bank.createAccount(passport + ":savings");
        bank.createAccount(passport + ":travel");

        person.addAccount("savings", bank.getAccount(passport + ":savings"));
        person.addAccount("travel", bank.getAccount(passport + ":travel"));

        person.getAccount("savings").setAmount(100);
        person.getAccount("travel").setAmount(300);

        assertEquals(100, person.getAccount("savings").getAmount());
        assertEquals(300, person.getAccount("travel").getAmount());
    }

    @Test
    public void testAccountConsistency() throws RemoteException {
        String passport = "4000";
        bank.createPerson("Bob", "Builder", passport);
        RemotePerson r1 = bank.getRemotePerson(passport);
        bank.createAccount(passport + ":a1");
        r1.addAccount("a1", bank.getAccount(passport + ":a1"));
        r1.getAccount("a1").setAmount(700);

        RemotePerson r2 = bank.getRemotePerson(passport);
        assertEquals(700, r2.getAccount("a1").getAmount());
    }

    @Test
    public void testLocalPersonStaleData() throws RemoteException {
        String passport = "6000";
        bank.createPerson("Old", "Cache", passport);
        RemotePerson remote = bank.getRemotePerson(passport);
        Account acc = bank.createAccount(passport + ":cached");
        remote.addAccount("cached", acc);
        acc.setAmount(100);

        LocalPerson local = bank.getLocalPerson(passport);
        acc.setAmount(200);

        assertEquals(100, local.getAccount("cached").getAmount());
        assertEquals(200, remote.getAccount("cached").getAmount());
    }

    @Test
    public void testConcurrentAccountCreation() throws InterruptedException, RemoteException {
        String passport = "7000";
        bank.createPerson("Race", "Condition", passport);

        int threads = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    bank.createAccount(passport + ":same");
                } catch (RemoteException ignored) {}
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        Account acc = bank.getAccount(passport + ":same");
        assertNotNull(acc);
        assertEquals(0, acc.getAmount());
    }

    @Test(expected = RemoteException.class)
    public void testRemoteFailureHandling() throws RemoteException {
        Bank brokenBank = new Bank() {
            @Override
            public RemotePerson createPerson(String name, String surname, String passport) throws RemoteException {
                throw new RemoteException("Simulated failure");
            }

            @Override
            public RemotePerson getRemotePerson(String passport) throws RemoteException {
                throw new RemoteException("Simulated failure");
            }

            @Override
            public LocalPerson getLocalPerson(String passport) throws RemoteException {
                throw new RemoteException("Simulated failure");
            }

            @Override
            public Account createAccount(String id) throws RemoteException {
                throw new RemoteException("Simulated failure");
            }

            @Override
            public Account getAccount(String id) throws RemoteException {
                throw new RemoteException("Simulated failure");
            }
        };
        brokenBank.getRemotePerson("nonexistent");
    }

    @Test
    public void testLocalPersonIsolationBetweenClients() throws RemoteException {
        String passport = "8000";
        bank.createPerson("Isolated", "Person", passport);
        RemotePerson remote = bank.getRemotePerson(passport);
        remote.addAccount("a", bank.createAccount(passport + ":a"));
        remote.getAccount("a").setAmount(500);

        LocalPerson local1 = bank.getLocalPerson(passport);
        LocalPerson local2 = bank.getLocalPerson(passport);

        local1.getAccount("a").setAmount(0);
        local2.getAccount("a").setAmount(999);

        assertEquals(0, local1.getAccount("a").getAmount());
        assertEquals(999, local2.getAccount("a").getAmount());
        assertEquals(500, remote.getAccount("a").getAmount());
    }

    @Test
    public void testParallelAccountAdditionByDifferentPersons() throws RemoteException, InterruptedException {
        for (int i = 0; i < 10; i++) {
            String passport = "user" + i;
            bank.createPerson("Name" + i, "Surname" + i, passport);
        }

        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            final int idx = i;
            executor.submit(() -> {
                try {
                    String passport = "user" + idx;
                    bank.createAccount(passport + ":main");
                } catch (RemoteException ignored) {}
            });
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        for (int i = 0; i < 10; i++) {
            Account acc = bank.getAccount("user" + i + ":main");
            assertNotNull(acc);
        }
    }
}
