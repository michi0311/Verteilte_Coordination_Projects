import DTO.DataType;
import DTO.Message;
import DTO.MessageType;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

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



            InputStream inputStream = s.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(inputStream);
            Message m = (Message) ois.readObject();

            byte[] resultOut = new byte[] {};
            if (m.getDataType() == DataType.StringTest) {
                resultOut = processTestString(m.getData());
            }

            Message res = new Message(MessageType.Result, m.getDataType(), resultOut.length, resultOut);

            outputStream = s.getOutputStream();
            oos = new ObjectOutputStream(outputStream);
            oos.writeObject(res);
            oos.flush();







        } catch (Exception e) {
            System.out.println("Slave: Error in connecting Client: " + e);
        }
    }


    private byte[] processTestString(byte[] dataIn) {
        String in = new String(dataIn, StandardCharsets.UTF_8);
        System.out.println("Slave: Data = " + in);
        in = in.toUpperCase(Locale.ROOT);
        return in.getBytes(StandardCharsets.UTF_8);
    }
}
