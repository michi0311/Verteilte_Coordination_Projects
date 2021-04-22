package DTO;

import java.io.Serializable;

/****************************
 * Created by Michael Marolt *
 *****************************/

public class Message implements Serializable {
    private MessageType type;
    private int id;
    private int datalength;
    private int data;

    public Message() {

    }

    public Message(int id, MessageType type) {
        this.id = id;
        this.type= type;
    }

    public Message(MessageType type, int datalength, int data) {
        this.type = type;
        this.datalength = datalength;
        this.data = data;
    }


    public MessageType getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public int getDatalength() {
        return datalength;
    }

    public int getData() {
        return data;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDatalength(int datalength) {
        this.datalength = datalength;
    }

    public void setData(int data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", id=" + id +
                ", datalength=" + datalength +
                ", data=" + data +
                '}';
    }
}
