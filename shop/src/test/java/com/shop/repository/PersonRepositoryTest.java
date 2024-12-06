package com.shop.repository;

import com.shop.entity.Person;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class PersonRepositoryTest {

    @Autowired
    PersonRepository personRepository;

    @Test
    @DisplayName("create-insert")
    public void insertPerson() {
        Person person = Person.builder()
                .userName("test02")
                .age(23)
                .phone("010-2222-2222")
                .address("Suwon, South Korea")
                .regDate(LocalDateTime.now())
                .build();

        personRepository.save(person);
    }

    @Test
    @DisplayName("create-insertList")
    public void insertListPerson() {
        for(int i=3; i<10; i++){
            Person person = Person.builder()
                    .userName("test0"+i)
                    .age(23+i)
                    .phone("010-0000-000"+i)
                    .address("Seoul, South Korea")
                    .regDate(LocalDateTime.now())
                    .build();

            personRepository.save(person);
        }
    }

    @Test
    @DisplayName("read-select")
    public void selectByUserNameTest() {
        String name = "test03";
        List<Person> userName = personRepository.findByUserName(name);

        userName.forEach(person -> log.info(person.toString()));
    }

    @Test
    @DisplayName("all select")
    public void findAllTest(){
        List<Person> all = personRepository.findAll();
        all.forEach(person -> log.info(person.toString()));
    }
//
//    @Test
//    @DisplayName("address 수정")
//    public void selectByAddressTest() {
//        this.findAllTest();
//
//        List<Person> person = personRepository.findByAddress("Anyang, South Korea");
//
//        PersonRepository.save(person);
//    }

}