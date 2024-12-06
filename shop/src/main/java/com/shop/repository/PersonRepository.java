package com.shop.repository;

import com.shop.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, String> {
    List<Person> findByUserName(String userName);

    List<Person> findByAddress(String address);
}
