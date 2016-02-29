package lr;

import com.google.code.gossip.GossipMember;

/**
 * Created by luca on 29/02/16.
 */
public class Node {
    protected String id;
    protected String ip;
    protected int portG;
    protected int portM;

    public Node(GossipMember m){
        this.id = m.getId();
        this.ip = m.getHost();
        this.portG = m.getPort();
        this.portM = m.getPort()+1;
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
        this.portM = port+1;
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

    @Override
    public String toString() {
        return "["+id+" "+ip+" "+portG+" "+portM+"]";
    }
}
