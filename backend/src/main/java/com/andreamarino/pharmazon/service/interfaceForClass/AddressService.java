package com.andreamarino.pharmazon.service.interfaceForClass;

import java.util.List;
import com.andreamarino.pharmazon.dto.AddressDto;

public interface AddressService {
    AddressDto insertAddressDto(AddressDto addressDto, String username);
    AddressDto updateAddressDto(AddressDto addressDto);
    List<AddressDto> getAddressDto(String username);
    void deactivateAddressDto(String code);
}
