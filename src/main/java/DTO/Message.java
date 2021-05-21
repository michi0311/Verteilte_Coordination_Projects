package DTO;

import java.io.Serializable;
import java.util.Arrays;

/****************************
 * Created by Michael Marolt *
 *****************************/

public class Message implements Serializable {
    private MessageType type;
    private int id;
    private DataType dataType;
    private int datalength;
    private byte[] data;

    public Message() {

    }

    public Message(int id, MessageType type) {
        this.id = id;
        this.type= type;
    }

    public Message(MessageType type, DataType dataType, int datalength, byte[] data) {
        this.type = type;
        this.datalength = datalength;
        this.dataType = dataType;
        this.data = data;
    }


    public MessageType getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public DataType getDataType() {
        return dataType;
    }

    public int getDatalength() {
        return datalength;
    }

    public byte[] getData() {
        return data;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public void setDatalength(int datalength) {
        this.datalength = datalength;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", id=" + id +
                ", dataType=" + dataType +
                ", datalength=" + datalength +
                ", data=" + data +
                '}';
    }
}
