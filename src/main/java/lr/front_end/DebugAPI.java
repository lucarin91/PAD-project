package lr.front_end;

import lr.Data;
import lr.Messages.MessageManage;
import lr.Messages.Message.*;
import lr.Messages.MessageStatus;
import lr.Node;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ListenableFutureReturnValueHandler;

import java.util.*;

/**
 * Created by luca on 02/03/16.
 */
@RestController
@RequestMapping("/status")
public class DebugAPI {

    @RequestMapping(path = "/store", method = RequestMethod.GET)
    public List<StatusObj> store() {
        Optional<GossipResource> opt_r = GossipResource.getInstance();
        if (opt_r.isPresent()) {
            GossipResource r = opt_r.get();
            List<Node> list = r.getNode();
            for (Node n : list) {
                n.send(new MessageStatus(MSG_TYPE.REQUEST, r));
            }
            List<StatusObj> res = new ArrayList<>();
            for (Node item : list) {
                MessageStatus m = r.<MessageStatus>receive().get();
                StatusObj s = new StatusObj(m.getSender(), m.getStore());
                res.add(s);
            }
            return res;
        } else
            return null;
    }

    @RequestMapping(path = "/ch", method = RequestMethod.GET)
    public List<ChNode> ch() {
        Optional<GossipResource> opt_r = GossipResource.getInstance();
        if (opt_r.isPresent()) {
            GossipResource r = opt_r.get();
            List<Node> list = r.getNode();
            for (Node n : list) {
                n.send(new MessageStatus(MSG_TYPE.REQUEST, r));
            }

            List<ChNode> res = new ArrayList<>();
            for (Node item : list) {
                MessageStatus m = r.<MessageStatus>receive().get();
                TreeMap<Integer,Node> ch = m.getCh();
                List<ChNodeChildren> listNode = new ArrayList<>();
                for (Map.Entry<Integer,Node> i : ch.entrySet()){
                    listNode.add(new ChNodeChildren(i.getKey(),i.getValue().getId()));
                }
                res.add(new ChNode(m.getSender().getId(),listNode));
            }
            return res;
        } else
            return null;
    }

    class ChNode {
        private String id;
        private List<ChNodeChildren> ch;

        public ChNode(){ }
        public ChNode(String id, List<ChNodeChildren> ch) {
            this.id = id;
            this.ch = ch;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<ChNodeChildren> getCh() {
            return ch;
        }

        public void setCh(List<ChNodeChildren> ch) {
            this.ch = ch;
        }
    }

   class ChNodeChildren{
        private int hash;
        private String id;

        public ChNodeChildren(){ }

        public int getHash() {
            return hash;
        }

        public void setHash(int hash) {
            this.hash = hash;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public ChNodeChildren(int hash, String id) {
            this.hash = hash;
            this.id = id;
        }
    }

    class StatusObj{
        private String id;
        private String ip;
        private Map<String, Data<?>> store;

        public StatusObj(){ }

        public StatusObj(Node n, Map<String, Data<?>> store){
            this.store = store;
            this.id = n.getId();
            this.ip = n.getIp();
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

        public Map<String, Data<?>> getStore() {
            return store;
        }

        public void setStore(Map<String, Data<?>> store) {
            this.store = store;
        }
    }
}

