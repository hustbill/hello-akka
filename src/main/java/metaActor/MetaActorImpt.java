package metaActor;

import java.io.Serializable;
import java.util.*;

import org.junit.Test;

import scala.actors.Future;
import scala.concurrent.duration.*;
import CSP.*;
import akka.actor.ActorPath;
import akka.actor.Address;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.AddressFromURIString;
import akka.actor.Kill;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinRouter;
import akka.util.Timeout;

import java.util.Enumeration;

public class MetaActorImpt {

	public final static int NUMBER = 700;
	public static int nodeNum = 280;
	public static String registering = "";
	public static HashMap<ActorPath, String> map = new HashMap<ActorPath, String>();

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
			public static void separateActor(HashMap<ActorPath, String> map,
					ActorRef[] workerActor, List<ActorRef> ConstraintsList) {
				// System.out.println("#3 Separate Actors in ConstraintsList");

				// print out the old map
				// System.out.print("#4 print out the old map\n");
				// Set<?> map_ety = map.entrySet();
				// for (Iterator<?> iter = map_ety.iterator(); iter.hasNext();)
				// {
				// Map.Entry ety = (Map.Entry) iter.next();
				// // System.out.println("----->" + ety.getValue());
				// }
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

			public static void collocateActor(HashMap<ActorPath, String> map,
					ActorRef[] workerActor, List<ActorRef> ConstraintsList) {

				System.out
						.println("#3 Collocate constraints in ConstraintsList");
				Network net = new Network();
				// print out the old map
				System.out.print("#4 print out the map\n");
				Set<?> map_ety = map.entrySet();
				for (Iterator<?> iter = map_ety.iterator(); iter.hasNext();) {
					Map.Entry ety = (Map.Entry) iter.next();
					// System.out.println("----->" + ety.getValue());
				}
			}
		}

		public void onReceive(Object message) throws Exception {
			if (message instanceof Register) {
				registering = "Register " + ((Register) message).who;
				// add the Actor sender() into HashMap
				map.put(getSender().path(), getSender().path().name());
				// System.out.print("\n" + registering);
			} else if (message instanceof Separate) {
				// Send the Separate greeting back to the sender
				// System.out.print("\n Separate!\n" + getSender());
				Separate.separateActor(map, ((Separate) message).workerActor,
						((Separate) message).ConstraintsList);

			} else if (message instanceof Collocate) {
				// Send the Collocate greeting back to the sender
				System.out.print("\n Collocate!\n" + getSender());
				Collocate.collocateActor(map,
						((Collocate) message).workerActor,
						((Collocate) message).ConstraintsList);

			}  else if (message.equals("kill")) {
			      getContext().stop( getSender());
			      //lastSender = getSender();
			    } 
			
			else
				unhandled(message);
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
		long nowTime=System.currentTimeMillis();
		
		for (int i = 0; i < num; i++) {
			workerNameList[i] = "Worker" + nowTime + i;
			workerActor[i] = system.actorOf(Props.create(WorkerActor.class),
					workerNameList[i]);
			metaActor.tell(new MetaActorImpt.MetaActor.Register(
					workerNameList[i]), workerActor[i]);
		}
		return workerActor;
	}

	static long runExample(Network net) {
		Solver solver = new DefaultSolver(net);
		long timeout = 600; // 1 * 60 * 1000;
		//System.out.println("# Solutions");
		for (solver.start(); solver.waitNext(); solver.resume()) {
			Solution solution = solver.getSolution();
			// Solution solution = solver.findBest(timeout);
			//System.out.println(solution);
			solver.stop();
		}

		long count = solver.getCount();
		long time = solver.getElapsedTime();
		 System.out.println("Found one solution!");
		//System.out.println("time = " + time + " milli seconds");
		// System.out.println("Found " + count + " solutions in " + time
		// + " milli seconds");
		// System.out.println("# Problem");
		// System.out.println(net);
		// System.out.println();
		return time;
	}

