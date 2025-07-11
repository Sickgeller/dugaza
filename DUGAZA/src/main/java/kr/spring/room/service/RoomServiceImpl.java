package kr.spring.room.service;

import kr.spring.room.vo.RoomDetailVO;
import kr.spring.room.dao.RoomMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomMapper roomMapper;

    @Override
    public List<RoomDetailVO> getRoomsWithSeller(Long id, int startRow, int endRow) {
        List<RoomDetailVO> result = roomMapper.getRoomsWithSellerId(id, startRow, endRow);
        if(result.isEmpty()){
            return new ArrayList<>();
        }
        return result;
    }

    @Override
    public int getTotalRoomCount(Long sellerId) {
        return roomMapper.getTotalRoomCount(sellerId);
    }

    @Override
    public void insertRoom(RoomDetailVO roomDetailVO) {
        roomMapper.insertRoom(roomDetailVO);
    }
    
    @Override
    public void updateRoom(RoomDetailVO roomDetailVO) {
        roomMapper.updateRoom(roomDetailVO);
    }
    
    @Override
    public void deleteRoom(Long roomId) {
        roomMapper.deleteRoom(roomId);
    }
    
    @Override
    public RoomDetailVO getRoomById(Long roomId) {
        return roomMapper.getRoomById(roomId);
    }
}
