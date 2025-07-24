package kr.spring.wishlist.vo;

import lombok.Data;

@Data
public class WishItemVO {
    private Long contentId;
    private String contentType; // ← 숫자 대신 문자열
    private String title;
    private String description;
    private String imageUrl;
    private Double rating;
    private Integer reviewCount;
    private String location;
    private Integer price;
    

    public void setContentTypeFromInt(Integer contentTypeInt) {
        if (contentTypeInt == 32) {
            this.contentType = "HOUSE";
        } else if (contentTypeInt == 15) {
            this.contentType = "CAR";
        } else {
            this.contentType = "UNKNOWN";
        }
    }
}
