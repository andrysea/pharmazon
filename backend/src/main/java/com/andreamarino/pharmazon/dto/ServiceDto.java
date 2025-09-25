package com.andreamarino.pharmazon.dto;


import java.time.LocalDateTime;
import com.andreamarino.pharmazon.model.ServiceClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDto {
    private String name;
    private String code;
    private String description;
    private Double price;
    private byte[] image;
    private Integer availability;
    private LocalDateTime dateChosen;

    public ServiceDto(ServiceClass service){
        this.code = service.getCode();
        this.name = service.getName();
        this.availability = service.getAvailability();
        this.description = service.getDescription();
        this.price = service.getPrice();
        this.image = service.getImage();
        this.dateChosen = service.getDateChosen();
    }
}
