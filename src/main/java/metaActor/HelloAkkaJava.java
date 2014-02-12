package metaActor;
import java.io.Serializable;
import akka.actor.UntypedActor;

public class HelloAkkaJava {
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
    
    static class Calculate {
    	
    }
    
    static class Result{
    	private final double value;
    	public Result(double value) {
    		this.value = value;
    	}
    	public double getValue(){
    		return value;
    	}
    }

    public static class HelloActor extends UntypedActor {
        String greeting = "";

        public void onReceive(Object message) {
            if (message instanceof WhoToGreet){
                greeting = "\n"+ getSelf() +" say hello to " + ((WhoToGreet) message).who;
                System.out.print("\n" +greeting);
            }
            else if (message instanceof Greet){
                // Send the current greeting back to the sender
                getSender().tell(new Greeting(greeting), getSelf());
                 System.out.print("\n" + getSelf() +" repsonse to " + getSender());
                 
            }
            else if (message instanceof Calculate) {
            	
            }
            else unhandled(message);
        }
        public void printOut(Object message) {
        	System.out.print(message);
        }
    }

	private static Object msg="test555";

  
    public static class GreetPrinter extends UntypedActor {
        public void onReceive(Object message) {
            if (message instanceof Greeting)
                System.out.println(((Greeting) message).message);
        }
    }
}
