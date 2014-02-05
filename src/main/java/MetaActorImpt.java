
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.Props;
import akka.actor.UntypedActor;


public class MetaActorImpt {
	
	  public static class World extends UntypedActor {
		  
	  }

	  public static void main(String[] args) {
		  String greeting = "";
	        // Create the 'helloakka' actor system
	        final ActorSystem system = ActorSystem.create("helloakka");

	        final ActorRef greeter = system.actorOf(Props.create(HelloAkkaJava.Greeter.class), "greeter");

	        final ActorRef world = system.actorOf(Props.create(WorldAkkaJava.World.class), "world");
	        // Create the "actor-in-a-box"
	        final Inbox inbox = Inbox.create(system);

	        // Tell the 'greeter' to change its 'greeting' message
	        //greeter.tell(new WhoToGreet("akka"), world);
	        
	        System.out.print("\ntell WhoToGreet!\n");
	        greeter.tell(new HelloAkkaJava.WhoToGreet("ping") , world);
	        
	        System.out.print("\ntell Greet!\n");
	        greeter.tell(new HelloAkkaJava.Greet("pong"), world);
	        
	     
	        // Change the greeting and ask for it again
	        greeter.tell(new HelloAkkaJava.WhoToGreet("greeting again!"), ActorRef.noSender());
	        System.out.println("Greeting\n ");
	
	     }

	
}
