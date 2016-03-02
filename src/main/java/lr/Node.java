package lr;

import com.google.code.gossip.GossipMember;
import org.json.JSONException;
import org.json.JSONObject;

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

    public Node(GossipMember m) {
        this.id = m.getId();
        this.ip = m.getHost();
        this.portG = m.getPort();
        this.portM = m.getPort() + 1;
    }

    public Node(JSONObject obj) {
        this.id = obj.getString("id");
        this.ip = obj.getString("ip");
        this.portG = obj.getInt("port_g");
        this.portM = obj.getInt("port_m");
    }

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
        this.portM = port + 1;
    }

    public JSONObject toJson(){
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("ip", ip);
        json.put("port_g", portG);
        json.put("port_m", portM);
        return json;
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

    public String getHostG() {
        return ip + ":" + portG;
    }

    public String getHostM() {
        return ip + ":" + portM;
    }


    public boolean send(Message msg) {
        return send(ip,portM,msg);
    }

    protected boolean send(String ip, int port, Message msg) {
        System.out.println("SEND to "+ id +"("+ip+":"+port+ ") - "+ msg);
        try {
            //Node n = _ch.get(data.getHash());
            //if (n != null) {
            InetAddress dest = InetAddress.getByName(ip);
            JSONObject json = msg.toJson();

            byte[] json_bytes = json.toString().getBytes();
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
    public String toString() {
        return "[" + id + " " + ip + " " + portG + " " + portM + "]";
    }
}
