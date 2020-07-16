package com.lizhe.distributeddemo.mapper;

import com.lizhe.distributeddemo.vo.Ticket;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TicketMapper {
    Ticket getTicketBySeq(@Param("ticketSeq") String ticketSeq);

    List<String> listTicketSeq();
}
