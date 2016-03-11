package lr.core;

import com.beust.jcommander.JCommander;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Created by luca on 24/02/16.
 */
public class PadFs {
    public static void main(String[] args) throws IOException {

        CmdArgs s = new CmdArgs();
        new JCommander(s, args);

        NodeService n = null;
        try {
            n = new NodeService(s.getId(), s.getIp(), s.getPort(), s.getMembers());
            System.out.print(n);
            while (true) { }
//            while (true) {
//                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//                System.out.print("\nEnter command: ");
//                String s = br.readLine();
//                String[] cmd = s != null ? s.split("\\s+") : new String[]{"quit"};
//                switch (cmd[0]) {
//                    case "quit":
//                        System.exit(0);
//                        break;
//                }
//            }
        } catch (InterruptedException | UnknownHostException e) {
            e.printStackTrace();
        }finally {
            if (n != null) n.shutdown();
        }
    }
}

