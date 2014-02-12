package metaActor;

import java.io.Serializable;
import java.util.*;

import org.junit.Test;

import scala.actors.Future;
import scala.concurrent.Await;
import scala.concurrent.Awaitable;
import scala.concurrent.duration.*;
import CSP.DefaultSolver;
import CSP.IntVariable;
import CSP.Network;
import CSP.NotEquals;
import CSP.Solution;
import CSP.Solver;
import akka.actor.Address;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.AddressFromURIString;
import akka.actor.Inbox;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinRouter;
import akka.remote.*;
import akka.remote.routing.RemoteRouterConfig;
import akka.util.Timeout;

public class MetaActorImpt {

	 public enum Message {
		  DemocratVote, DemocratCountResult, RepublicanVote, RepublicanCountResult
		}
	 

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
				int i = 1;
				map.put(i++, getSender().toString()); // add the Actor sender()
														// into HashMap

				// print out the old map
				System.out.println();
				System.out.println(map);
				System.out.println();

				System.out.print("\n" + registering);
			} else if (message instanceof Seperate) {
				// Send the current greeting back to the sender
				// getSender().tell(new Seperate(registering), getSelf());
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

		public static void cooperation(HashMap map) {
			// print out the old map
			System.out.println();
			System.out.println(map);
			System.out.println();

			// recreate the newMap with the constraints.
			// ... to be develop

		}

	}

	public static void main(String[] args) throws Exception {

		String greeting = "";
		// Create the 'helloakka' actor system
		final ActorSystem system = ActorSystem.create("helloakka");

		ActorRef actor1 = system.actorOf(Props.create(Worker.class), "actor1");
		ActorRef actor2 = system.actorOf(Props.create(Worker.class), "actor2");
		ActorRef actor3 = system.actorOf(Props.create(Worker.class), "actor3");
		Iterable<String> routees = Arrays.asList(new String[] { "/user/actor1",
				"/user/actor2", "/user/actor3" });
		ActorRef router = system.actorOf(Props.empty().withRouter(
				new RoundRobinRouter(routees)));

		System.out.print("\n create actos:" + actor1.toString() + " \n ");

		Address addr1 = new Address("akka", "remotesys", "otherhost", 1234);
		Address addr2 = AddressFromURIString
				.parse("akka://othersys@anotherhost:1234");
		Address[] addresses = new Address[] { addr1, addr2 };
//		ActorRef routerRemote = system.actorOf(Props.create(Worker.class)
//				.withRouter(
//						new RemoteRouterConfig(new RoundRobinRouter(5),
//								addresses)));
		/* http://doc.akka.io/docs/akka/current/java/routing.html */
//		System.out.print("\n create routerRemote actos:"
//				+ routerRemote.toString() + " \n ");

		final SupervisorStrategy strategy = new OneForOneStrategy(
				5,
				Duration.create("1 minute"),
				Collections
						.<Class<? extends Throwable>> singletonList(Exception.class));
	
		final ActorRef router2 = system.actorOf(Props.create(Worker.class)
				.withRouter(
						new RoundRobinRouter(5)
								.withSupervisorStrategy(strategy)));
		// Create the MetaActor
		final ActorRef metaActor = system.actorOf(
				Props.create(MetaActor.class), "metaActor");
		
		// Create fibonacciActor
		int i =1;

		final ActorRef fibonacciActor = system.actorOf(
				Props.create(FibonacciActor.class), "fibonacciActor");
		
		System.out.print("\n send message to  fibonacciActors:"
				+ fibonacciActor.toString() + " \n ");
		fibonacciActor.tell(new FibonacciActor.FibonacciNumber(9),
				 metaActor);

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
		// final Inbox inbox = Inbox.create(system);
		//
		// // System.out.print("\n Tell the 'helloActor' - ping!\n");
		// // Tell the 'helloActor' to change its 'greeting' message
		// helloActor.tell(new HelloAkkaJava.WhoToGreet("worldActor"),
		// worldActor);
		//
		// // System.out.print("\n Tell the 'helloActor' - pong!\n");
		// helloActor.tell(new HelloAkkaJava.Greet("world"), worldActor);
		//
		// // Change the greeting and ask for it again
		// helloActor.tell(new HelloAkkaJava.WhoToGreet("worldActor again!"),
		// ActorRef.noSender());
		//
		// System.out.print("\n Change the greeting order!\n");
		// // Tell the 'helloActor' to change its 'greeting' message
		// worldActor.tell(new WorldAkkaJava.WhoToGreet("helloActor"),
		// helloActor);
		//
		// // System.out.print("\n Tell the 'helloActor' - pong!\n");
		// worldActor.tell(new WorldAkkaJava.Greet("hello"), helloActor);
		//
		// // Change the greeting and ask for it again
		// worldActor.tell(new WorldAkkaJava.WhoToGreet("helloActor again!"),
		// ActorRef.noSender());
		// System.out.print("\n Change the greeting and ask for it again!\n");

		// Create several helloActors

		// Start the Calculate()
		// String[] args1 = {"helloActor", "worldActor"};
		// CSPSolver.allocate(args1);

		//
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public static void countVotesAsIntendedNotAsInFlorida() throws Exception {

		
		 
		// Create the 'helloakka' actor system
		final ActorSystem system = ActorSystem.create("customRouterSys");
	  ActorRef routedActor = system.actorOf(
	    Props.empty().withRouter(new VoteCountRouter()));
	  routedActor.tell(Message.DemocratVote, ActorRef.noSender());
	  routedActor.tell(Message.DemocratVote, ActorRef.noSender());
	  routedActor.tell(Message.RepublicanVote, ActorRef.noSender());
	  routedActor.tell(Message.DemocratVote, ActorRef.noSender());
	  routedActor.tell(Message.RepublicanVote, ActorRef.noSender());
	  Timeout timeout = new Timeout(Duration.create(1, "seconds"));
	/*  Future<Object> democratsResult =
	    ask(routedActor, Message.DemocratCountResult, timeout);
	  Future<Object> republicansResult =
	    ask(routedActor, Message.RepublicanCountResult, timeout);
	 */
	  //assertEquals(3, Await.result((Awaitable<T>) democratsResult, timeout.duration()));
	  //assertEquals(2, Await.result((Awaitable<T>) republicansResult, timeout.duration()));
	}

	private Future<Object> ask(ActorRef routedActor,
			Message democratcountresult, Timeout timeout) {
		// TODO Auto-generated method stub
		return null;
	}

}
