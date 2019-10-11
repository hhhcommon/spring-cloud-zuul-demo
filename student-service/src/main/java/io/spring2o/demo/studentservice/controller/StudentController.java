package io.spring2o.demo.studentservice.controller;

import io.spring2o.demo.studentservice.model.Student;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class StudentController {

    @RequestMapping(value = "echo/{name}")
    public String echoStudentName(@PathVariable(name = "name") String name) {
        return "hello  <strong style=\"color: red;\">" + name + " </strong> Responsed on : " + new Date();
    }

    @RequestMapping(value = "/getDetail/{name}")
    public Student getStuentDetails(@PathVariable(name = "name") String name) {
        return new Student(name, "beijing", "MSA201");
    }
}
