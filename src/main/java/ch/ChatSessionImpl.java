package ch;

import akka.actor.TypedActor;


/**
 * @author Hua Zhang
 */
//public class ChatSessionImpl extends TypedActor implements ChatSession {
public class ChatSessionImpl implements ChatSession  {

    public void printMessage(String sender, String message) {
        System.out.println(sender + ": " + message);
    }
    


}
