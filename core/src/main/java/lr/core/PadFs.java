package lr.core;

import lr.core.CmdArgs.NodeArgs;
import lr.core.Nodes.StorageNode;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.net.UnknownHostException;

/**
 * Created by luca on 24/02/16.
 */
public class PadFs {
    public static void main(String[] args) {

        Logger.getRootLogger().setLevel(Level.OFF);
        Logger.getLogger("lr").setLevel(Level.INFO);

        NodeArgs s = new NodeArgs();
        Helper.parseArgs(s, args);

        StorageNode n = null;
        try {
            n = new StorageNode(s.getId(), s.getIp(), s.getPort(), s.getMembers())
                    .setNBackup(s.getReplica())
                    .start();
            System.out.print(n);
            while (true) ;
        } catch (InterruptedException | UnknownHostException ignored) {
        }

        if (n != null) n.shutdown();
    }
}

