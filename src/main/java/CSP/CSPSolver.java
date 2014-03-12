package CSP;
//This is the client program for CSPSolver
import java.io.Serializable;
import java.util.*;

import akka.actor.ActorRef;

//import MetaActorImpt.MetaActor.Register;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;


public class CSPSolver {
	
	static void runExample(Network net) {
		Solver solver = new DefaultSolver(net);
		long timeout = 600; // 1 * 60 * 1000;
		System.out.println("# Solutions");
		for (solver.start(); solver.waitNext(); solver.resume()) {
			//Solution solution = solver.getSolution();
			Solution solution = solver.getSolution();
			//Solution solution = solver.findBest(timeout);
			
			System.out.println(solution);
			solver.stop();
			
//			timeout--;
//			if(timeout ==0) {
//				System.out.println(solution);
//				solver.stop();
//			}
//			
		}
	
		long count = solver.getCount();
		long time = solver.getElapsedTime();
		System.out.println("time = " + time);
	    System.out.println("Found " + count + " solutions in " + time + " milli seconds");
		
		System.out.println("# Problem");
		System.out.println(net);

		
			
		System.out.println();
	}


	static void relationExample() {
		for(int i=0; i< 100;i++) {
		Network net = new Network();
		IntVariable x = new IntVariable(net,"x" );
		IntVariable y = new IntVariable(net, "y");
		boolean[][] rel = {
				{ false,  true, false, false },
				{ false, false, false,  true },
				{  true, false, false, false },
				{ false,  true, false, false },
				{ false, false, false,  true },
				{  true, false, false, false },
				{ false, false,  true, false }
		};
		//new Relation(net, x, rel, y);
		runExample(net);
		}
	}
	
	public static void main(String[] args) {
		relationExample();
	}
	
	static int houses = 5;
	
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
	
	public static void main(String[] args) {
		Network net = new Network();	
		int nodeNum = 55;		
		int NUMBER = 100;  // the number of actors
		String[] actorList = new String[NUMBER];
		   IntVariable[] actorVarArr = new IntVariable[NUMBER];
		for(int i=0; i< NUMBER; i++) {
			actorList[i] = "actor" + i ;
			actorVarArr[i] = new IntVariable(net, 1, nodeNum, actorList[i]);
		   net.add(actorVarArr[i]);
		}
		
		int count =0;
		int countOfConstraints =0;
		//Separate Constraint List
		for(int i=0 ;i < 10; i++) {
			for(int j=i+1 ; j < 20; j++) {
				new NotEquals(net,actorVarArr[i], actorVarArr[j] );
				countOfConstraints++;
			}
			
		}
		//Collocate constraints list.
		for(int i=20 ;i < 50; i++) {
			for(int j=i+1 ; j < 50; j++) {				
				new NotEquals(net,actorVarArr[i], actorVarArr[j] );
				countOfConstraints++;
			}
		}
		//Collocate constraints list.
		for(int i=50 ;i < NUMBER; i++) {
			for(int j=i+1 ; j < NUMBER-1; j++) {				
				new NotEquals(net,actorVarArr[i], actorVarArr[j] );
					countOfConstraints++;
			}
		}
		System.out.println("Constraints Number= " + countOfConstraints);		
		runExample(net); //output the result.
	}
<<<<<<< HEAD
=======

