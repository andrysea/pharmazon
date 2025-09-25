package com.andreamarino.pharmazon.dto;

import java.sql.Timestamp;
import com.andreamarino.pharmazon.model.Booking;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    
    private String code;
    private ServiceDto service;
    private Timestamp dateTimeCreation;
    private Boolean accepted;

    public BookingDto(Booking booking){
        this.code = booking.getCode();
        this.dateTimeCreation = booking.getDateTimeCreation();
        this.service = new ServiceDto(booking.getService());
        this.accepted = booking.getAccepted();
    }
}
