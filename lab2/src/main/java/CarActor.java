import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import dto.CarMessageDto;
import dto.StopMessageDto;

import java.time.Duration;
import java.util.Random;

public class CarActor extends AbstractActor {

    private ActorRef[] parkPlaces;
    private ActorRef parkPlace;
    private String name;

    public CarActor(ActorRef[] stops, String name) {
        System.out.println("new car arrived " + name);
        this.name = name;
        this.parkPlaces = stops;
        tryTakeAPlace(stops);
    }

    private void tryTakeAPlace(ActorRef[] stops) {
        int rnd = new Random().nextInt(stops.length);
        final ActorRef stopTryTakeAPlace = stops[rnd];
        if (parkPlace == null) {
            stopTryTakeAPlace.tell(new CarMessageDto(CarMessageDto.CarrMessageType.IS_THERE_FREE_PLACES), getSelf());
        }
        if (parkPlace == null) {
            getContext().system().scheduler().scheduleOnce(
                    Duration.ofMillis((int) (Math.random() * 3300)),
                    getSelf(),
                    new CarMessageDto(CarMessageDto.CarrMessageType.I_AM_LIVIN),
                    getContext().dispatcher(),
                    getSelf()
            );
        }
    }

    public static Props props(ActorRef[] parkPlaces, String name) {
        return Props.create(CarActor.class, (Object) parkPlaces, name);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchUnchecked(StopMessageDto.class, massage -> {
                    processMessageFromStop((StopMessageDto) massage);
                    takePlace();
                })
                .matchUnchecked(CarMessageDto.class, massage -> {
                    processMessageFromCar((CarMessageDto) massage);
                })
                .build();
    }

    private void processMessageFromCar(CarMessageDto massage) {
        switch (massage.messageType) {
            case I_AM_LIVIN:
                System.out.println(name + " I try other stop");
                tryTakeAPlace(parkPlaces);
        }
    }

    private void processMessageFromStop(StopMessageDto massage) {
        switch (massage.messageType) {
            case YOU_CAN_TAKE_PLACE:
                takePlace();
                break;
        }
    }

    private void takePlace() {
        if (parkPlace == null) {
            parkPlace = getSender();
            System.out.println(name + " take place: " + parkPlace);
            waitingAndGoAway();
        } else {
            getSender().tell(new CarMessageDto(CarMessageDto.CarrMessageType.I_HAVE_PLACE), getSelf());
        }
    }

    private void waitingAndGoAway() {
        getContext().system().scheduler().scheduleOnce(Duration.ofMillis((int) (Math.random() * 30000)),
                parkPlace, new CarMessageDto(CarMessageDto.CarrMessageType.I_AM_GO_NOW), getContext().dispatcher(), getSelf());
        System.out.println(name + " has gone away");
    }
}
