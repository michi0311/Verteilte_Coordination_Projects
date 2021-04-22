import DTO.Message;
import DTO.MessageType;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

/****************************
 * Created by Michael Marolt *
 *****************************/

public class Slave implements Runnable{
    private int ID;
    private String host;
    private int port = 9120;

    public Slave(int ID, String host) {
        this.ID = ID;
        this.host = host;
    }

    public void run() {
        Socket s;
        try {
            s = new Socket(host,port);
            OutputStream outputStream = s.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(outputStream);

            Message init = new Message(ID, MessageType.Initialize);

            oos.writeObject(init);
            oos.flush();
            oos.close();
            s.close();
        } catch (IOException e) {
            System.out.println("Error in connecting Client: " + e);
        }
    }
}
