import DTO.Message;
import DTO.MessageType;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/****************************
 * Created by Michael Marolt *
 *****************************/

public class Master implements Runnable{
    private int port = 9120;
    private ServerSocket serverSocket;
    private boolean isStopped;
    private int maxUsers;
    private Map<Integer,Socket> clients;

    public Master(int maxUsers) {
        this.isStopped = false;
        clients = new HashMap<Integer,Socket>();
        this.maxUsers = maxUsers;
    }

    public static void main(String[] args) {
        Master x = new Master(1);
        x.run();
    }

    public void run() {
        try {
            this.serverSocket = new ServerSocket(this.port);
            System.out.println("Master: Master running on port " + this.port);
        } catch (IOException var4) {
            throw new RuntimeException("Master: Can't open port 9120", var4);
        }

        //Initialize connections
        while (maxUsers > clients.size()) {
            try {
                Socket clientSocket = serverSocket.accept();

                InputStream is = clientSocket.getInputStream();
                ObjectInputStream ois = new ObjectInputStream(is);
                Message m = (Message) ois.readObject();

                if (m.getType() == MessageType.Initialize) {
                    if (!clients.containsKey(m.getId())) {
                        clients.put(m.getId(), clientSocket);
                        System.out.println("Master: Client " + clientSocket + " successfully added");
                    } else {
                        System.out.println("Master: ID already taken: " + m.getId());
                    }
                } else {
                    System.out.println("Master: Wrong Message Type");
                }

                System.out.println(m);
            } catch (Exception e) {
                System.out.println("Master: Error in accepting Socket: " + e);
            }
        }

        System.out.println("Master: Finished waiting");


        for (int i: clients.keySet()) {
            try {
                Socket client = clients.get(i);

                OutputStream outputStream = client.getOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(outputStream);

                Message ex = new Message(MessageType.Exerscise, 10, 1);

                oos.writeObject(ex);
                oos.flush();
                oos.close();

            } catch (Exception e) {
                System.out.println("Master: Error in sending Exercise: " + e);
            }
        }


        while (!isStopped) {

        }
    }
}
