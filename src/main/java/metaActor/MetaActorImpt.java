package metaActor;

import java.io.Serializable;
import java.util.*;

import org.junit.Test;

import scala.actors.Future;
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
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinRouter;
import akka.util.Timeout;

import java.util.Enumeration;

public class MetaActorImpt {

	public final static int NUMBER = 6;
	public static int nodeNum = 5;
	public static String registering = "";
	public static HashMap map = new HashMap();

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

		public static class Separate implements Serializable {
			public List<ActorRef> ConstraintsList;
			public ActorRef[] workerActor;

			public Separate(ActorRef[] workerActor,
					List<ActorRef> ConstraintsList) {
				this.ConstraintsList = ConstraintsList;
				this.workerActor = workerActor;
			}

			// Separate Actors in ConstraintsList
			public static void separateActor(HashMap map,
					ActorRef[] workerActor, List<ActorRef> ConstraintsList) {
				System.out.println("#3 Separate Actors in ConstraintsList");

				Network net = new Network();
				IntVariable[] host = new IntVariable[NUMBER];
				for (int i = 0; i < NUMBER; i++) {
					host[i] = new IntVariable(net, 1, NUMBER, workerActor[i]
							.path().name());
					System.out.println(host[i]);
				}
				new NotEquals(net, host);

				Solver solver = new DefaultSolver(net);
				int count = 0;
				for (solver.start(); solver.waitNext(); solver.resume()) {
					Solution solution = solver.getSolution();
					count++;
					System.out.println("Solution " + count);
					for (int node = 1; node <= nodeNum; node++) {
						System.out.println("\tNode " + node + ": "
								+ find(node, host, solution));
					}
					
			
					System.out.println();
				}

				Iterator<ActorRef> iterator_constraint = ConstraintsList
						.iterator();
				while (iterator_constraint.hasNext()) {
					System.out
							.println(iterator_constraint.next().path().name());
				}

				// print out the old map
				System.out.print("#4 print out the old map\n");
				Set map_ety = map.entrySet();
				for (Iterator iter = map_ety.iterator(); iter.hasNext();) {
					Map.Entry ety = (Map.Entry) iter.next();				
					System.out.println("----->" + ety.getValue());
				}

			}

		}

		public static class Collocate implements Serializable {
			public List<ActorRef> ConstraintsList;
			public ActorRef[] workerActor;

			public Collocate(ActorRef[] workerActor,
					List<ActorRef> ConstraintsList) {
				this.ConstraintsList = ConstraintsList;
				this.workerActor = workerActor;
			}

			public static void collocateActor(HashMap map,
					ActorRef[] workerActor, List<ActorRef> ConstraintsList) {

				System.out
						.println("#3 Collocate constraints in ConstraintsList");

				Network net = new Network();
				IntVariable[] host = new IntVariable[NUMBER];
				for (int i = 0; i < NUMBER; i++) {
					host[i] = new IntVariable(net, 1, NUMBER, workerActor[i]
							.path().name());
					System.out.println(host[i]);

				}
				new NotEquals(net, host);
				Solver solver = new DefaultSolver(net);
				int count = 0;
				for (solver.start(); solver.waitNext(); solver.resume()) {
					Solution solution = solver.getSolution();
					count++;
					System.out.println("Solution " + count);
					for (int node = 1; node <= nodeNum; node++) {
						System.out.println("\tNode " + node + ": "
								+ find(node, host, solution));
					}
					System.out.println();
				}

				Iterator<ActorRef> iterator_constraint = ConstraintsList
						.iterator();
				while (iterator_constraint.hasNext()) {
					System.out
							.println(iterator_constraint.next().path().name());
				}

				// print out the old map
				System.out.print("#4 print out the old map\n");
				Set map_ety = map.entrySet();
				for (Iterator iter = map_ety.iterator(); iter.hasNext();) {
					Map.Entry ety = (Map.Entry) iter.next();
					System.out.println("----->" + ety.getValue());
				}
			}
		}

