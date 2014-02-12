package ch;

import static ch.ChatUtil.*;

import akka.actor.TypedActor;
import akka.actor.UntypedActor;

import java.util.*;

/**
 * @author Hua Zhang
 */
public class ChatServer {

    public static void main(String[] args) {
        new ChatServer().start();
    }

    private void start() {
        remote().start(CHAT_SERVICE_HOST, CHAT_SERVICE_PORT, getClass().getClassLoader()).register(CHAT_SERVICE_NAME, actorOf(ChatServerListener.class));
    }

    public static class ChatServerListener extends UntypedActor {

        private final Map<String, ChatSession> sessions = new HashMap<String, ChatSession>();

        public void onReceive(Object event) {
            if (event instanceof LoginEvent) doLogin((LoginEvent)event);
            else if (event instanceof MessageEvent) broadcastMessage((MessageEvent)event);
            else if (event instanceof LogoutEvent) doLogout((LogoutEvent)event);
        }

        private void doLogin(LoginEvent login) {
        	
        	ChatSession session =
        			TypedActor.get(system).typedActorOf(
        			new TypedProps<ChatSessionImpl>(ChatSession.class, ChatSessionImpl.class));

        	
        	
            ChatSession session = TypedActor.newRemoteInstance(ChatSession.class, ChatSessionImpl.class, login.getClientHost(), login.getClientPort());
            String username = login.getUsername();
            sessions.put(username, session);
            System.out.println(username + " just logged in");
            broadcastMessage(username, "I just logged in");
        }

        private void broadcastMessage(MessageEvent messageEvent) {
            broadcastMessage(messageEvent.getUsername(), messageEvent.getMessage());
        }

        private void broadcastMessage(String sender, String message) {
            System.out.println(sender + " sent: " + message);
            for (Map.Entry<String, ChatSession> entry : sessions.entrySet()) {
                if (!entry.getKey().equals(sender)) entry.getValue().printMessage(sender, message);
            }
        }

        private void doLogout(LogoutEvent logout) {
            String username = logout.getUsername();
            ChatSession session = sessions.remove(username);
            TypedActor.stop(session);
            System.out.println(username + " just logged out");
            broadcastMessage(username, "I just logged out");
        }
    }
}
