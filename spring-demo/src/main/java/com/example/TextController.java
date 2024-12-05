package com.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TextController {

    @GetMapping("/test")
    public UserDto test() {
        UserDto userDto = new UserDto();
        userDto.setName("an");
        userDto.setAge(20);

        return userDto;
    }
}