	int getCountOfConstraints() {

		int countOfConstraints = 0;
		return countOfConstraints;

	}

	// Create the constraint list for specific number of Actors
	// for obtain Experiment data
	static List<ActorRef> getConstraintList(ActorRef[] workerActors,
			int numOfNodes) {

		int numOfActors = workerActors.length;

		List<ActorRef> ConstraintsList = Arrays.asList(new ActorRef[] {
				workerActors[5], workerActors[3], workerActors[2] });

		// System.out.println("#3 Separate Actors in ConstraintsList");

		Network net = new Network();
		IntVariable[] actorVarArr = new IntVariable[numOfActors];
		for (int i = 0; i < numOfActors; i++) {
			actorVarArr[i] = new IntVariable(net, 1, numOfNodes,
					workerActors[i].path().name());
			net.add(actorVarArr[i]);
		}

		int countOfConstraints = 0;
		// Separate Constraint List
		for (int i = 0; i < numOfActors / 5; i++) {
			for (int j = i + 1; j < numOfActors / 5; j++) {
				new NotEquals(net, actorVarArr[i], actorVarArr[j]);
				countOfConstraints++;
			}

		}
		// Separate constraints list.
		for (int i = numOfActors / 5; i < 2 * numOfActors / 5; i++) {
			for (int j = i + 1; j < 2 * numOfActors / 5; j++) {
				new NotEquals(net, actorVarArr[i], actorVarArr[j]);
				countOfConstraints++;
			}
		}
		// Separate constraints list.
		for (int i = 4 * numOfActors / 5; i < numOfActors; i++) {
			for (int j = i + 1; j < numOfActors - 1; j++) {
				new NotEquals(net, actorVarArr[i], actorVarArr[j]);
				countOfConstraints++;
			}
		}
		System.out.println("Constraints Number= " + countOfConstraints);
		long time = runExample(net); // output the result.
		System.out.println("time = " + time + " milli seconds\n");

		return ConstraintsList;

	}

	public static void main(String[] args) throws Exception {
		// Do experiments
		int nRepeat = 6; // repeat times
		//int[] numOfActors = { 100, 200, 300, 500, 700 };
		int[] numOfActors = { 400,  500, 600, 700, 800, 900, 1000};
		//int[] numOfNodes = { 80, 200, 280, 350, 400 };
		int[] numOfNodes = {  200, 280, 350, 400, 500, 600, 700 };
		// int[] numConstraints = getConstraintsNum( numActors);
		long[] timeArr = new long[nRepeat];

		// Create the 'helloakka' actor system
		final ActorSystem system = ActorSystem.create("helloakka");
		// Create the MetaActor
		final ActorRef metaActor = system.actorOf(
				Props.create(MetaActor.class), "metaActor");



		// Run experiment nRepeat times
		for (int i = 0; i < nRepeat; i++) {
			System.out.println("Experiment  "+ i + " :");
			System.out.println("numOfActors =" + numOfActors[i]);
			System.out.println("numOfNodes =" + numOfNodes[2]);
	
			ActorRef[] workerActors = crtRegularActors(system, metaActor,
					numOfActors[i]);
			// Separate the actors based on ConstraintsList
			List<ActorRef> ConstraintsList = getConstraintList(workerActors,
					numOfNodes[2]);

			metaActor.tell(new MetaActorImpt.MetaActor.Separate(workerActors,
					ConstraintsList), workerActors[2]);

			
			// Collocate the actors based on ConstraintsList
			// metaActor.tell(new
			// MetaActorImpt.MetaActor.Collocate(workerActors,
			// ConstraintsList), workerActors[2]);

			// List<ActorRef> ConstraintsList = Arrays.asList(new ActorRef[] {
			// democratActor[5], republicanActor[3], workerActor[2] });
			//
			// metaActor.tell(new
			// MetaActorImpt.MetaActor.Separate(ConstraintsList),
			// workerActor[2]);
		}

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
