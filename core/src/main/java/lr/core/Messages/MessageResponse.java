package lr.core.Messages;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lr.core.Node;

/**
 * Created by luca on 07/03/16.
 */
public class MessageResponse<T> extends Message{
    public static final String KEY_NOT_FOUND = "key not found error";
    public static final String KEY_ALREADY = "key is already present in the FS";

    public enum MSG_STATUS {ERROR, OK}
    private MSG_STATUS status;
    @JsonTypeInfo( use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY )
    private T data;

    public MessageResponse(){ }

    public MessageResponse(Node sender, MSG_STATUS status) {
        super(sender);
        this.status = status;
    }

    public MessageResponse(MSG_STATUS status, T data) {
        this.status = status;
        this.data = data;
    }

    public MessageResponse(Node sender, MSG_STATUS status, T data) {
        super(sender);
        this.status = status;
        this.data = data;
    }

    public MSG_STATUS getStatus() {
        return status;
    }

    public void setStatus(MSG_STATUS status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "MessageResponse{" +
                "status=" + status +
                ", data=" + data +
                "} " + super.toString();
    }
}
