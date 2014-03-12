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
	
//	public static void main(String[] args) {
//		relationExample();
//	}
	
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
}
