
import java.io.Serializable;


import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
 
public class WorldAkkaJava {
    public static class Greet implements Serializable {
  	  public final String who;
        public Greet(String who) {
            this.who = who;
        }

  }
  public static class WhoToGreet implements Serializable {
      public final String who;
      public WhoToGreet(String who) {
          this.who = who;
      }
  }
  public static class Greeting implements Serializable {
      public final String message;
      public Greeting(String message) {
          this.message = message;
      }
  }

  
  public static class World extends UntypedActor {
      String greeting = "";
	  LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	  
      public void onReceive(Object message) {
          if (message instanceof WhoToGreet){
              greeting = "\n"+ getSelf() +" say world to " + ((WhoToGreet) message).who;
              System.out.print("\n" +greeting);
          }
          else if (message instanceof Greet){
              // Send the current greeting back to the sender
              getSender().tell(new Greeting(greeting), getSelf());
               System.out.print("\n" + getSelf() +" repsonse to " + getSender());
               
          }
          else unhandled(message);
      }
  }


  
}