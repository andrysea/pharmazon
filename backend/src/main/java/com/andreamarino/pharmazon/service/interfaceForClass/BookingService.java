package com.andreamarino.pharmazon.service.interfaceForClass;

import java.util.List;
import com.andreamarino.pharmazon.dto.BookingDto;

public interface BookingService {
    BookingDto insertBookingDto(BookingDto bookingDto, String username);
    List<BookingDto> getBookingDto();
    List<BookingDto> getBookingDtoNotAccepted();
    List<BookingDto> getBookingDtoAccepted();
    BookingDto updateBookingDto(BookingDto bookingDto);
    void deleteBookingDto(BookingDto bookingDto, String username);
}
