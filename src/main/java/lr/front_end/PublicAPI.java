package lr.front_end;

import lr.Data;
import lr.Messages.MessageManage;
import lr.Messages.Message.*;
import lr.Node;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Created by luca on 01/03/16.
 */
@RestController
@RequestMapping("/api")
public class PublicAPI {

    @RequestMapping(method = RequestMethod.GET)
    public Response<?> get(@RequestParam(value = "key") String key) {
        Optional<GossipResource> opt_r = GossipResource.getInstance();
        if (opt_r.isPresent()) {
            GossipResource r = opt_r.get();
            Node n = r.getRandomNode();
            n.send(new MessageManage(MSG_TYPE.REQUEST, MSG_OPERATION.GET, r, Optional.of(new Data(key))));

            return new Response<>(RESPONSE_STATUS.ok, r.<MessageManage>receive().get().getData());
        } else
            return new Response<>(RESPONSE_STATUS.error, "error");
    }

    @RequestMapping(method = RequestMethod.POST)
    public Response<String> add(@RequestBody Data data) {
        Optional<GossipResource> r = GossipResource.getInstance();
        if (r.isPresent()) {
            Node n = r.get().getRandomNode();
            n.send(new MessageManage(MSG_TYPE.REQUEST, MSG_OPERATION.ADD, r.get() , Optional.of(data)));
            return new Response<>(RESPONSE_STATUS.ok, "send to "+ n.getId());
        }
        return new Response<>(RESPONSE_STATUS.error, "error");
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public Response<String> del(@RequestParam(value = "key") String key) {
        Optional<GossipResource> r = GossipResource.getInstance();
        if (r.isPresent()) {
            Node n = r.get().getRandomNode();
            n.send(new MessageManage(MSG_TYPE.REQUEST, MSG_OPERATION.DEL, r.get(), Optional.of(new Data(key))));
            return new Response<>(RESPONSE_STATUS.ok, "send to "+ n.getId());
        }
        return new Response<>(RESPONSE_STATUS.error, "error");
    }

    @RequestMapping(method = RequestMethod.PUT)
    public Response<String> update(@RequestBody Data data) {
        Optional<GossipResource> r = GossipResource.getInstance();
        if (r.isPresent()) {
            Node n = r.get().getRandomNode();
            n.send(new MessageManage(MSG_TYPE.REQUEST, MSG_OPERATION.UP, r.get(), Optional.of(data)));
            return new Response<>(RESPONSE_STATUS.ok, "send to "+ n.getId());
        }
        return new Response<>(RESPONSE_STATUS.error, "error");
    }

    enum RESPONSE_STATUS {ok,error}
    class Response<T>{
        private RESPONSE_STATUS status;
        private T data;

        public Response(){}

        public Response(RESPONSE_STATUS status, T msg) {
            this.status = status;
            this.data = msg;
        }

        public RESPONSE_STATUS getStatus() {
            return status;
        }

        public void setStatus(RESPONSE_STATUS status) {
            this.status = status;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }
}


