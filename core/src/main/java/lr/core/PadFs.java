package lr.core;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Created by luca on 24/02/16.
 */
public class PadFs {
    public static void main(String[] args) throws IOException {
        CmdArgs s = new CmdArgs();
        Helper.parseArgs(s,args);

        NodeService n = null;
        try {
            n = new NodeService(s.getId(), s.getIp(), s.getPort(), s.getMembers()).start();
            System.out.print(n);
            while (true) {
            }
        } catch (InterruptedException | UnknownHostException ignored) {
        }

        if (n != null) n.shutdown();
    }
}

