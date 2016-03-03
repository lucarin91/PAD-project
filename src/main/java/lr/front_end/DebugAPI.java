package lr.front_end;

import lr.Data;
import lr.Messages.MessageManage;
import lr.Messages.Message.*;
import lr.Messages.MessageStatus;
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
                n.send(new MessageStatus(MSG_TYPE.REQUEST, r));
            }
            Map<String, Map<String, Data<?>>> res = new HashMap<>();
            for (Node item : list) {
                MessageStatus m = r.<MessageStatus>receive().get();
                res.put(m.getSender().getId(), m.getStore());
            }
            return res;
        } else
            return null;
    }
}

