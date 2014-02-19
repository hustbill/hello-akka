package metaActor;

import java.io.Serializable;
import java.util.*;

import org.junit.Test;

import scala.actors.Future;
import scala.concurrent.duration.*;

import CSP.IntVariable;
import CSP.Solution;
import akka.actor.Address;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.AddressFromURIString;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinRouter;
import akka.util.Timeout;

// to verify the sync between mac and windows pc.

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
			public List<ActorRef> ConstraintsList;

			public Seperate( List<ActorRef> ConstraintsList) {
				this.ConstraintsList = ConstraintsList;
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
				// add the Actor sender() into HashMap
				map.put(getSender().path(), getSender().path().name()); 
		    	// print out the old map
			    // System.out.println(map +"\n");
				System.out.print("\n" + registering);
			} else if (message instanceof Seperate) {
				// Send the current greeting back to the sender
				System.out.print("\n Seperate!\n" + getSender());
				//((Seperate) message).seperate(map, ((Seperate) message).ConstraintsList);
				seperate(map, ((Seperate) message).ConstraintsList);
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

		public static void cooperation(HashMap map,
				List<ActorRef> ConstraintsList) {
			// print out the old map
			System.out.println();
			System.out.println(map);
			System.out.println();

			System.out.println("#2 all constraints in ConstraintsList");
			Iterator<ActorRef> iterator_constraint = ConstraintsList.iterator();
			while (iterator_constraint.hasNext()) {
				System.out.println(iterator_constraint.next());
			}

			// recreate the newMap with the constraints.
			// ... to be develop
		}

	}

	public static void seperate(HashMap map, List<ActorRef> ConstraintsList) {
		
		
		System.out.println("#3 seperate constraints in ConstraintsList");
		Iterator<ActorRef> iterator_constraint = ConstraintsList.iterator();
		while (iterator_constraint.hasNext()) {
			System.out.println(iterator_constraint.next().path().name());
		}
		
		// print out the old map
	     System.out.print(map +"\n");
		
//		
//        for (Entry<String, String> entry : map.entrySet()) {
//            String key=entry.getKey();
//            String value=entry.getValue();
//            System.out.println(key + " " + value);  
//        }
        
	
		
	}
	
	public static void main(String[] args) throws Exception {

		String greeting = "";
		// Create the 'helloakka' actor system
		final ActorSystem system = ActorSystem.create("helloakka");
		// Create the MetaActor
		final ActorRef metaActor = system.actorOf(
				Props.create(MetaActor.class), "metaActor");

		final ActorRef democratActor[] = new ActorRef[10];
		String[] democratNameList = new String[10];
		for (int i = 0; i < 10; i++) {
			democratNameList[i] = "democrate" + i;
			democratActor[i] = system.actorOf(
					Props.create(DemocratActor.class), democratNameList[i]);
			metaActor.tell(new MetaActorImpt.MetaActor.Register(
					democratNameList[i]), democratActor[i]);
		}

		final ActorRef republicanActor[] = new ActorRef[10];
		String[] republicanNameList = new String[10];
		for (int i = 0; i < 10; i++) {
			republicanNameList[i] = "republican" + i;
			republicanActor[i] = system.actorOf(
					Props.create(RepublicanActor.class), republicanNameList[i]);
			metaActor.tell(new MetaActorImpt.MetaActor.Register(
					republicanNameList[i]), republicanActor[i]);
		}

		final ActorRef workerActor[] = new ActorRef[10];
		String[] workerNameList = new String[10];
		for (int i = 0; i < 10; i++) {
			workerNameList[i] = "Worker" + i;
			workerActor[i] = system.actorOf(Props.create(WorkerActor.class),
					workerNameList[i]);
			metaActor.tell(new MetaActorImpt.MetaActor.Register(
					workerNameList[i]), workerActor[i]);
	
		}
		
		
		 List<ActorRef> ConstraintsList = Arrays.asList(new ActorRef[] { democratActor[5],
		  republicanActor[3], workerActor[2] });
		 
		 metaActor.tell(new MetaActorImpt.MetaActor.Seperate(ConstraintsList), workerActor[2]) ;
		
		 /* ConstraintsList = Arrays.asList(new ActorRef[] { democratActor,
		 * workerActor1[i] }); // seperate(HashMap map,List<ActorRef>
		 * ConstraintsList ) // seperate( metaActor, ConstraintsList );
		 * metaActor.tell(new
		 * MetaActorImpt.MetaActor.Seperate(ConstraintsList),
		 * workerActor1[i]);
		 */

		/*
		 * List<ActorRef> ActorsList= Arrays.asList(new ActorRef[] {
		 * democratActor, republicanActor, workerActor });
		 * 
		 * List<ActorRef> ConstraintsList = Arrays.asList(new ActorRef[] {
		 * democratActor, republicanActor });
		 */

		// iterator loop
		/*
		 * System.out.println("#1 all actors in ActorsList"); Iterator<ActorRef>
		 * iterator = ActorsList.iterator(); while (iterator.hasNext()) {
		 * System.out.println(iterator.next()); }
		 */

	

		// Start the Calculate()
		// String[] args1 = {"helloActor", "worldActor"};
		// CSPSolver.allocate(args1);

	}

	public static void unUsedCode() {

		final ActorSystem system = ActorSystem.create("helloakka");
		// Create the MetaActor
		final ActorRef metaActor = system.actorOf(
				Props.create(MetaActor.class), "metaActor");

		// ActorRef roundRobinRouter = system.actorOf(
		// Props.create(PrintlnActor.class).withRouter(
		// new RoundRobinRouter(10)), "router");
		// for (int i = 1; i <= 10; i++) {
		// roundRobinRouter.tell(i, ActorRef.noSender());
		// // System.out.println("\n roundRobinRouter actors" +
		// // roundRobinRouter.path().name() +"\n");
		// }
		// System.out.println("\n radmonRouter actors\n");

		/*
		 * final ActorRef democratActor = system.actorOf(
		 * Props.create(DemocratActor.class), "d"); metaActor.tell(new
		 * MetaActorImpt.MetaActor.Register("d"), democratActor);
		 */
		/*
		 * final ActorRef republicanActor = system.actorOf(
		 * Props.create(RepublicanActor.class), "r");
		 */
		/*
		 * metaActor.tell(new MetaActorImpt.MetaActor.Register("r"),
		 * republicanActor);
		 */

		// final ActorRef workerActor = system.actorOf(
		// Props.create(WorkerActor.class), "W");
		// metaActor.tell(new MetaActorImpt.MetaActor.Register("W"),
		// workerActor);

		// seperate(HashMap map,List<ActorRef> ConstraintsList );

		/*
		 * ActorRef actor1 = system.actorOf(Props.create(Worker.class),
		 * "actor1"); ActorRef actor2 =
		 * system.actorOf(Props.create(Worker.class), "actor2"); ActorRef actor3
		 * = system.actorOf(Props.create(Worker.class), "actor3");
		 * Iterable<String> routees = Arrays.asList(new String[] {
		 * "/user/actor1", "/user/actor2", "/user/actor3" }); ActorRef router =
		 * system.actorOf(Props.empty().withRouter( new
		 * RoundRobinRouter(routees)));
		 * 
		 * System.out.print("\n create actos:" + actor1.toString() + " \n ");
		 */

		Address addr1 = new Address("akka", "helloakka", "localhost", 1234);
		Address addr2 = AddressFromURIString
				.parse("akka://othersys@anotherhost:1234");

		Address addr3 = AddressFromURIString
				.parse("akka://my-sys/user/service-a/worker1");
		Address[] addresses = new Address[] { addr1 };

		// Address[] addresses = new Address[] { addr1, addr2 };
		/*
		 * ActorRef routerRemote = system.actorOf(Props.create(Worker.class)
		 * .withRouter( new RemoteRouterConfig(new RoundRobinRouter(5),
		 * addresses))); //
		 * http://doc.akka.io/docs/akka/current/java/routing.html
		 * System.out.print("\n create routerRemote actos:" +
		 * routerRemote.toString() + " \n ");
		 */
		final SupervisorStrategy strategy = new OneForOneStrategy(
				5,
				Duration.create("1 minute"),
				Collections
						.<Class<? extends Throwable>> singletonList(Exception.class));
		/*
		 * final ActorRef router2 = system.actorOf(Props.create(Worker.class)
		 * .withRouter( new RoundRobinRouter(5)
		 * .withSupervisorStrategy(strategy)));
		 */

		/*
		 * Set<ActorSelection> initialContacts = new HashSet<ActorSelection>();
		 * initialContacts.add(system.actorSelection("/user/receptionist"));
		 * ActorRef clusterClient =
		 * system.actorOf(ClusterClient.defaultProps(initialContacts),
		 * "clusterClient"); system.actorOf(Worker.props(clusterClient,
		 * Props.create(Worker.class)), "worker");
		 */

		// Create fibonacciActor
		final ActorRef fibonacciActor = system.actorOf(
				Props.create(FibonacciActor.class), "fibonacciActor");

		System.out.print("\n send message to  fibonacciActors:"
				+ fibonacciActor.toString() + " \n ");
		fibonacciActor.tell(new FibonacciActor.FibonacciNumber(9), metaActor);

		// Create several helloActors
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

	}

	@SuppressWarnings("unchecked")
	@Test
	public static void countVotesAsIntendedNotAsInFlorida() throws Exception {

		// Create the 'helloakka' actor system
		final ActorSystem system = ActorSystem.create("customRouterSys");
		ActorRef routedActor = system.actorOf(Props.empty().withRouter(
				new VoteCountRouter()));
		routedActor.tell(Message.DemocratVote, ActorRef.noSender());
		routedActor.tell(Message.DemocratVote, ActorRef.noSender());
		routedActor.tell(Message.RepublicanVote, ActorRef.noSender());
		routedActor.tell(Message.DemocratVote, ActorRef.noSender());
		routedActor.tell(Message.RepublicanVote, ActorRef.noSender());
		Timeout timeout = new Timeout(Duration.create(1, "seconds"));
		/*
		 * Future<Object> democratsResult = ask(routedActor,
		 * Message.DemocratCountResult, timeout); Future<Object>
		 * republicansResult = ask(routedActor, Message.RepublicanCountResult,
		 * timeout);
		 */
		// assertEquals(3, Await.result((Awaitable<T>) democratsResult,
		// timeout.duration()));
		// assertEquals(2, Await.result((Awaitable<T>) republicansResult,
		// timeout.duration()));
	}

	private Future<Object> ask(ActorRef routedActor,
			Message democratcountresult, Timeout timeout) {
		// TODO Auto-generated method stub
		return null;
	}

}
