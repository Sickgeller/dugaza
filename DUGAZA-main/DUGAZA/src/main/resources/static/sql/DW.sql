--QnA_question
create qna_question table(
 qna_id number primary key,
 member_id number not null,
 category varchar2(50) not null,
 title varhchar2(50) not null,
 content clob not null,
 created_at date default sysdate not null,
 is_answered char(1) not null,
 
 constraint qna_question_pk primary key (qna_id),
 constraint qna_question_fk foreign key (member_id) references member (member_id)
);

create sequence qna_question_seq;

create qna_response table(
 respose_id number primary key,
 qna_id number not null,
 member_id varchar2(50) not null,
 content clob,
 created_at date default sysdate,
 
 constraint qna_response_pk primary key (response_id),
 constraint qna_response_fk foreign key (qna_id) references qna_question (qna_id),
 constraint qna_response_fk foreign key (member_id) references member (member_id)
);

create sequence qna_response_seq;