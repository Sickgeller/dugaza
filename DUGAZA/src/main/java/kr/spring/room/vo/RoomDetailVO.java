package kr.spring.room.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomDetailVO {
    private Long roomId;
    private Long houseId;
    private String roomName;
    private Long price;
    private String roomSize;
    private Integer minimumCapacity;
    private Integer maximumCapacity;
    private String bedInfo;
    private Integer wifi;
    private Integer tv;
    private Integer aircon;
    private Integer bathroom;
    private Integer sofa;
    private Integer kitchen;
    private Integer pet;
    private Integer smokingRoom;
    private String image1;
    private String image2;
    private String image3;
    private Integer status;
    private String description;
}