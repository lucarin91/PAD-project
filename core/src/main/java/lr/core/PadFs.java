package lr.core;

import lr.core.Nodes.StorageNode;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Created by luca on 24/02/16.
 */
public class PadFs {
    public static void main(String[] args) throws IOException {
        CmdArgs s = new CmdArgs();
        Helper.parseArgs(s,args);

        StorageNode n = null;
        try {
            n = new StorageNode(s.getId(), s.getIp(), s.getPort(), s.getMembers()).start();
            System.out.print(n);
            while (true) {
            }
        } catch (InterruptedException | UnknownHostException ignored) {
        }

        if (n != null) n.shutdown();
    }
}

