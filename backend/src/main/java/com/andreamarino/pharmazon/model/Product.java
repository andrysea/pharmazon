package com.andreamarino.pharmazon.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.andreamarino.pharmazon.dto.ProductDto;
import com.andreamarino.pharmazon.model.designPattern.observer.product.ObserverProduct;
import com.andreamarino.pharmazon.model.designPattern.observer.product.Subject;
import com.andreamarino.pharmazon.services.mail.service.EmailService;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Component
@Scope("prototype") 
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id")
public class Product implements Serializable, Subject{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_product")
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Boolean prescription;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private String producer;

    @Column(nullable = false)
    private String activeIngredient;

    @Column(nullable = false)
    private Boolean activeProduct;

    @Column(nullable = false)
    private Integer quantity;

    @Lob
    @Column(name = "image", nullable = false, columnDefinition = "LONGBLOB")
    private byte[] image;
    
    @ManyToOne()
    @JoinColumn(name = "id_category", nullable = false) 
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<CartItem> cartItems = new ArrayList<>();
    
    @ManyToOne()
    @JoinColumn(name = "id_user", nullable = false) 
    private Pharmacist pharmacist;
    
    @CreationTimestamp
    private Timestamp dateTimeCreation; 

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "cart_product",
        joinColumns = @JoinColumn(name = "id_product"),
        inverseJoinColumns = @JoinColumn(name = "id_cart")
    )
    private List<Cart> cart;

    //Pattern Observer
    @Transient
    private List<ObserverProduct> observersProduct = new ArrayList<>();

    @Override
    public void addObserver(ObserverProduct observer) {
        observersProduct.add(observer);
    }

    @Override
    public void removeObserver(ObserverProduct observer) {
        observersProduct.remove(observer);
    }

    @Override
    public void notifyObservers(EmailService emailService) {
        for (ObserverProduct observer : observersProduct) {
            observer.updateProduct(this, emailService);
        }
    }

    public Product(ProductDto productDto){
        this.name = productDto.getName();
        this.code = productDto.getCode();
        this.price = productDto.getPrice();
        this.description = productDto.getDescription();
        this.image = productDto.getImage();
        this.producer = productDto.getProducer();
        this.activeIngredient = productDto.getActiveIngredient();
        this.activeProduct = productDto.getActiveProduct();
        this.quantity = productDto.getQuantity();
        this.prescription = productDto.getPrescription();
        this.category = new Category(productDto.getCategoryDto());
    }
}
