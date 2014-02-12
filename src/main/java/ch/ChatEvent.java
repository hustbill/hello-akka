package ch;

import java.io.*;

/**
 * @author Hua Zhang
 */
public abstract class ChatEvent implements Serializable {

    private final String username;

    public ChatEvent(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
