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
//		IntVariable red = new IntVariable(net, 1, houses, "red");
//		IntVariable green = new IntVariable(net, 1, houses, "green");
//		IntVariable ivory = new IntVariable(net, 1, houses, "ivory");
//		IntVariable yellow = new IntVariable(net, 1, houses, "yellow");
//		IntVariable blue = new IntVariable(net, 1, houses, "blue");
//
//		IntVariable[] color = {
//				red, green, ivory, yellow, blue//, blue3, blue4
//		};

	
		int nodeNum = 55;
//		//new NotEquals(net, color);
//		for(int i=0; i< nodeNum; i++) {
//			net.add(color[i]);
//		}
		
		int NUMBER = 100;  // the number of actors
		String[] actorList = new String[NUMBER];
		   IntVariable[] actorVarArr = new IntVariable[NUMBER];
		for(int i=0; i< NUMBER; i++) {
			actorList[i] = "actor" + i ;
			actorVarArr[i] = new IntVariable(net, 1, nodeNum, actorList[i]);
		   net.add(actorVarArr[i]);
		}
//		
//		new NotEquals(net,actorVarArr[1], actorVarArr[2] );
//		
//		new NotEquals(net,actorVarArr[2], actorVarArr[3] );
//		new NotEquals(net,actorVarArr[3], actorVarArr[4] );
//		new NotEquals(net,actorVarArr[5], actorVarArr[6] );
//		new NotEquals(net,actorVarArr[7], actorVarArr[8] );
//		new NotEquals(net,actorVarArr[9], actorVarArr[4] );
//		
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
//		new NotEquals(net,actorVarArr[9], actorVarArr[4] );
//		actorVarArr[8].equals(actorVarArr[9]);
//		
//		actorVarArr[2].equals(actorVarArr[9]);
//		actorVarArr[4].equals(actorVarArr[7]);
//		actorVarArr[6].equals(actorVarArr[9]);
		
		
//	   Network net2 = new Network();
//		Constraint c = new Constraint();
//	   net.add(c);

	    
//		new NotEquals(net, nationality);
//		new NotEquals(net, drink);
//		new NotEquals(net, smoke);
//		new NotEquals(net, pet);
//		// The Englishman lives in the red house.
//		englishman.equals(red);
//		// The Spaniard owns the dog.
//		spaniard.equals(dog);
//		// Coffee is drunk in the green house.
//		coffee.equals(green);
//		// The Ukrainian drinks tea.
//		ukrainian.equals(tea);
//		// The green house is immediately to the right of the ivory house.
//		rightOf(green, ivory);
//		// The Old Gold smoker owns snails.
//		oldGold.equals(snails);
//		// Kools are smoked in the yellow house.
//		kools.equals(yellow);
//		// Milk is drunk in the middle house.
//		milk.equals(3);
//		// The Norwegian lives in the first house.
//		norwegian.equals(1);
//		// The man who smokes Chesterfields lives in the house next to the man with the fox.
//		nextTo(chesterfields, fox);
//		// Kools are smoked in the house next to the house where the horse is kept.
//		nextTo(kools, horse);
//		// The Lucky Strike smoker drinks orange juice.
//		luckyStrike.equals(orangeJuice);
//		// The Japanese smokes Parliaments.
//		japanese.equals(parliaments);
//		// The Norwegian lives next to the blue house.
//		nextTo(norwegian, blue);

//		Solver solver = new DefaultSolver(net);
//		count = 0;
//		for (solver.start(); solver.waitNext(); solver.resume()) {
//			Solution solution = solver.getSolution();
//			count++;
//		//System.out.println("Solution " + count);
//			//System.out.println(solution);
//			if(count >=8000) {
//				solver.stop();
//				System.out.println("Solution " + count);
//				System.out.println(solution.toString());
//			}
//		
//			for (int house = 1; house <= houses; house++) {
//				System.out.println("\tHouse " + house
//						+ ": " + find(house, color, solution)
////						+ ", " + find(house, nationality, solution)
////						+ ", " + find(house, drink, solution)
////						+ ", " + find(house, smoke, solution)
////						+ ", " + find(house, pet, solution)
//						);
//			}
//			System.out.println();
//		}
		
		runExample(net); //output the result.
	}

}
