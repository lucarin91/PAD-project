package lr.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.code.gossip.GossipMember;
import lr.core.Messages.Message;
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
    protected String id;
    protected String ip;
    protected int portG;
    protected int portM;

    public Node() {
    }

    public Node(GossipMember m) {
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

    public Node(String id, String ip, int portG, int portM) {
        this.id = id;
        this.ip = ip;
        this.portG = portG;
        this.portM = portM;
    }

    public Node(String id, String ip, int port) {
        this.id = id;
        this.ip = ip;
        this.portG = port;
        this.portM = port+1;
    }



//    public JSONObject toJson(){
//        JSONObject json = new JSONObject();
//        json.add("id", id);
//        json.add("ip", ip);
//        json.add("port_g", portG);
//        json.add("port_m", portM);
//        return json;
//    }


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

        } catch (IOException e2) {
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
        if (id != null ? !id.equals(node.id) : node.id != null) return false;
        return ip != null ? ip.equals(node.ip) : node.ip == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (ip != null ? ip.hashCode() : 0);
        result = 31 * result + portG;
        result = 31 * result + portM;
        return result;
    }

    @Override
    public String toString() {
        return "[" + id + " " + ip + " " + portG + " " + portM + "]";
    }
}
