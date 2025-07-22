package kr.spring.api.mapper;

public interface CommonApiMapper {
    <T> void insert(T t);
    <T> void update(T t);
}
