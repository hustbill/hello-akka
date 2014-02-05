import java.io.Serializable;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class MetaActorImpt {

	public static class MetaActor extends UntypedActor {
		public static class Greet implements Serializable {
			public final String who;

			public Greet(String who) {
				this.who = who;
			}

		}

		public static class WhoToGreet implements Serializable {
			public final String who;

			public WhoToGreet(String who) {
				this.who = who;
			}
		}

		public static class Greeting implements Serializable {
			public final String message;

			public Greeting(String message) {
				this.message = message;
			}
		}

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

		public void onReceive(Object message) throws Exception {
			if (message instanceof Register) {
				registering = "Register, " + ((Register) message).who;
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
			
			public static void allocate(String[] args) {
				Network net = new Network();
				IntVariable a1 = new IntVariable(net, 1, nodes, "a1");
				IntVariable a2 = new IntVariable(net, 1, nodes, "a2");
				IntVariable a3 = new IntVariable(net, 1, nodes, "a3");
				IntVariable a4 = new IntVariable(net, 1, nodes, "a4");
				IntVariable a5 = new IntVariable(net, 1, nodes, "a5");
				IntVariable[] host = {
						a1, a2, a3, a4, a5
				};
				IntVariable b1 = new IntVariable(net, 1, nodes, "b1");
				IntVariable b2 = new IntVariable(net, 1, nodes, "b2");
				IntVariable b3 = new IntVariable(net, 1, nodes, "b3");
				IntVariable b4 = new IntVariable(net, 1, nodes, "b4");
				IntVariable b5 = new IntVariable(net, 1, nodes, "b5");
				IntVariable[] computation = {
						b1, b2, b3, b4, b5
				};
				IntVariable c1 = new IntVariable(net, 1, nodes, "c1");
				IntVariable c2 = new IntVariable(net, 1, nodes, "c2");
				IntVariable c3 = new IntVariable(net, 1, nodes, "c3");
				IntVariable c4 = new IntVariable(net, 1, nodes, "c4");
				IntVariable c5 = new IntVariable(net, 1, nodes, "c5");
				IntVariable[] data = {
						c1, c2, c3, c4, c5
				};
				IntVariable d1 = new IntVariable(net, 1, nodes, "d1");
				IntVariable d2 = new IntVariable(net, 1, nodes, "d2");
				IntVariable d3 = new IntVariable(net, 1, nodes, "d3");
				IntVariable d4 = new IntVariable(net, 1, nodes, "d4");
				IntVariable d5 = new IntVariable(net, 1, nodes, "d5");
				IntVariable[] domain = {
						d1, d2, d3, d4, d5
				};
				IntVariable e1 = new IntVariable(net, 1, nodes, "e1");
				IntVariable e2 = new IntVariable(net, 1, nodes, "e2");
				IntVariable e3 = new IntVariable(net, 1, nodes, "e3");
				IntVariable e4 = new IntVariable(net, 1, nodes, "e4");
				IntVariable e5 = new IntVariable(net, 1, nodes, "e5");
				IntVariable[] ext = {
						e1, e2, e3, e4, e5 
				};
				new NotEquals(net, host);
				new NotEquals(net, computation);
				new NotEquals(net, data);
				new NotEquals(net, domain);
				new NotEquals(net, ext);
				
				
				/* The original solution 
				Node 1: a1, b2, c5, d1, e1
				Node 2: a2, b4, c1, d2, e2
				Node 3: a3, b3, c2, d3, e3
				Node 4: a4, b1, c4, d4, e4
				Node 5: a5, b5, c3, d5, e5
				*/
				c5.notEquals(1);
				b2.notEquals(1);
						
				// The host a1-a5 lives in the Node1 to Node5.
				a1.equals(1);
				a2.equals(2);
				a3.equals(3);
				a4.equals(4);
				a5.equals(5);
				
				// The Node1-Node5 lives in the domain d1-d5 .
				d1.equals(1);
				d2.equals(2);
				d3.equals(3);
				d4.equals(4);
				d5.equals(5);
				
				// The extension e1-e5 lives in the Node1 to Node5.
				e1.equals(1);
				e2.equals(2);
				e3.equals(3);
				e4.equals(4);
				e5.equals(5);
				
				//The input constraints 
				// The b1 lives in the a1 node.
				b1.equals(a1);
				// The computation b3 need data c2.
				b3.equals(c2);
				// The b5 domains d5.
				b5.equals(d5);		    
				// the a2 node need data c1.
				c1.equals(a2);
				// the a5 node need data c3.
				c3.equals(a5);
		
				Solver solver = new DefaultSolver(net);
				int count = 0;
				for (solver.start(); solver.waitNext(); solver.resume()) {
					Solution solution = solver.getSolution();
					count++;
					System.out.println("Solution " + count);
					for (int node = 1; node <= nodes; node++) {
						System.out.println("\tNode " + node
								+ ": " + find(node, host, solution)
								+ ", " + find(node, computation, solution)
								+ ", " + find(node, data, solution)
								+ ", " + find(node, domain, solution)
								+ ", " + find(node, ext, solution)
								);
					}
					System.out.println();
				}
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
		String[] args1 = {"helloActor", "worldActor"};
		CSPSolver.allocate(args1);

	}

}
