package ch;

/**
 * @author hua Zhang
 */
public class MessageEvent extends ChatEvent {

    private final String message;

    public MessageEvent(String username, String message) {
        super(username);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
