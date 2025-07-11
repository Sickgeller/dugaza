CREATE TABLE EVENT_RESERVATION (
    RESERVATION_ID   NUMBER(22) PRIMARY KEY,
    CREATED_AT       DATE,
    GUEST_COUNT      NUMBER(22),
    PAYMENT_STATUS   VARCHAR2(20),
    STATUS           VARCHAR2(20),
    TOTAL_PRICE      NUMBER(22),
    UPDATED_MDATE    DATE,
    MEMBER_ID        NUMBER(22),
    CONTENT_ID       NUMBER(22),
    constraint event_reservation_fk1 foreign key (member_id) references member (member_id),
    constraint event_reservation_fk2 foreign key (content_id) references tour_content (content_id)
);

create sequence event_reservation_seq;

create table wishlist(
	wishlist_id number(22) primary key,
	member_id number(22) not null,
	content_type number(22) not null,
	content_id number(22) not null,
	created_at date not null,
	constraint wishlist_fk1 foreign key (member_id) references member (member_id),
    constraint wishlist_fk2 foreign key (content_id) references tour_content (content_id),
    constraint wishlist_fk3 foreign key (content_type) references tour_category_code (tour_content_id)
);

create sequence wishlist_seq;