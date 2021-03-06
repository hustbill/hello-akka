package client;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinRouter;
import client.Time;
import scala.collection.mutable.ArraySeq;

import java.util.ArrayList;

public class Master extends UntypedActor {

    private long messages = 100;
    private ActorRef workerRouter;
    private final Time time = new Time();
    private ArrayList list = new ArrayList();

    public Master() {
        workerRouter = this.getContext().actorOf(Worker.createWorker().withRouter(new RoundRobinRouter(8)), "workerRouter");
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof Calculate) {
            time.start();
            processMessages();
        } else if (message instanceof Result) {
            list.add(((Result) message).getFactorial());
            if (list.size() == messages)
                end();
        } else {
            unhandled(message);
        }
    }

    private void processMessages() {
        for (int i = 0; i < messages; i++) {
            workerRouter.tell(new Work(), getSelf());
            System.out.print("\n" + workerRouter +" repsonse " );
        }
    }

    private void end() {
        time.end();
        System.out.println("Done: " + time.elapsedTimeMilliseconds() + " Milliseconds");
        getContext().system().shutdown();
    }

    public static Props createMaster() {
        return Props.create(Master.class, new ArraySeq<Object>(0));
    }
}
