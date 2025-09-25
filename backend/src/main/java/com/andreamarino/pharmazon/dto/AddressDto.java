package com.andreamarino.pharmazon.dto;

import com.andreamarino.pharmazon.model.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    private String code;
    private String name;
    private String surname;
    private String number;
    private String address;
    private String cap;
    private String city;
    private String province;

    public AddressDto(Address address){
        this.code = address.getCode();
        this.name = address.getName();
        this.surname = address.getSurname();
        this.number = address.getNumber();
        this.address = address.getAddress();
        this.cap = address.getCap();
        this.city = address.getCity();
        this.province = address.getProvince();
    }
}
