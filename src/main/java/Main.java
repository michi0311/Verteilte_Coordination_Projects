

/****************************
 * Created by Michael Marolt *
 *****************************/

public class Main implements Runnable{
    public static void main(String[] args) {
        new Main().run();
    }

    @Override
    public void run() {
        int clients = 2;
        String host = "127.0.0.1";
        new Thread(
                new Master(clients)
        ).start();


        for (int i = 1; i <= clients; i++) {
            new Thread(
                    new Slave(i,host)
            ).start();
        }

    }
}
