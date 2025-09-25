package com.andreamarino.pharmazon.service.interfaceForClass;

import java.util.List;
import com.andreamarino.pharmazon.dto.ServiceDto;
import org.springframework.security.core.userdetails.UserDetails;

public interface ServiceService {
    ServiceDto insertServiceDto(ServiceDto serviceDto, String username);
    ServiceDto updateServiceDto(ServiceDto serviceDtoNew, String oldCode, UserDetails userDetails);
    ServiceDto getServiceDto(String code);
    List<ServiceDto> getServiceListDto(String username);
    List<ServiceDto> getServiceListDtoName(String username, String name);
    void deleteServiceDto(String code);
}
