package lr;

import com.google.code.gossip.GossipMember;
import com.sun.prism.shader.AlphaOne_ImagePattern_AlphaTest_Loader;

/**
 * Created by luca on 28/02/16.
 */
public class RemoteNode {
    private String id;
    private String ip;
    private int portG;
    private int portM;

    public RemoteNode(GossipMember m){
        this.id = m.getId();
        this.ip = m.getHost();
        this.portG = m.getPort();
        this.portM = m.getPort()+1;
    }

    public RemoteNode(String id, String ip, int portG, int portM) {
        this.id = id;
        this.ip = ip;
        this.portG = portG;
        this.portM = portM;
    }

    public RemoteNode(String id, String ip, int port) {
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
        return ip+id+portG+portM;
    }
}
