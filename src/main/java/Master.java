import DTO.ArrayData;
import DTO.DataType;
import DTO.Message;
import DTO.MessageType;
import org.apache.commons.lang3.SerializationUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/*****************************
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
            //int[][] test = {{1,2,3,4},{5,6,7,8},{9,10,11,12},{13,14,15,16}};
            int[][] test = {{1,2,3,4,5,6,7},
                            {8,9,10,11,12,13,14},
                            {15,16,17,18,19,20,21},
                            {22,23,24,25,26,27,28},
                            {29,30,31,32,33,34,35}};
            int[][] test2 = {{10,20,30,40,50},
                            {11,21,31,41,51},
                            {12,22,32,42,52},
                            {13,23,33,43,53},
                            {14,24,34,44,54},
                            {15,25,35,45,55},
                            {16,26,36,46,56}};

            data = getMatrixData(test,test2);
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
        } else if (dataType == DataType.Matrix) {
            handleMatrixDataResults(clientMessages);
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
    private Map<Integer,byte[]> getMatrixData(int[][] array1, int[][]array2) {
        Map<Integer,byte[]> out = new HashMap<>();

        Map<Integer, ArrayData> clientChunks = new HashMap<>();
        Integer[] clientIds = clients.keySet().toArray(new Integer[clients.keySet().size()]);
        int chunkSize = array1.length / clientIds.length;
        int startIndex = 0;
        int endIndex = 0;
        for (int i = 0; i < clientIds.length; i++) {
            endIndex += chunkSize;
            if (i == clientIds.length - 1) {
                endIndex = array1.length;
            }

            int[][] array2out = getColumnsToArray(array2);

            clientChunks.put(clientIds[i], new ArrayData( new int[endIndex - startIndex][array1[1].length], array2out));
            System.arraycopy(array1,startIndex,clientChunks.get(clientIds[i]).getPartitionedArray(),0,endIndex - startIndex);
            startIndex = endIndex;
        }



        for (int i: clientIds) {
            out.put(i,SerializationUtils.serialize(clientChunks.get(i)));
        }


        return out;
    }


    private void handleTestDataResults(Map<Integer,byte[]> data) {
        for (int i: data.keySet()) {
            System.out.println("Master: Slave " + i + ": " + new String(data.get(i), StandardCharsets.UTF_8));
        }
    }

    private void handleMatrixDataResults(Map<Integer,byte[]> data) {
        LinkedList<int[]> results = new LinkedList<>();

        for (int i: data.keySet()) {
           ArrayData t = SerializationUtils.deserialize(data.get(i));
            System.out.println(Helper.twoDimensionalArrayToString(t.getSolutionsArray()));

        }
    }


    private int[][] getColumnsToArray(int[][] data) {
        int[][] col = new int[data[0].length][data.length];

        for (int i = 0; i < data.length ; i++) {
            for (int j = 0; j < data[i].length; j++) {
                col[j][i]= data[i][j];
            }
        }
        return col;
    }
}
