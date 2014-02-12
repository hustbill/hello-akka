package metaActor;

import java.util.*;

import akka.actor.*;
import akka.dispatch.Dispatchers;
import akka.routing.CustomRoute;
import akka.routing.CustomRouterConfig;
import akka.routing.Destination;
import akka.routing.RouteeProvider;


public class VoteCountRouter extends CustomRouterConfig {

	public enum Message {
		DemocratVote, DemocratCountResult, RepublicanVote, RepublicanCountResult
	}

	@Override
	public String routerDispatcher() {
		return Dispatchers.DefaultDispatcherId();
	}

	@Override
	public SupervisorStrategy supervisorStrategy() {
		return SupervisorStrategy.defaultStrategy();
	}

	// crRoute ...
	// The router created in this example is a simple vote counter.
	// It will route the votes to specific vote counter actors.
	// In this case we only have two parties the Republicans and the Democrats.
	// We would like a router that forwards all
	// democrat related messages to the Democrat actor
	// and all republican related messages to the Republican actor.
	@Override
	public CustomRoute createCustomRoute(RouteeProvider routeeProvider) {
		final ActorRef democratActor = routeeProvider.context().actorOf(
				Props.create(DemocratActor.class), "d");
		final ActorRef republicanActor = routeeProvider.context().actorOf(
				Props.create(RepublicanActor.class), "r");
		List<ActorRef> routees = Arrays.asList(new ActorRef[] { democratActor,
				republicanActor });

		routeeProvider.registerRoutees(routees);

		return new CustomRoute() {
			@Override
			public scala.collection.immutable.Seq<Destination> destinationsFor(
					ActorRef sender, Object msg) {
				switch ((Message) msg) {
				case DemocratVote:
				case DemocratCountResult:
					return akka.japi.Util
							.immutableSingletonSeq(new Destination(sender,
									democratActor));
				case RepublicanVote:
				case RepublicanCountResult:
					return akka.japi.Util
							.immutableSingletonSeq(new Destination(sender,
									republicanActor));
				default:
					throw new IllegalArgumentException("Unknown message: "
							+ msg);
				}
			}
		};
	}

	
}
