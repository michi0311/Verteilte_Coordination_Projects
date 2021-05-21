import DTO.ArrayData;
import DTO.DataType;
import DTO.Message;
import DTO.MessageType;
import org.apache.commons.lang3.SerializationUtils;

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
    private boolean willFail;

    public Slave(int ID, String host) {
        this.ID = ID;
        this.host = host;
        this.willFail = false;
    }

    public Slave(int ID, String host, boolean willFail) {
        this.ID = ID;
        this.host = host;
        this.willFail = willFail;
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





            if (willFail) {
                return;
            }
            boolean finished = false;
            while (!finished) {
                InputStream inputStream = s.getInputStream();
                ObjectInputStream ois = new ObjectInputStream(inputStream);

                Message m = (Message) ois.readObject();

                if (m.getType() == MessageType.Finished) {
                    return;
                }

                byte[] resultOut = new byte[] {};
                if (m.getDataType() == DataType.StringTest) {
                    resultOut = processTestString(m.getData());
                } else if (m.getDataType() == DataType.Matrix) {
                    resultOut = processMatrix(m.getData());
                }

                Message res = new Message(MessageType.Result, m.getDataType(), resultOut.length, resultOut);
                if (m.getId() != 0) {
                    res.setId(m.getId());
                }
                System.out.println("Slave " + ID + " : " + res.toString());

                outputStream = s.getOutputStream();
                oos = new ObjectOutputStream(outputStream);
                oos.writeObject(res);
                oos.flush();

            }


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Slave " + ID +": Error in connecting Client: " + e);
        }
    }


    private byte[] processTestString(byte[] dataIn) {
        String in = new String(dataIn, StandardCharsets.UTF_8);
        System.out.println("Slave: Data = " + in);
        in = in.toUpperCase(Locale.ROOT);
        return in.getBytes(StandardCharsets.UTF_8);
    }

    private byte[] processMatrix(byte[] dataIn) {
        ArrayData arrayData = SerializationUtils.deserialize(dataIn);

        System.out.println("Slave " + ID + " : " + Helper.twoDimensionalArrayToString(arrayData.getPartitionedArray()));

        int[][] arrayOut = matrixMultiplication(arrayData.getPartitionedArray(), arrayData.getFullArray());

        ArrayData out = new ArrayData();
        out.setSolutionsArray(arrayOut);

        return SerializationUtils.serialize(out);
    }


    private int[][] matrixMultiplication(int[][] array1, int[][] array2) {
        int[][] arrayOut = new int[array1.length][array2.length];

        for (int i = 0; i < array1.length; i++) {
            for (int j = 0; j < array2.length; j++) {
                arrayOut[i][j] = 0;
                for (int k = 0; k < array1[i].length; k++) {
                    arrayOut[i][j] += array1[i][k] * array2[j][k];
                }
            }
        }
        return arrayOut;
    }
}
