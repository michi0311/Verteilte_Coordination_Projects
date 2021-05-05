import DTO.Message;
import DTO.MessageType;

import java.io.*;
import java.net.Socket;

/****************************
 * Created by Michael Marolt *
 *****************************/

public class Slave implements Runnable{
    private int ID;
    private String host;
    private int port = 9120;
    private boolean isStopped = false;

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
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Slave: Error in connecting Client: " + e);
        }

        try {
            InputStream inputStream = s.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(inputStream);
            Message m = (Message) ois.readObject();
            System.out.println(m);
        } catch (Exception e) {
            e.printStackTrace();
        }



        
    }
}
