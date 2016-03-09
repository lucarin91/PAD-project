package lr.front_end;

import lr.Data;
import lr.Messages.MessageManage;
import lr.Messages.Message.*;
import lr.Messages.MessageRequest;
import lr.Messages.MessageStatus;
import lr.Node;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ListenableFutureReturnValueHandler;

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
            List<Node> list = r.getNode();
            for (Node n : list) {
                n.send(new MessageRequest<>(r,MSG_OPERATION.STATUS));
            }
            List<StatusObj> res = new ArrayList<>();
            for (Node item : list) {
                MessageStatus m = r.<MessageStatus>receive().get();

                List<ChNodeChildren> listNode = m.getCh().entrySet().stream().map(i -> new ChNodeChildren(i.getKey(), i.getValue().getId())).collect(Collectors.toList());

                StatusObj s = new StatusObj(m.getSender(), new ArrayList<>(m.getStore().values()), listNode);
                res.add(s);
            }
            return res;
        } else
            return null;
    }
//
//    @RequestMapping(path = "/ch", method = RequestMethod.GET)
//    public List<ChNode> ch() {
//        Optional<GossipResource> opt_r = GossipResource.getInstance();
//        if (opt_r.isPresent()) {
//            GossipResource r = opt_r.get();
//            List<Node> list = r.getNode();
//            for (Node n : list) {
//                n.send(new MessageStatus(MSG_TYPE.REQUEST, r));
//            }
//
//
//            return res;
//        } else
//            return null;
//    }

//    class ChNode {
//        private String id;
//        private List<ChNodeChildren> ch;
//
//        public ChNode(){ }
//        public ChNode(String id, List<ChNodeChildren> ch) {
//            this.id = id;
//            this.ch = ch;
//        }
//
//        public String getId() {
//            return id;
//        }
//
//        public void setId(String id) {
//            this.id = id;
//        }
//
//        public List<ChNodeChildren> getCh() {
//            return ch;
//        }
//
//        public void setCh(List<ChNodeChildren> ch) {
//            this.ch = ch;
//        }
//    }

    class ChNodeChildren {
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

    class StatusObj {
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

