package com.lizhe.distributeddemo.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;


public class Ticket implements Serializable {
    private Integer id;
    private String ticketSeq;
    private Date ticketDate;
    private String fromStation;
    private String endStation;
    private Integer ticketStock;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTicketSeq() {
        return ticketSeq;
    }

    public void setTicketSeq(String ticketSeq) {
        this.ticketSeq = ticketSeq;
    }

    public Date getTicketDate() {
        return ticketDate;
    }

    public void setTicketDate(Date ticketDate) {
        this.ticketDate = ticketDate;
    }

    public String getFromStation() {
        return fromStation;
    }

    public void setFromStation(String fromStation) {
        this.fromStation = fromStation;
    }

    public String getEndStation() {
        return endStation;
    }

    public void setEndStation(String endStation) {
        this.endStation = endStation;
    }

    public Integer getTicketStock() {
        return ticketStock;
    }

    public void setTicketStock(Integer ticketStock) {
        this.ticketStock = ticketStock;
    }
}
