import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

/****************************
 * Created by Michael Marolt *
 *****************************/

public class Master implements Runnable{
    private int port = 9120;
    private ServerSocket serverSocket;
    private boolean isStopped;
    private int maxUsers;
    private LinkedList<Socket> clients;

    private Master(int maxUsers) {
        this.isStopped = false;
        clients = new LinkedList<Socket>();
        this.maxUsers = maxUsers;
    }

    public static void main(String[] args) {
        Master x = new Master(1);
        x.run();
    }

    public void run() {
        try {
            this.serverSocket = new ServerSocket(this.port);
            System.out.println("Master running on port " + this.port);
        } catch (IOException var4) {
            throw new RuntimeException("Can't open port 9120", var4);
        }


        while (maxUsers > clients.size()) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println(clientSocket);
                System.out.println(clients.size());
                clients.add(clientSocket);
            } catch (IOException e) {
                System.out.println("Error in accepting Socket: " + e);
            }
        }

        System.out.println("Finished waiting");

    }
}
