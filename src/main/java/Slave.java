import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/****************************
 * Created by Michael Marolt *
 *****************************/

public class Slave implements Runnable{
    private String host;
    private int port = 9120;

    public Slave(String host) {
        this.host = host;
    }

    public void run() {
        Socket s;
        try {
            s = new Socket(host,port);
            OutputStream outputStream = s.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeUTF("nice");
            dataOutputStream.flush();
            dataOutputStream.close();
            s.close();
        } catch (IOException e) {
            System.out.println("Error in connecting Client: " + e);
        }
    }
}
