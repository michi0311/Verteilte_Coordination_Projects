import java.util.concurrent.TimeUnit;

/****************************
 * Created by Michael Marolt *
 *****************************/

public class Main {
    public static void main(String[] args) {
        Slave slave1 = new Slave(1,"127.0.0.1");
        slave1.run();
    }
}
