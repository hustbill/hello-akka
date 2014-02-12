package metaActor;

import akka.actor.UntypedActor;

public class WorkerActor extends UntypedActor {
	  int counter = 0;
	  enum Message {
		  WorkerVote, WorkerCountResult, RepublicanVote, RepublicanCountResult
		}
	 
	  public void onReceive(Object msg) {
	    switch ((Message) msg) {
	    case WorkerVote:
	      counter++;
	      break;
	    case WorkerCountResult:
	      getSender().tell(counter, getSelf());
	      break;
	    default:
	      unhandled(msg);
	    }
	  }
	}
	 



/*package metaActor;

import akka.actor.UntypedActor;
import client.CalculateFactorial;
import java.math.BigInteger;

public class Worker extends UntypedActor {

	public class Result {

		private BigInteger bigInt;

		public Result(BigInteger bigInt) {
			this.bigInt = bigInt;
		}

		public BigInteger getFactorial() {
			return this.bigInt;
		}
	}

	public static class Work {
	}

	@Override
	public void onReceive(Object message) {
		if (message instanceof Work) {
			BigInteger bigInt = new CalculateFactorial().calculate();
			getSender().tell(new Result(bigInt), getSelf());

		}

		else
			unhandled(message);
	}

//	public static Props createWorker() {
//
//		return Props.create(Worker.class, new ArraySeq<Object>(0));
//	}
}
*/