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

	//create the regular actors with specific number
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
		// System.out.println("Found one solution!");
		//System.out.println("time = " + time + " milli seconds");
		// System.out.println("Found " + count + " solutions in " + time
		// + " milli seconds");
		// System.out.println("# Problem");
		// System.out.println(net);
		// System.out.println();
		return time;
	}
	
	public static void main(String[] args) throws Exception {

		// Do experiments
		
		
	  	ActorRef[] workerActors;
		final ActorSystem system = ActorSystem.create("verifyCSP");
		// Create the MetaActor
		final ActorRef metaActor = system.actorOf(
				Props.create(MetaActor.class), "metaActor");
		
		
				int nRepeat = 6; // repeat times
				//int[] numOfActors = { 100, 200, 300, 500, 700 };
//				int[] numOfActors = {400,  500, 600, 700, 800, 900, 1000};
//				//int[] numOfNodes = { 80, 200, 280, 350, 400 };
//				int[] numOfNodes = {200, 280, 350, 400, 500, 600, 700 };
	
				int[] numOfConstraints= {1000, 2000, 3000, 4000, 5000, 6000, 7000 };
				
				
				int[] numOfNodes = {200, 300, 400, 500, 600 };
				int[] densityArr= {1, 2 , 3, 4, 5 , 6 };
								
				int[] numOfActors = new int[numOfNodes.length];
			
				for(int i=0; i< densityArr.length; i++) {
					System.out.print("\n\n densityArr["+i + "]= " + densityArr[i]);
					for(int j=0; j< numOfActors.length; j++){
						numOfActors[j] = densityArr[i] * numOfNodes[j];
						System.out.print("\n\n nodes["+j + "]= " + numOfNodes[j]);
						System.out.print("\t Actors["+j + "]= " + numOfActors[j]);
						workerActors = crtRegularActors(system, metaActor,
								numOfActors[j]);
						for(int k=0; k< numOfConstraints.length; k++) {
							createConstraintList(numOfActors[j],
								numOfNodes[j] ,numOfConstraints[k], workerActors);
						}

					}
				}
				
				
				// int[] numConstraints = getConstraintsNum( numActors);
				long[] timeArr = new long[nRepeat];

				int firstArg =0 ;
				if(args.length > 0 ) {
				 try {
				        firstArg = Integer.parseInt(args[0]);
					    } catch (NumberFormatException e) {
					        System.err.println("Argument" + args[0] + " must be an integer.");
					        System.exit(1);
					    }
				 
//				 int actorNum = Integer.parseInt(args[1]);
//				 int nodeNum = Integer.parseInt(args[2]);
//				 int constraintNum = Integer.parseInt(args[3]);
				
				
//				switch(firstArg) {
//					case 1:
//						System.out.println("Experiment : fixed actors");
//					
//						verifyCSP1(actorNum, nodeNum, constraintNum, workerActors);
//						break;
//					case 2:
//						System.out.println("Experiment: fixed notes");
//						workerActors = crtRegularActors(system, metaActor,
//								actorNum);
//						verifyCSP2(actorNum, nodeNum, constraintNum, workerActors);
//						break;      
//					case 3:
//						System.out.println("Experiment: actors/notes radio");
//						workerActors = crtRegularActors(system, metaActor,
//								actorNum);
//						verifyCSP3(actorNum, numOfNodes, constraintNum, workerActors);
//						break;
//					default:
//						System.out.println("The first arg should be 0, 1 or 2! ");
//	                    break;
//				}
				 System.exit(1);
			}
	}


	
	//Experiment series one  - fixed actors
	public static void verifyCSP1(int actorNum, int nodeNum, int constraintNum, ActorRef[] workerActors ) throws Exception {
	
		System.out.println("Experiment 1: fixed actors " + "actors= \t" + actorNum);
		Network net =  createConstraintList( actorNum,
				nodeNum ,  constraintNum , workerActors);	
	}

	//Experiment series two  - fixed nodes
	public static void verifyCSP2(int actorNum, int nodeNum, int constraintNum, ActorRef[] workerActors) throws Exception {
		System.out.println("Experiment 2: fixed nodes " + "nodes= \t" + nodeNum);
		Network net =  createConstraintList( actorNum,
				nodeNum ,  constraintNum, workerActors);	
	}

	//fixed radio = actors/nodes 
	public static void verifyCSP3(int actorNum, int[] numOfNodes, int constraintNum, ActorRef[] workerActors ) throws Exception {
		System.out.println("Experiment 3: fixed radio " + "actors/nodes= \t" + actorNum/nodeNum);
		//int[] numOfNodes = { 80, 200, 280, 350, 400 };
		//int[] numOfNodes = {  100, 200, 300, 400, 500, 600, 700 };
		int size = numOfNodes.length;
		int[] numOfActors= new int[size];
		int[] d= { 1, 2 , 3 , 4, 5};
		for(int j=0; j< d.length; j++) {
		for(int i=1; i< numOfNodes.length; i++) {
			
				numOfActors[i] = d[j] * numOfNodes[i];
				Network net =  createConstraintList( numOfActors[i],
						numOfNodes[i] ,  constraintNum, workerActors);	
				
			}
			
		}
		
	
	}
	
	//create the random -
	/*
	 * random constraints:  0- separate,  1- Collocate
	 * usage: create the random constraints for actors 
	 * 
	 * 
	 */
	
	public static Network  createConstraintList(int actorNum,
			int nodeNum , int constraintNum, ActorRef[] workerActors) {
		//ArrayList<Arralylist, String>
		Network net = new Network();		
		IntVariable[] actorVarArr = new IntVariable[actorNum];
		
		for (int i = 0; i < actorNum; i++) {
			actorVarArr[i] = new IntVariable(net, 1, nodeNum,
					workerActors[i].path().name());
			net.add(actorVarArr[i]);
		}
		
		int countOfConstraints =0;
		 for (int i = 1; i <= constraintNum ; i++)
	       {
	        int Random1 = (int)(Math.random()*actorNum);
	        int Random2  = (int)(Math.random()*actorNum);
	        if (Random1 != Random2) 
	        	//System.out.println(Random1 + " " + Random2);
	        	new NotEquals(net, actorVarArr[Random1], actorVarArr[Random2]);
	        	countOfConstraints++;
	       }
	
		System.out.print("\n Constraints Number= " + countOfConstraints);
		long time = runExample(net); // output the result.
		System.out.print("\t time = " + time + " milli seconds");
		

		return net; 
        
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
