package metaActor;

import akka.actor.UntypedActor;

public class RepublicanActor extends UntypedActor {
	  int counter = 0;
	  enum Message {
		  DemocratVote, DemocratCountResult, RepublicanVote, RepublicanCountResult
		}
	 
	 
	  public void onReceive(Object msg) {
	    switch ((Message) msg) {
	    case RepublicanVote:
	      counter++;
	      break;
	    case RepublicanCountResult:
	      getSender().tell(counter, getSelf());
	      break;
	    default:
	      unhandled(msg);
	    }
	  }
	}
	 
	