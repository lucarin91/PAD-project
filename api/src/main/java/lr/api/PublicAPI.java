package lr.api;

import lr.core.Data;
import lr.core.Exception.SendException;
import lr.core.Nodes.GossipResource;
import lr.core.Messages.Message.*;
import lr.core.Messages.MessageRequest;
import lr.core.Messages.MessageResponse;
import org.springframework.web.bind.annotation.*;

/**
 * Created by luca on 01/03/16.
 */
@RestController
@RequestMapping("/api")
public class PublicAPI {

    private MessageResponse<?> sendRequest(MessageRequest<?> req){
        try {
            return GossipResource.sendRequestToRandomNode(req);
        } catch (SendException e) {
            return new MessageResponse<>(MessageResponse.MSG_STATUS.ERROR, "error");
        }
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


