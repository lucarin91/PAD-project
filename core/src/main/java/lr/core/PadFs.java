package lr.core;

import com.beust.jcommander.JCommander;
import com.google.code.gossip.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luca on 24/02/16.
 */
public class PadFs {
    public static void main(String[] args) throws IOException {

        CommandArgs s = new CommandArgs();
        new JCommander(s, args);

        NodeService n = null;
        try {
            n = new NodeService(s.getIp(), s.getPort(), s.getId(), s.getMembers());
            System.out.print(n);
            while (true){}
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

