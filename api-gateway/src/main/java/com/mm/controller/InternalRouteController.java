package com.mm.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InternalRouteController {

    @RequestMapping("/internal/**")
    public ResponseEntity<Void> blockInternalRoutes() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
