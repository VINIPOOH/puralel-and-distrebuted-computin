package dto;

public class StopMessageDto {
    public StopMessageType messageType;

    public StopMessageDto(StopMessageType messageType) {
        this.messageType = messageType;
    }

    public enum StopMessageType{
        YOU_CAN_TAKE_PLACE
    }
}
