import DTO.DataType;
import DTO.Message;
import DTO.MessageType;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/****************************
 * Created by Michael Marolt *
 *****************************/

public class Master implements Runnable{
    final private int port = 9120;
    private boolean isStopped;
    private int maxUsers;
    private Map<Integer,Socket> clients;
    private DataType dataType = DataType.Matrix;

    public Master(int maxUsers) {
        this.isStopped = false;
        clients = new HashMap<Integer,Socket>();
        this.maxUsers = maxUsers;
    }

    public static void main(String[] args) {
        Master x = new Master(0);
        x.run();
    }

    public void run() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(this.port);
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

                System.out.println("Master: " + m);
            } catch (Exception e) {
                System.out.println("Master: Error in accepting Socket: " + e);
            }
        }

        System.out.println("Master: Finished waiting");

        Map<Integer,byte[]> data = new HashMap<>();
        if (dataType == DataType.StringTest) {
             data = getTestData();
        } else if (dataType == DataType.Matrix) {
            data = getMatrixData();
        } else {
            System.out.println("No Data Type given");
            System.exit(2);
        }


        for (int i: clients.keySet()) {
            try {
                Socket client = clients.get(i);

                OutputStream outputStream = client.getOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(outputStream);

                byte[] bytesOut = data.get(i);
                int dataLength = bytesOut.length;
                Message ex = new Message(MessageType.Exerscise, dataType, dataLength, bytesOut);
                System.out.println("Master: " + ex);

                oos.writeObject(ex);
                oos.flush();

            } catch (Exception e) {
                System.out.println("Master: Error in sending Exercise: " + e);
            }
        }


        Map<Integer,byte[]> clientMessages = new HashMap<>();
        try {
            for (int i: clients.keySet()) {
                InputStream in = clients.get(i).getInputStream();
                ObjectInputStream ois = new ObjectInputStream(in);
                Message m = (Message) ois.readObject();
                clientMessages.put(i,m.getData());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Master: Error in getting Result");
        }

        if (dataType == DataType.StringTest) {
            handleTestDataResults(clientMessages);
        }
    }


    private Map<Integer,byte[]> getTestData() {
        Map<Integer,byte[]> out = new HashMap<>();

        for (int i: clients.keySet()) {
            clients.get(i);
            String m = "Du bist Slave " + i;
            byte[] bytesOut = m.getBytes(StandardCharsets.UTF_8);
            out.put(i,bytesOut);
        }
        return out;
    }

    /**
     * Distribute Matrix Data to Socket IDs
     *
     * @return
     */
    private Map<Integer,byte[]> getMatrixData() {
        Map<Integer,byte[]> out = new HashMap<>();

        int[][] test = {{1,2,3,4},{5,6,7,8},{9,10,11,12},{13,14,15,16}};

        Map<Integer,int[][]> clientChunks = new HashMap<>();
        Integer[] clientIds = clients.keySet().toArray(new Integer[clients.keySet().size()]);
        int chunkSize = test.length / clientIds.length;
        int startIndex = 0;
        int endIndex = 0;
        for (int i = 0; i < clientIds.length; i++) {
            endIndex += chunkSize;
            if (i == clientIds.length - 1) {
                endIndex = test.length;
            }
            clientChunks.put(clientIds[i],new int[endIndex - startIndex][test[1].length]);
            System.arraycopy(test,startIndex,clientChunks.get(clientIds[i]),0,endIndex - startIndex);
            startIndex = endIndex;
        }

        for (int i: clientIds) {
            System.out.println(i);
            System.out.println(twoDimensionalArrayToString( clientChunks.get(i) ));
            System.out.println();
        }



        return out;
    }


    private void handleTestDataResults(Map<Integer,byte[]> data) {
        for (int i: data.keySet()) {
            System.out.println("Master: Slave " + i + ": " + new String(data.get(i), StandardCharsets.UTF_8));
        }
    }

    private String twoDimensionalArrayToString(int[][] in) {
        StringBuffer result = new StringBuffer();
        String separator = ",";

        for (int i = 0; i < in.length; ++i) {
            result.append('[');
            for (int j = 0; j < in[i].length; ++j)
                if (j > 0)
                    result.append(separator).append(in[i][j]);
                else
                    result.append(in[i][j]);
            result.append(']');
            result.append("\n");
        }

        return result.toString();
    }
}
