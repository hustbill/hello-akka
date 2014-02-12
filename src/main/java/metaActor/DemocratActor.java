package metaActor;

import akka.actor.UntypedActor;

public class DemocratActor extends UntypedActor {
	  int counter = 0;
	  enum Message {
		  DemocratVote, DemocratCountResult, RepublicanVote, RepublicanCountResult
		}
	 
	  public void onReceive(Object msg) {
	    switch ((Message) msg) {
	    case DemocratVote:
	      counter++;
	      break;
	    case DemocratCountResult:
	      getSender().tell(counter, getSelf());
	      break;
	    default:
	      unhandled(msg);
	    }
	  }
	}
	 
