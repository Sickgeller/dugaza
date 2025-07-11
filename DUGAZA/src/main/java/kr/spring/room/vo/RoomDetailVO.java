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
    private Long houseId; // contentId
    private String roomName; //호실이나 방 이름
    private String roomType; // 스위트룸, 뭐시기룸 기타등등
    private Long price; // 
    private String roomSize; // 평이든 m제곱이든 자유
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