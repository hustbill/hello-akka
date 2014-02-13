package metaActor;

import java.io.Serializable;

import akka.actor.*;
import akka.routing.RandomRouter;
import akka.routing.RoundRobinRouter;

public class FibonacciActor extends UntypedActor {

	public void onReceive(Object msg) {
		if (msg instanceof FibonacciNumber) {
			FibonacciNumber fibonacciNumber = (FibonacciNumber) msg;
			getSender().tell(fibonacci(fibonacciNumber.getNbr()), getSelf());

		/*	ActorRef roundRobinRouter = getContext().actorOf(
					Props.create(PrintlnActor.class).withRouter(
							new RoundRobinRouter(100)), "router");
			for (int i = 1; i <= 100; i++) {
				roundRobinRouter.tell(i, getSelf());
				//System.out.println("\n roundRobinRouter actors" + roundRobinRouter.path().name() +"\n");
			}
			System.out.println("\n radmonRouter actors\n");
*/
			/*ActorRef randomRouter = getContext().actorOf(
					Props.create(PrintlnActor.class).withRouter(
							new RandomRouter(5)), "routerRandom");
			for (int i = 11; i <= 20; i++) {
				randomRouter.tell(i, getSelf());
			}*/

		} else {
			unhandled(msg);
		}
	}

	private int fibonacci(int n) {
		return fib(n, 1, 0);
	}

	private int fib(int n, int b, int a) {
		if (n == 0)
			return a;
		// recursion
		return fib(n - 1, a + b, b);
	}

	public static class FibonacciNumber implements Serializable {
		private static final long serialVersionUID = 1L;
		private final int nbr;

		public FibonacciNumber(int nbr) {
			this.nbr = nbr;
		}

		public int getNbr() {
			return nbr;
		}

	}
}