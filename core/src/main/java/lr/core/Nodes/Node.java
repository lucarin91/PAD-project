package lr.core.Nodes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import com.google.code.gossip.GossipMember;
//import lr.gossip.GossipMember;

import lr.core.Exception.SendException;
import lr.core.LoggerWrapper;
import lr.core.Messages.Message;
import lr.core.Messages.MessageRequest;
import lr.core.Messages.MessageStatus;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

/**
 * Created by luca on 29/02/16.
 */
public class Node {
    protected LoggerWrapper LOG;
    private String id;
    private String ip;
    private int portG;
    private int portM;

    public Node() {
    }

    public Node(GossipMember m) {
        this(m.getId(), m.getHost(), m.getPort());
    }

    public Node(String id, String ip, int port) {
        this(id, ip, port, port + 1);
    }

    public Node(String id, String ip, int portG, int portM) {
        this.LOG = new LoggerWrapper(Node.class, id + "@" + ip + "@" + portG + "@" + portM);
        this.id = id;
        this.ip = ip;
        this.portG = portG;
        this.portM = portM;
    }

    protected void logFilter (Message msg, String logMsg){
        if ((msg instanceof MessageRequest<?> && ((MessageRequest<?>) msg).getOperation() == Message.MSG_OPERATION.STATUS)
                || msg instanceof MessageStatus)
            LOG.d(logMsg);
        else
            LOG.i(logMsg);
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

    protected void send(Node to, Message msg) throws SendException {
        logFilter(msg, "send to " + to + " message " + msg);
        try {
            InetAddress dest = InetAddress.getByName(to.getIp());

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
            DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length, dest, to.getPortM());
            socket.send(datagramPacket);
            socket.close();
        } catch (IOException e) {
            throw new SendException(e.getMessage());
        }
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
        if (!id.equals(node.id)) return false;
        return ip.equals(node.ip);
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
