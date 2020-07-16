package com.lizhe.distributeddemo.controller;

import com.lizhe.distributeddemo.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TicketController {
    @Autowired
    private TicketService ticketService;
    @RequestMapping("/ticket/{ticketSeq}")
    public Object getTicket(@PathVariable("ticketSeq") String ticketSeq) {
        return ticketService.getTicket(ticketSeq);
    }

}
