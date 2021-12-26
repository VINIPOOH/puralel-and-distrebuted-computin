import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class CarCreatorActor extends Thread {

    private final ActorSystem system;
    private final ActorRef[] parkPlaces;

    public CarCreatorActor(ActorRef[] parkPlaces, ActorSystem system) {
        this.parkPlaces = parkPlaces;
        this.system = system;
    }

    @Override
    public void run() {
        for (int i = 0; i < 15; i++) {
            try {
                Thread.sleep((int) (Math.random() * 300));
                system.actorOf(CarActor.props(parkPlaces, "car-" + i), "car_" + i);
            } catch (InterruptedException e) {
            }
        }


    }
}
