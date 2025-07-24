package kr.spring.wishlist.vo;

import java.sql.Date;

import lombok.Data;

@Data
public class WishListVO {
	private Long wishListId;
	private Long memberId;
	private Long contentType;
	private Long contentId;
	private Date createdAt;
}