		public void onReceive(Object message) throws Exception {
			if (message instanceof Register) {
				registering = "Register " + ((Register) message).who;
				// add the Actor sender() into HashMap
				map.put(getSender().path(), getSender().path().name());
				System.out.print("\n" + registering);
			} else if (message instanceof Separate) {
				// Send the Separate greeting back to the sender
				System.out.print("\n Separate!\n" + getSender());
				Separate.separateActor(map,
						((Separate) message).workerActor,
						((Separate) message).ConstraintsList);

			} else if (message instanceof Collocate) {
				// Send the Collocate greeting back to the sender
				System.out.print("\n Collocate!\n" + getSender());
				Collocate.collocateActor(map,
						((Collocate) message).workerActor,
						((Collocate) message).ConstraintsList);

			} else
				unhandled(message);
		}

	}

	public static class CSPSolver {
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
			System.out.println("#1.2print out the map");
			System.out.println(map);
			System.out.println();

			System.out.println("#2 all constraints in ConstraintsList");
			Iterator<ActorRef> iterator_constraint = ConstraintsList.iterator();
			while (iterator_constraint.hasNext()) {
				System.out.println(iterator_constraint.next());
			}

			// recreate the newMap with the constraints.

		}

	}

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

	public static ActorRef[] crtRegularActors(ActorSystem system,
			ActorRef metaActor, int num) {

		final ActorRef workerActor[] = new ActorRef[num];
		String[] workerNameList = new String[num];
		for (int i = 0; i < num; i++) {
			workerNameList[i] = "Worker" + i;
			workerActor[i] = system.actorOf(Props.create(WorkerActor.class),
					workerNameList[i]);
			metaActor.tell(new MetaActorImpt.MetaActor.Register(
					workerNameList[i]), workerActor[i]);

		}

		return workerActor;

	}

	public static ActorRef[] crtMetaActor() {

		return null;

	}

	public static void main(String[] args) throws Exception {

		// Create the 'helloakka' actor system
		final ActorSystem system = ActorSystem.create("helloakka");
		// Create the MetaActor
		final ActorRef metaActor = system.actorOf(
				Props.create(MetaActor.class), "metaActor");
		ActorRef[] workerActors = crtRegularActors(system, metaActor, NUMBER);

		// Separate the actors based on ConstraintsList
		List<ActorRef> ConstraintsList = Arrays.asList(new ActorRef[] {
				workerActors[5], workerActors[3], workerActors[2] });
		metaActor.tell(new MetaActorImpt.MetaActor.Separate(workerActors,
				ConstraintsList), workerActors[2]);

		// Collocate the actors based on ConstraintsList
		metaActor.tell(new MetaActorImpt.MetaActor.Collocate(workerActors,
				ConstraintsList), workerActors[2]);

		// List<ActorRef> ConstraintsList = Arrays.asList(new ActorRef[] {
		// democratActor[5], republicanActor[3], workerActor[2] });
		//
		// metaActor.tell(new MetaActorImpt.MetaActor.Separate(ConstraintsList),
		// workerActor[2]);

	}

	public static void main_2(String[] args) throws Exception {

		String greeting = "";
		// Create the 'helloakka' actor system
		final ActorSystem system = ActorSystem.create("helloakka");
		// Create the MetaActor
		final ActorRef metaActor = system.actorOf(
				Props.create(MetaActor.class), "metaActor");

		final ActorRef democratActor[] = new ActorRef[NUMBER];
		String[] democratNameList = new String[NUMBER];
		for (int i = 0; i < NUMBER; i++) {
			democratNameList[i] = "democrate" + i;
			democratActor[i] = system.actorOf(
					Props.create(DemocratActor.class), democratNameList[i]);
			metaActor.tell(new MetaActorImpt.MetaActor.Register(
					democratNameList[i]), democratActor[i]);
		}

		final ActorRef republicanActor[] = new ActorRef[NUMBER];
		String[] republicanNameList = new String[NUMBER];
		for (int i = 0; i < NUMBER; i++) {
			republicanNameList[i] = "republican" + i;
			republicanActor[i] = system.actorOf(
					Props.create(RepublicanActor.class), republicanNameList[i]);
			metaActor.tell(new MetaActorImpt.MetaActor.Register(
					republicanNameList[i]), republicanActor[i]);
		}

		ActorRef[] workerActor = crtRegularActors(system, metaActor, NUMBER);
		// Create several helloActors
		final ActorRef helloActor[] = new ActorRef[NUMBER];
		String[] helloNameList = new String[NUMBER];
		for (int i = 0; i < NUMBER; i++) {
			helloNameList[i] = "hello" + i;
			helloActor[i] = system.actorOf(
					Props.create(HelloAkkaJava.HelloActor.class),
					helloNameList[i]);
			metaActor.tell(new MetaActorImpt.MetaActor.Register(
					helloNameList[i]), helloActor[i]);
		}
		// Create World Actor
		final ActorRef worldActor[] = new ActorRef[NUMBER];
		String[] worldNameList = new String[NUMBER];
		for (int i = 0; i < NUMBER; i++) {
			worldNameList[i] = "world" + i;
			worldActor[i] = system.actorOf(
					Props.create(WorldAkkaJava.World.class), worldNameList[i]);
			metaActor.tell(new MetaActorImpt.MetaActor.Register(
					worldNameList[i]), worldActor[i]);
		}

		Network net = new Network();
		IntVariable[] host = new IntVariable[NUMBER];
		IntVariable[] computation = new IntVariable[NUMBER];
		IntVariable[] data = new IntVariable[NUMBER];
		IntVariable[] domain = new IntVariable[NUMBER];
		IntVariable[] ext = new IntVariable[NUMBER];

		for (int i = 0; i < NUMBER; i++) {
			host[i] = new IntVariable(net, 1, NUMBER, workerActor[i].path()
					.name());
			System.out.println(host[i]);

		}
		for (int i = 0; i < NUMBER; i++) {
			computation[i] = new IntVariable(net, 1, NUMBER, democratActor[i]
					.path().name());

		}
		for (int i = 0; i < NUMBER; i++) {
			data[i] = new IntVariable(net, 1, NUMBER, republicanActor[i].path()
					.name());
		}
		for (int i = 0; i < NUMBER; i++) {
			domain[i] = new IntVariable(net, 1, NUMBER, helloActor[i].path()
					.name());
			// domain[i].equals(i);
		}
		for (int i = 0; i < NUMBER; i++) {
			ext[i] = new IntVariable(net, 1, NUMBER, worldActor[i].path()
					.name());
			// ext[i].equals(i);
		}

		new NotEquals(net, host);
		new NotEquals(net, computation);
		new NotEquals(net, data);
		new NotEquals(net, domain);
		new NotEquals(net, ext);

		// The host 1-n lives in the Node1 to Node-n.

		host[0].equals(1);
		host[1].equals(2);
		host[2].equals(3);
		host[3].equals(4);
		host[4].equals(5);

		domain[0].equals(1);
		domain[1].equals(2);
		domain[2].equals(3);
		domain[3].equals(4);
		domain[4].equals(5);

		// The extension e1-e5 lives in the Node1 to Node5.
		ext[0].equals(1);
		ext[1].equals(2);
		ext[2].equals(3);
		ext[3].equals(4);
		ext[4].equals(5);

		// The input constraints
		data[4].notEquals(1);
		computation[1].notEquals(1);
		// The b1 lives in the a1 node.
		computation[0].equals(host[0]);
		// The computation b3 need data c2.
		computation[2].equals(data[1]);
		// The b5 domains d5.
		computation[4].equals(domain[4]);
		// the a2 node need data c1.
		data[0].equals(host[1]);
		// the a5 node need data c3.
		data[4].equals(host[2]);

		Solver solver = new DefaultSolver(net);
		int count = 0;
		for (solver.start(); solver.waitNext(); solver.resume()) {
			Solution solution = solver.getSolution();
			count++;
			System.out.println("Solution " + count);
			for (int node = 1; node <= NUMBER; node++) {
				System.out.println("\tNode " + node + ": "
						+ find(node, host, solution) + ", "
						+ find(node, computation, solution) + ", "
						+ find(node, data, solution) + ", "
						+ find(node, domain, solution) + ", "
						+ find(node, ext, solution));
			}
			System.out.println();
		}

		// List<ActorRef> ConstraintsList = Arrays.asList(new ActorRef[] {
		// democratActor[5], republicanActor[3], workerActor[2] });
		//
		// metaActor.tell(new MetaActorImpt.MetaActor.Separate(ConstraintsList),
		// workerActor[2]);

		/*
		 * ConstraintsList = Arrays.asList(new ActorRef[] { democratActor,
		 * workerActor1[i] }); // Separate(HashMap map,List<ActorRef>
		 * ConstraintsList ) // Separate( metaActor, ConstraintsList );
		 * metaActor.tell(new MetaActorImpt.MetaActor.Separate(ConstraintsList),
		 * workerActor1[i]);
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

	public static void unUsedCode() {

		final ActorSystem system = ActorSystem.create("helloakka");
		// Create the MetaActor
		final ActorRef metaActor = system.actorOf(
				Props.create(MetaActor.class), "metaActor");

		// ActorRef roundRobinRouter = system.actorOf(
		// Props.create(PrintlnActor.class).withRouter(
		// new RoundRobinRouter(10)), "router");
		// for (int i = 0; i <= 10; i++) {
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

		// Separate(HashMap map,List<ActorRef> ConstraintsList );

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
}
