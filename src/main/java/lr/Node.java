package lr;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.code.gossip.GossipMember;
import ie.ucd.murmur.MurmurHash;
import lr.Messages.Message;
import lr.Messages.MessageManage;
import lr.Messages.MessageStatus;
import lr.front_end.GossipResource;
import org.json.JSONException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

/**
 * Created by luca on 29/02/16.
 */
public class Node {
    public enum NODE_TYPE {FRONT, BACK}

    private NODE_TYPE type;
    protected String id;
    protected String ip;
    protected int portG;
    protected int portM;

    public Node() {
    }

    public Node(GossipMember m) {
        this.type = NODE_TYPE.BACK;
        this.id = m.getId();
        this.ip = m.getHost();
        this.portG = m.getPort();
        this.portM = m.getPort() + 1;
    }

//    public Node(JSONObject obj) {
//        this.id = obj.getString("id");
//        this.ip = obj.getString("ip");
//        this.portG = obj.getInt("port_g");
//        this.portM = obj.getInt("port_m");
//    }

    public Node(NODE_TYPE type, String id, String ip, int portG, int portM) {
        this.type = type;
        this.id = id;
        this.ip = ip;
        this.portG = portG;
        this.portM = portM;
    }

    public Node(NODE_TYPE type, String id, String ip, int port) {
        this(type, id, ip, port, port + 1);
    }

    public Node(String id, String ip, int port) {
        this(NODE_TYPE.BACK, id, ip, port);
    }
//
//    public JSONObject toJson(){
//        JSONObject json = new JSONObject();
//        json.add("id", id);
//        json.add("ip", ip);
//        json.add("port_g", portG);
//        json.add("port_m", portM);
//        return json;
//    }


    public NODE_TYPE getType() {
        return type;
    }

    public void setType(NODE_TYPE type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPortG() {
        return portG;
    }

    public void setPortG(int portG) {
        this.portG = portG;
    }

    public int getPortM() {
        return portM;
    }

    public void setPortM(int portM) {
        this.portM = portM;
    }

    @JsonIgnore
    public String getHostG() {
        return ip + ":" + portG;
    }

    @JsonIgnore
    public String getHostM() {
        return ip + ":" + portM;
    }

//    @JsonIgnore
//    public int getHash(){
//        return Helper.hash(toString());
//    }

    public boolean send(Message msg) {
        return send(ip, portM, msg);
    }

    protected boolean send(String ip, int port, Message msg) {
        System.out.println("SEND to " + id + "(" + ip + ":" + port + ") - " + msg);
        try {
            //Node n = _ch.get(data.getHash());
            //if (n != null) {
            InetAddress dest = InetAddress.getByName(ip);
            //JSONObject json = msg.toJson();

            //byte[] json_bytes = json.toString().getBytes();
            byte[] json_bytes = new ObjectMapper().registerModule(new Jdk8Module()).writeValueAsBytes(msg);
            int packet_length = json_bytes.length;
            //TODO check packet size

            // Convert the packet length to the byte representation of the int.
            byte[] length_bytes = new byte[4];
            length_bytes[0] = (byte) (packet_length >> 24);
            length_bytes[1] = (byte) ((packet_length << 8) >> 24);
            length_bytes[2] = (byte) ((packet_length << 16) >> 24);
            length_bytes[3] = (byte) ((packet_length << 24) >> 24);

            ByteBuffer byteBuffer = ByteBuffer.allocate(4 + json_bytes.length);
            byteBuffer.put(length_bytes);
            byteBuffer.put(json_bytes);
            byte[] buf = byteBuffer.array();

            DatagramSocket socket = new DatagramSocket();
            DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length, dest, port);
            socket.send(datagramPacket);
            socket.close();
            return true;

        } catch (JSONException | IOException e2) {
            e2.printStackTrace();
        }
        return false;
    }


    public void shutdown() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (portG != node.portG) return false;
        if (portM != node.portM) return false;
        if (type != node.type) return false;
        if (!id.equals(node.id)) return false;
        return ip.equals(node.ip);

    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + id.hashCode();
        result = 31 * result + ip.hashCode();
        result = 31 * result + portG;
        result = 31 * result + portM;
        return result;
    }

    @Override
    public String toString() {
        return "[" + id + " " + ip + " " + portG + " " + portM + "]";
    }
}
