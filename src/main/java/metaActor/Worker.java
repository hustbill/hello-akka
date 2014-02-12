package metaActor;

import akka.actor.Props;
import akka.actor.UntypedActor;
import client.CalculateFactorial;
import scala.collection.mutable.ArraySeq;
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

    public static Props createWorker() {
    	
        return Props.create(Worker.class, new ArraySeq<Object>(0));
    }
}
