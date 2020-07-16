package com.lizhe.distributeddemo.service;

import com.lizhe.distributeddemo.vo.Ticket;

public interface TicketService {
    Ticket getTicket(String ticketSeq);
}
