import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import junit.framework.TestCase;
import org.junit.Test;

public class CarsStopsTest extends TestCase {

    @Test
    public void test() {
        ActorSystem system = ActorSystem.create("test-system");

        ActorRef[] carPlaces = new ActorRef[3];
        carPlaces[0] = system.actorOf(StopActor.props("CAR-PLACE-1"), "car_place_1");
        carPlaces[1] = system.actorOf(StopActor.props("CAR-PLACE-2"), "car_place_2");
        carPlaces[2] = system.actorOf(StopActor.props("CAR-PLACE-3"), "car_place_3");
        CarCreatorActor carCreatorActor = new CarCreatorActor(carPlaces, system);
        carCreatorActor.start();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}