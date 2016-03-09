package lr.front_end;

import lr.Data;
import lr.Messages.Message;
import lr.Messages.MessageManage;
import lr.Messages.Message.*;
import lr.Messages.MessageRequest;
import lr.Messages.MessageResponse;
import lr.Node;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Created by luca on 01/03/16.
 */
@RestController
@RequestMapping("/api")
public class PublicAPI {

    private MessageResponse<?> sendRequest(MessageRequest<?> req){
        Optional<GossipResource> opt_r = GossipResource.getInstance();
        if (opt_r.isPresent()) {
            GossipResource r = opt_r.get();
            Node n = r.getRandomNode();
            req.setSender(r);
            n.send(req);
            Optional<MessageResponse<?>> responseOptional = r.<MessageResponse<?>>receive();
            if (responseOptional.isPresent()) {
                return responseOptional.get();
            }
        }
        return new MessageResponse<>(MessageResponse.MSG_STATUS.ERROR, "error");
    }

    @RequestMapping(method = RequestMethod.GET)
    public MessageResponse<?> get(@RequestParam(value = "key") String key) {
        return sendRequest(new MessageRequest<>(MSG_OPERATION.GET, key));
    }

    @RequestMapping(method = RequestMethod.POST)
    public MessageResponse<?> add(@RequestBody Data data) {
       return sendRequest(new MessageRequest<>(MSG_OPERATION.ADD, data.getKey(), data.getValue()));
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public MessageResponse<?> del(@RequestParam(value = "key") String key) {
        return sendRequest(new MessageRequest<>(MSG_OPERATION.DEL, key));
    }

    @RequestMapping(method = RequestMethod.PUT)
    public MessageResponse<?> update(@RequestBody Data data) {
       return sendRequest(new MessageRequest<>(MSG_OPERATION.UP, data.getKey(), data.getValue()));
    }
}


