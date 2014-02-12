package metaActor;

import akka.actor.UntypedActor;

public class PrintlnActor extends UntypedActor {
	public void onReceive(Object msg) {
		System.out.println(String.format(
				"Received message '%s' in actor %s", msg, getSelf().path()
						.name()));
	}
}