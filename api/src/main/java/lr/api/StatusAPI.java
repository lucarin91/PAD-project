package lr.api;

import lr.core.Data;
import lr.core.Exception.SendException;
import lr.core.Nodes.GossipResource;
import lr.core.Messages.Message.*;
import lr.core.Messages.MessageRequest;
import lr.core.Messages.MessageStatus;
import lr.core.Nodes.Node;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by luca on 02/03/16.
 */
@RestController
@RequestMapping("/status")
public class StatusAPI {

    @RequestMapping(method = RequestMethod.GET)
    public List<StatusObj> status() {
        Optional<GossipResource> opt_r = GossipResource.getInstance();
        if (opt_r.isPresent()) {
            GossipResource r = opt_r.get();
            List<Node> list = r.getNodes();
            for (Node n : list) {
                try {
                    n.send(new MessageRequest<>(r, MSG_OPERATION.STATUS));
                } catch (SendException e) {
                    e.printStackTrace();
                }
            }
            List<StatusObj> res = new ArrayList<>();
            for (Node item : list) {
                r.<MessageStatus>receive().ifPresent(m -> {
                    List<ChNodeChildren> listNode = m.getCh().entrySet().stream().map(i -> new ChNodeChildren(i.getKey(), i.getValue().getId())).collect(Collectors.toList());
                    StatusObj s = new StatusObj(m.getSender(), new ArrayList<>(m.getStore().values()), listNode);
                    res.add(s);
                });
            }
            return res;
        } else
            return null;
    }

    private class ChNodeChildren {
        private Long hash;
        private String id;

        public ChNodeChildren() {
        }

        public Long getHash() {
            return hash;
        }

        public void setHash(Long hash) {
            this.hash = hash;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public ChNodeChildren(Long hash, String id) {
            this.hash = hash;
            this.id = id;
        }
    }

    private class StatusObj {
        private String id;
        private String ip;
        private List<Data<?>> store;
        private List<ChNodeChildren> ch;

        public StatusObj() {
        }

        public StatusObj(Node n, List<Data<?>> store, List<ChNodeChildren> ch) {
            this.id = n.getId();
            this.ip = n.getIp();
            this.store = store;
            this.ch = ch;
        }

        public List<ChNodeChildren> getCh() {
            return ch;
        }

        public void setCh(List<ChNodeChildren> ch) {
            this.ch = ch;
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

        public List<Data<?>> getStore() {
            return store;
        }

        public void setStore(List<Data<?>> store) {
            this.store = store;
        }
    }
}

