package gachon.bridge.userservice.dto;

import lombok.Data;

import java.util.Date;

@Data
public class SendLinkResponseDTO {
    private Date time;

    public SendLinkResponseDTO() {
        this.time = new Date();
    }
}