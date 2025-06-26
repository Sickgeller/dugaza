--회원관리
create table spmember(
 mem_num number not null,
 id varchar2(16) unique not null,
 nick_name varchar2(30) unique,
 authority varchar2(30) default 'ROLE_USER' not null,
 constraint spmember_pk primary key (mem_num)
);
create table spmember_detail(
 mem_num number not null,
 name varchar2(30) not null,
 passwd varchar2(60) not null,
 phone varchar2(15) not null,
 email varchar2(50) not null,
 zipcode varchar2(5) not null,
 address1 varchar2(120) not null,
 address2 varchar2(90) not null,
 photo blob,
 photo_name varchar2(100),
 reg_date date default sysdate not null,
 modify_date date,
 constraint spmember_detail_pk primary key (mem_num),
 constraint spmember_detail_fk foreign key (mem_num)
                         references spmember (mem_num)
);
create sequence spmember_seq;

--자동 로그인(스프링 시큐리티 자동 로그인 기능 사용)
create table persistent_logins(
 series varchar2(64) primary key,
 username varchar2(64) not null,
 token varchar2(64) not null,
 last_used timestamp not null
);