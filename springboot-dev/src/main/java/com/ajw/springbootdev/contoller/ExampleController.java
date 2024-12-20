package com.ajw.springbootdev.contoller;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.List;

@Controller  // 컨트롤러라는 것을 명식적으로 표시
public class ExampleController {

    @Getter @Setter
    class Person {
        private Long id;
        private String name;
        private int age;
        private List<String> hobbies;
    }

    @GetMapping("/thymeleaf/example")
    public String thymeleafExample(Model model) {  // 뷰로 데이터를 넘겨주는 모델 객체
        Person examplePerson = new Person();
        examplePerson.setId(1L);
        examplePerson.setName("홍길동");
        examplePerson.setAge(20);
        examplePerson.setHobbies(List.of("운동", "독서"));

        model.addAttribute("person", examplePerson);  // Person 객체 저장
        model.addAttribute("today", LocalDateTime.now());

        return "example";
    }
}
