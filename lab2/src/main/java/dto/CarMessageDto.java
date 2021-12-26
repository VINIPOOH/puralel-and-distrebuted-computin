package dto;

public class CarMessageDto {
    public CarrMessageType messageType;

    public CarMessageDto(CarrMessageType messageType) {
        this.messageType = messageType;
    }

    public enum CarrMessageType{
        IS_THERE_FREE_PLACES,
        I_HAVE_PLACE,
        I_AM_LIVIN,
        I_AM_GO_NOW
    }
}
