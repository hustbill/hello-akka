package ch;

//import akka.actor.Actors.*;
import akka.actor.*;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.TypedActor;


import ch.*;

import java.io.*;

import static ch.ChatUtil.*;

/**
 * @author Hua Zhang
 */
public class ChatClient {

    private final String clientHost = "localhost";
    private final int clientPort = getFreePort();

    public static void main(String[] args) {
        System.out.print("Username: ");
        String username = readInput();
        new ChatClient(username);
    }

    private final String username;

    public ChatClient(String username) {
        this.username = username;

        remote().start(clientHost, clientPort);

        ActorRef serverActor = login();
        chat(serverActor);
        logout(username, serverActor);

    }

    private ActorRef login() {
        ActorRef serverActor = remote().actorFor(CHAT_SERVICE_NAME, CHAT_SERVICE_HOST, CHAT_SERVICE_PORT);
        //serverActor.sendOneWay(new LoginEvent(clientHost, clientPort, username));
        return serverActor;
    }

    private void chat(ActorRef serverActor) {
        while(true) {
            String message = readInput();
            if (message.trim().equals("bye")) return;
          //  serverActor.sendOneWay(new MessageEvent(username, message));
        }
    }

    private void logout(String username, ActorRef serverActor) {
        //serverActor.sendOneWay(new LogoutEvent(username));
        //remote().shutdown();
        System.exit(0);
    }

    private static String readInput() {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        String username = null;
        try {
            username = input.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return username;
    }
}
