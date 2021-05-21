

/****************************
 * Created by Michael Marolt *
 *****************************/

public class Main implements Runnable{
    static int clients = 2;
    static int failingClients = 0;
    public static void main(String[] args) {
        new Main().run();
    }

    @Override
    public void run() {
        String host = "127.0.0.1";
        new Thread(
                new Master(clients + failingClients)
        ).start();


        for (int i = 1; i <= clients; i++) {
            new Thread(
                    new Slave(i,host)
            ).start();
        }

        for (int i = clients + 1; i <= clients + failingClients; i++) {
            new Thread(
                    new Slave(i,host,true)
            ).start();
        }
    }
}