	public static void main2(String[] args) {
		String[] problem = {"ft06", "ft10", "la40","swv20", "abz08", "ta44" };
		
		
		//problem ="ft06" ;
		//problem = "ft10";
//		problem = "la40";
//		problem = "swv20";
//		problem = "abz08";
//		problem = "ta44";
		
		String name = "bb"; //backtracking

		long timeout = 10 * 60 * 1000;
		String[] solverNames = { "bb" };
		int i = 0;
//		if (i < args.length)
//			problem = args[i++];
		if (i < args.length)
			timeout = Integer.parseInt(args[i++]) * 1000;
		
		for(int k=1; k< 20; k++) {  // run k times
			 System.out.println("----------Run k=" + k +" times----------");
		for(int j=0; j< 6; j++) {
			Network network = jssp(problem[j]);
				if (network == null)
					return;
				int opt = Solver.MINIMIZE;
				Solver solver = null;		
			if (name.equals("bb")) {
				solver = new DefaultSolver((Network) network.clone(), opt, name);
			}	
			Solution solution = solver.findFirst();
			long time = solver.getElapsedTime();
			//System.out.println("First = " + solution.getObjectiveIntValue());
//		System.out.println("Start backtracking solve problem "+ problem[j] + ", time = " + time
//				+ " msecs");
		System.out.println(  problem[j] + ", time = " + time + " msecs");
		
		}
	 }
		
	}
//		
//		public static void main(String[] args) {
//			Network net = new Network();
//			IntVariable a1 = new IntVariable(net, 1, nodes, "a1");
//			IntVariable a2 = new IntVariable(net, 1, nodes, "a2");
//			IntVariable a3 = new IntVariable(net, 1, nodes, "a3");
//			IntVariable a4 = new IntVariable(net, 1, nodes, "a4");
//			IntVariable a5 = new IntVariable(net, 1, nodes, "a5");
//			IntVariable[] host = {
//					a1, a2, a3, a4, a5
//			};
//			IntVariable b1 = new IntVariable(net, 1, nodes, "b1");
//			IntVariable b2 = new IntVariable(net, 1, nodes, "b2");
//			IntVariable b3 = new IntVariable(net, 1, nodes, "b3");
//			IntVariable b4 = new IntVariable(net, 1, nodes, "b4");
//			IntVariable b5 = new IntVariable(net, 1, nodes, "b5");
//			IntVariable[] computation = {
//					b1, b2, b3, b4, b5
//			};
//			IntVariable c1 = new IntVariable(net, 1, nodes, "c1");
//			IntVariable c2 = new IntVariable(net, 1, nodes, "c2");
//			IntVariable c3 = new IntVariable(net, 1, nodes, "c3");
//			IntVariable c4 = new IntVariable(net, 1, nodes, "c4");
//			IntVariable c5 = new IntVariable(net, 1, nodes, "c5");
//			IntVariable[] data = {
//					c1, c2, c3, c4, c5
//			};
//			IntVariable d1 = new IntVariable(net, 1, nodes, "d1");
//			IntVariable d2 = new IntVariable(net, 1, nodes, "d2");
//			IntVariable d3 = new IntVariable(net, 1, nodes, "d3");
//			IntVariable d4 = new IntVariable(net, 1, nodes, "d4");
//			IntVariable d5 = new IntVariable(net, 1, nodes, "d5");
//			IntVariable[] domain = {
//					d1, d2, d3, d4, d5
//			};
//			IntVariable e1 = new IntVariable(net, 1, nodes, "e1");
//			IntVariable e2 = new IntVariable(net, 1, nodes, "e2");
//			IntVariable e3 = new IntVariable(net, 1, nodes, "e3");
//			IntVariable e4 = new IntVariable(net, 1, nodes, "e4");
//			IntVariable e5 = new IntVariable(net, 1, nodes, "e5");
//			IntVariable[] ext = {
//					e1, e2, e3, e4, e5 
//			};
//			new NotEquals(net, host);
//			new NotEquals(net, computation);
//			new NotEquals(net, data);
//			new NotEquals(net, domain);
//			new NotEquals(net, ext);
//			
//			
//			/* The original solution 
//			Node 1: a1, b2, c5, d1, e1
//			Node 2: a2, b4, c1, d2, e2
//			Node 3: a3, b3, c2, d3, e3
//			Node 4: a4, b1, c4, d4, e4
//			Node 5: a5, b5, c3, d5, e5
//			*/
//			c5.notEquals(1);
//			b2.notEquals(1);
//					
//			// The host a1-a5 lives in the Node1 to Node5.
//			a1.equals(1);
//			a2.equals(2);
//			a3.equals(3);
//			a4.equals(4);
//			a5.equals(5);
//			
//			// The Node1-Node5 lives in the domain d1-d5 .
//			d1.equals(1);
//			d2.equals(2);
//			d3.equals(3);
//			d4.equals(4);
//			d5.equals(5);
//			
//			// The extension e1-e5 lives in the Node1 to Node5.
//			e1.equals(1);
//			e2.equals(2);
//			e3.equals(3);
//			e4.equals(4);
//			e5.equals(5);
//			
//			//The input constraints 
//			// The b1 lives in the a1 node.
//			b1.equals(a1);
//			// The computation b3 need data c2.
//			b3.equals(c2);
//			// The b5 domains d5.
//			b5.equals(d5);		    
//			// the a2 node need data c1.
//			c1.equals(a2);
//			// the a5 node need data c3.
//			c3.equals(a5);
//	
//			Solver solver = new DefaultSolver(net);
//			int count = 0;
//			for (solver.start(); solver.waitNext(); solver.resume()) {
//				Solution solution = solver.getSolution();
//				count++;
//				System.out.println("Solution " + count);
//				for (int node = 1; node <= nodes; node++) {
//					System.out.println("\tNode " + node
//							+ ": " + find(node, host, solution)
//							+ ", " + find(node, computation, solution)
//							+ ", " + find(node, data, solution)
//							+ ", " + find(node, domain, solution)
//							+ ", " + find(node, ext, solution)
//							);
//				}
//				System.out.println();
//			}
//		}
//	
>>>>>>> ddf19d50b603a3fdbe6275ea970b9adcc127df4d
}
