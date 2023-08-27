package com.dlwlrma.testroute.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author hex1n
 * @Date 2023/8/24/16:19
 * @Description
 **/
@RestController
public class TestRouteController {

    private int count = 0;

    @GetMapping("/limit-test")
    public String testRoute() {
        count += 1;
        return "hello==" + count;
    }

    @GetMapping("/test")
    public String test() {
        return "test hello";
    }
}
