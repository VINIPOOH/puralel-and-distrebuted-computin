import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import dto.CarMessageDto;
import dto.StopMessageDto;

import java.util.LinkedList;

public class StopActor extends AbstractActor {

    private LinkedList<ActorRef> waitingCars;
    private int amountPlaces = 10;
    private ActorRef[] parkedCars = new ActorRef[amountPlaces];
    private String name;

    public StopActor(String name) {
        this.name = name;
        this.waitingCars = new LinkedList<>();
    }


    public static Props props(String name) {
        return Props.create(StopActor.class, name);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchUnchecked(CarMessageDto.class, massage -> {
                    processCarrMessage((CarMessageDto) massage);
                })
                .build();
    }

    private void processCarrMessage(CarMessageDto massage) {
        switch (massage.messageType) {
            case IS_THERE_FREE_PLACES:
                checkPlaces();
                break;
            case I_HAVE_PLACE:
                cleanParkPlace();
                notifyAnotherCars();
                break;
            case I_AM_GO_NOW:
                cleanParkPlace();
                System.out.println("Is Free: " + name);
                notifyAnotherCars();
                break;
        }
    }

    public void checkPlaces() {
        for (int i = 0; i < amountPlaces; i++) {
            if (parkedCars[i] == null) {
                parkedCars[i] = getSender();
                getSender().tell(new StopMessageDto(StopMessageDto.StopMessageType.YOU_CAN_TAKE_PLACE), getSelf());
                return;
            }
        }
        waitingCars.add(getSender());
    }

    public void cleanParkPlace() {
        for (int i = 0; i < amountPlaces; i++) {
            if (parkedCars[i] == getSender()) {
                parkedCars[i] = null;
                getSender().tell(new StopMessageDto(StopMessageDto.StopMessageType.YOU_CAN_TAKE_PLACE), getSelf());
                break;
            }
        }
    }

    public void notifyAnotherCars() {
        for (int i = 0; i < waitingCars.size(); i++) {
            if (waitingCars.getFirst().isTerminated()) {
                waitingCars.removeFirst();
            } else {
                for (int j = 0; j < amountPlaces; j++) {
                    if (parkedCars[j] == null) {
                        parkedCars[j] = waitingCars.getFirst();
                        waitingCars.getFirst().tell(new StopMessageDto(StopMessageDto.StopMessageType.YOU_CAN_TAKE_PLACE), getSelf());
                        waitingCars.removeFirst();
                        return;
                    }
                }
                return;
            }
        }
    }

}
