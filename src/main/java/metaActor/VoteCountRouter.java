package metaActor;

import akka.actor.*;
import akka.dispatch.Dispatchers;
import akka.routing.CustomRoute;
import akka.routing.CustomRouterConfig;
import akka.routing.RouteeProvider;

public class VoteCountRouter extends CustomRouterConfig {
	  
	  @Override public String routerDispatcher() {
	    return Dispatchers.DefaultDispatcherId();
	  }
	  
	  @Override public SupervisorStrategy supervisorStrategy() {
	    return SupervisorStrategy.defaultStrategy();
	  }

	 
	  // crRoute ...
	@Override
	public CustomRoute createCustomRoute(RouteeProvider routeeProvider) {
	  final ActorRef democratActor =
	    routeeProvider.context().actorOf(Props.create(DemocratActor.class), "d");
	  final ActorRef republicanActor =
	    routeeProvider.context().actorOf(Props.create(RepublicanActor.class), "r");
	  List<ActorRef> routees =
	    Arrays.asList(new ActorRef[] { democratActor, republicanActor });
	 
	  routeeProvider.registerRoutees(routees);
	 
	  return new CustomRoute() {
	    @Override
	    public scala.collection.immutable.Seq<Destination> destinationsFor(
	        ActorRef sender, Object msg) {
	      switch ((Message) msg) {
	      case DemocratVote:
	      case DemocratCountResult:
	        return akka.japi.Util.immutableSingletonSeq(
	          new Destination(sender, democratActor));
	      case RepublicanVote:
	      case RepublicanCountResult:
	        return akka.japi.Util.immutableSingletonSeq(
	          new Destination(sender, republicanActor));
	      default:
	        throw new IllegalArgumentException("Unknown message: " + msg);
	      }
	    }
	  };
	}
	 
	}