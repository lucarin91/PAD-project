package lr.front_end;

import lr.Data;
import lr.MessageManage;
import lr.Node;
import org.springframework.web.bind.annotation.*;

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
                n.send(new MessageManage(MessageManage.MSG_TYPE.STATUS, MessageManage.SENDER_TYPE.FRONT, r, null));
            }
            Map<String, Map<String, Data<?>>> res = new HashMap<>();
            for (Node item : list) {
                MessageManage m = r.receive().get();
                res.put(m.getSender().getId(), m.getStore());
            }
            return res;
        } else
            return null;
    }
}

