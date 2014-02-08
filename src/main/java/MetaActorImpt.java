import java.io.Serializable;
import java.util.*;

import CSP.DefaultSolver;
import CSP.IntVariable;
import CSP.Network;
import CSP.NotEquals;
import CSP.Solution;
import CSP.Solver;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class MetaActorImpt {


	public static class MetaActor extends UntypedActor {

		// Store the name of Actor when Actor was initialized
		public static class Register implements Serializable {
			
			
			
			public final String who;

			public Register(String who) {
				this.who = who;
			}
		}

		public static class Seperate implements Serializable {
			public final String message;

			public Seperate(String message) {
				this.message = message;
			}
		}

		public static class Combain implements Serializable {
			public final String message;

			public Combain(String message) {
				this.message = message;
			}
		}

		String registering = "";
		public HashMap map = new HashMap();
		
		public void onReceive(Object message) throws Exception {
			if (message instanceof Register) {
				registering = "Register, " + ((Register) message).who;
				int i=1;
				map.put( i++, getSender().toString()); //add the Actor sender() into HashMap
				
				//print out the old map
				System.out.println();
				System.out.println(map);
				System.out.println();
				
				System.out.print("\n" + registering);
			} else if (message instanceof Seperate) {
				// Send the current greeting back to the sender
				getSender().tell(new Greeting(registering), getSelf());
				System.out.print("\n repsonse!\n" + getSender());
			} else
				unhandled(message);
		}
	}

	public static class CSPSolver {
		static int nodes = 5;
			
			static void rightOf(IntVariable v1, IntVariable v2) {
				v1.equals(v2.add(1));
			}
			
			static void nextTo(IntVariable v1, IntVariable v2) {
				v1.subtract(v2).abs().equals(1);
			}
			
			static String find(int value, IntVariable[] vs, Solution solution) {
				for (IntVariable v : vs) {
					if (solution.getIntValue(v) == value) {
						return v.getName();
					}
				}
				return null;
			}
			
			public static void cooperation(HashMap map){
				//print out the old map
				System.out.println();
				System.out.println(map);
				System.out.println();
				
				//recreate the newMap with the constraints.
				//... to be develop
				
			}
			
	}

		
	public static void main(String[] args) {
		String greeting = "";
		// Create the 'helloakka' actor system
		final ActorSystem system = ActorSystem.create("helloakka");

		// Create the MetaActor
		final ActorRef metaActor = system.actorOf(
				Props.create(MetaActor.class), "metaActor");

		// Create Hello Actor
		final ActorRef helloActor = system.actorOf(
				Props.create(HelloAkkaJava.HelloActor.class), "hello");
		metaActor.tell(new MetaActorImpt.MetaActor.Register("HelloActor"),
				helloActor);

		// Create World Actor
		final ActorRef worldActor = system.actorOf(
				Props.create(WorldAkkaJava.World.class), "world");
		metaActor.tell(new MetaActorImpt.MetaActor.Register("WorldActor"),
				worldActor);

		// Create the "actor-in-a-box"
		final Inbox inbox = Inbox.create(system);

		// System.out.print("\n Tell the 'helloActor' - ping!\n");
		// Tell the 'helloActor' to change its 'greeting' message
		helloActor.tell(new HelloAkkaJava.WhoToGreet("worldActor"), worldActor);

		// System.out.print("\n Tell the 'helloActor' - pong!\n");
		helloActor.tell(new HelloAkkaJava.Greet("world"), worldActor);

		// Change the greeting and ask for it again
		helloActor.tell(new HelloAkkaJava.WhoToGreet("worldActor again!"),
				ActorRef.noSender());

		System.out.print("\n Change the greeting order!\n");
		// Tell the 'helloActor' to change its 'greeting' message
		worldActor.tell(new WorldAkkaJava.WhoToGreet("helloActor"), helloActor);

		// System.out.print("\n Tell the 'helloActor' - pong!\n");
		worldActor.tell(new WorldAkkaJava.Greet("hello"), helloActor);

		// Change the greeting and ask for it again
		worldActor.tell(new WorldAkkaJava.WhoToGreet("helloActor again!"),
				ActorRef.noSender());
		// System.out.print("\n Change the greeting and ask for it again!\n");

		// Create several helloActors

		// Start the Calculate()
		//String[] args1 = {"helloActor", "worldActor"};
		//CSPSolver.allocate(args1);
				
		

	}

}
