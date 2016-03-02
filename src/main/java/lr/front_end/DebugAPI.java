package lr.front_end;

import ie.ucd.murmur.MurmurHash;
import lr.Data;
import lr.Message;
import lr.Node;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.*;

/**
 * Created by luca on 02/03/16.
 */
@RestController
@RequestMapping("/debug")
public class DebugAPI {

    @RequestMapping(method = RequestMethod.GET)
    public Map<String, Map<String, Data<?>>> status() {
        Optional<GossipResource> opt_r = GossipResource.getInstance();
        if (opt_r.isPresent()) {
            GossipResource r = opt_r.get();
            List<Node> list = r.getNode();
            for (Node n : list) {
                n.send(new Message(Message.MSG_TYPE.STATUS, Message.SENDER_TYPE.FRONT, r, null));
            }
            Map<String, Map<String, Data<?>>> res = new HashMap<>();
            for (Node item : list) {
                Message m = r.receive().get();
                res.put(m.getSender().getId(), m.getStore());
            }
            return res;
        } else
            return null;
    }
}

