package kr.spring.api.mapper;

import kr.spring.api.dto.RestaurantDetailApiDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RestaurantDetailApiMapper extends CommonApiMapper{
    void insert(RestaurantDetailApiDto restaurantDetailApiDto);
    void update(RestaurantDetailApiDto restaurantDetailApiDto);
}
