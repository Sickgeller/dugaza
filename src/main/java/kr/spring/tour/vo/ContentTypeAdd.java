package kr.spring.tour.vo;

public enum ContentTypeAdd {
	TOUR(12, "touristAttraction", "관광지"),
	//    CULTURE(14, "culturalCenter"),
	FESTIVAL(15, "event", "축제"),
	//    COURSE(25, "tripCourse"),
	//    LEISURE(28, "reports"),
	LODGING(32, "house", "숙소"),
	//    SHOPPING(38, "shopping"),
	FOOD(39, "restaurant", "음식점");

	private final int id;
	private final String name;
	private final String displayName; // 한글 이름

	ContentTypeAdd(int id, String name, String displayName) {
		this.id = id;
		this.name = name;
		this.displayName = displayName;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}
	
	// 여러 ID를 TOUR로 묶기
    public static ContentTypeAdd fromId(int id) {
        if (id == 12 || id == 14 || id == 25 || id == 28 || id == 38) {
            return TOUR;
        }
        for (ContentTypeAdd type : values()) {
            if (type.id == id) return type;
        }
        throw new IllegalArgumentException("Unknown content type id: " + id);
    }

//	public static ContentTypeAdd fromId(int id) {
//		for (ContentTypeAdd type : values()) {
//			if (type.id == id) return type;
//		}
//		throw new IllegalArgumentException("Unknown content type id: " + id);
//	}

	public static ContentTypeAdd fromName(String name) {
		for (ContentTypeAdd type : values()) {
			if (type.name.equals(name)) return type;
		}
		throw new IllegalArgumentException("Unknown content type name: " + name);
	}
}

