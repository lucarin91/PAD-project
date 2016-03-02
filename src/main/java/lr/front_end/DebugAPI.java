package lr.front_end;

import lr.Data;
import lr.Message;
import lr.Node;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by luca on 02/03/16.
 */
@RestController
@RequestMapping("/debug")
public class DebugAPI {

    @RequestMapping(method = RequestMethod.GET)
    public List<Map<String, Data<?>>> status() {
        Optional<GossipResource> opt_r = GossipResource.getInstance();
        if (opt_r.isPresent()) {
            GossipResource r = opt_r.get();
            List<Node> list = r.getNode();
            for (Node n : list) {
                n.send(new Message(Message.MSG_TYPE.STATUS, Message.SENDER_TYPE.FRONT, r.getIp(), r.getPortM(), null));
            }
            List<Map<String, Data<?>>> res = new ArrayList<>();
            for (Node n : list){
                res.add(r.receive().get().getStore());
            }
            return res;
        } else
            return null;
    }
}

