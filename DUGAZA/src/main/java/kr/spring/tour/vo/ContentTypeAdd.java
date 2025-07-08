package kr.spring.tour.vo;

public enum ContentTypeAdd {
    TOUR(12, "touristAttraction"),
    CULTURE(14, "culturalCenter"),
    FESTIVAL(15, "event"),
    COURSE(25, "tripCourse"),
    LEISURE(28, "reports"),
    LODGING(32, "house"),
    SHOPPING(38, "shopping"),
    FOOD(39, "restaurant");

    private final int id;
    private final String name;

    ContentTypeAdd(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ContentTypeAdd fromId(int id) {
        for (ContentTypeAdd type : values()) {
            if (type.id == id) return type;
        }
        throw new IllegalArgumentException("Unknown content type id: " + id);
    }
}

