package com.andreamarino.pharmazon.configuration;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.andreamarino.pharmazon.model.Booking;
import com.andreamarino.pharmazon.model.Cart;
import com.andreamarino.pharmazon.model.CreditCard;
import com.andreamarino.pharmazon.model.Order;
import com.andreamarino.pharmazon.model.Pharmacist;
import com.andreamarino.pharmazon.model.Product;
import com.andreamarino.pharmazon.model.Feedback;
import com.andreamarino.pharmazon.model.ServiceClass;
import com.andreamarino.pharmazon.model.User;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class CollectionConfig {

    @Bean
    @Qualifier("products")
    List<Product> products() {
        return new ArrayList<Product>();
    }

    @Bean
    @Qualifier("pharmacists")
    List<Pharmacist> pharmacists() {
        return new ArrayList<Pharmacist>();
    }

    @Bean
    @Qualifier("carts")
    List<Cart> carts() {
        return new ArrayList<Cart>();
    }

    @Bean
    @Qualifier("reviews")
    List<Feedback> reviews() {
        return new ArrayList<Feedback>();
    }

    @Bean
    @Qualifier("users")
    List<User> users() {
        return new ArrayList<User>();
    }

    @Bean
    @Qualifier("prescriptions")
    List<byte[]> prescriptions(){
        return new ArrayList<byte[]>();
    }

    @Bean
    @Qualifier("bookings")
    List<Booking> bookings(){
        return new ArrayList<Booking>();
    }

    @Bean
    @Qualifier("orders")
    List<Order> orders(){
        return new ArrayList<Order>();
    }

    @Bean
    @Qualifier("creditCards")
    List<CreditCard> creditCards(){
        return new ArrayList<CreditCard>();
    }

    @Bean
    @Qualifier("services")
    List<ServiceClass> services(){
        return new ArrayList<ServiceClass>();
    }

}
