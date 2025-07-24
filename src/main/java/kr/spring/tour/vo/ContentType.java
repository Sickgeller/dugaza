package kr.spring.tour.vo;

public enum ContentType {
    TOUR(12, "관광지"),
    CULTURE(14, "문화시설"),
    FESTIVAL(15, "축제/공연/행사"),
    COURSE(25, "여행코스"),
    LEISURE(28, "레포츠"),
    LODGING(32, "숙박"),
    SHOPPING(38, "쇼핑"),
    FOOD(39, "음식점");

    private final int id;
    private final String name;

    ContentType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }

    public static String getNameById(int id) {
        for (ContentType type : values()) {
            if (type.id == id) return type.name;
        }
        return "알 수 없음";
    }
}

