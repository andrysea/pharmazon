package com.andreamarino.pharmazon.model;

import java.util.ArrayList;
import java.util.List;
import com.andreamarino.pharmazon.dto.PharmacistDto;
import com.andreamarino.pharmazon.exception.NotFoundException;
import com.andreamarino.pharmazon.model.designPattern.observer.product.ObserverProduct;
import com.andreamarino.pharmazon.model.designPattern.observer.service.ObserverService;
import com.andreamarino.pharmazon.services.chat.model.Chat;
import com.andreamarino.pharmazon.services.mail.service.EmailService;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id")
public class Pharmacist extends User implements ObserverService, ObserverProduct{

    public Pharmacist(User user){
      super(user);
    }

    public Pharmacist(PharmacistDto pharmacistDto){
      super(pharmacistDto);
    }
        
    @OneToMany(mappedBy = "pharmacist")
    private List<ServiceClass> services = new ArrayList<>();
    
    @OneToMany(mappedBy = "pharmacist")
    private List<Product> products = new ArrayList<>();

    @OneToMany(mappedBy = "pharmacist", cascade = CascadeType.ALL)
    private List<Chat> chats = new ArrayList<>();

    @Override
    public void updateService(EmailService emailService, String nameService, String date) {
      String text = "Gentile farmacista " + this.getName() + "." + 
      "\nLe ricordiamo che il servizio: " + nameService + " con data e ora: " + date + 
      " non è piu' disponibile. \nComunichi la cancellazione dell'evento ad eventuali clienti che non utilizzano la piattaforma.";

      emailService.sendSimpleEmail(getEmail(), "Informazioni prenotazione servizio: " + nameService , text);
    }

    @Override
    public void updateProduct(Product product, EmailService emailService) {
      final String text;

      if(emailService == null){
        throw new NotFoundException("L'oggetto relativo alla email inserito e' nullo.");
      }

      if(product == null){
        throw new NotFoundException("L'oggetto prodotto inserito e' nullo.");
      }

      if(product.getQuantity() == null){
        throw new NotFoundException("Il prodotto ha una quantita' nulla.");
      }

      if(product.getQuantity() < 0){
        throw new IllegalArgumentException("Il prodotto ha una quantita' non valida < 0.");
      }
  
      if(product.getQuantity() == 2){
        text = "Gentile farmacista, " + "\n" +
                    "la informiamo che il prodotto: " + product.getName() + "\n" +
                    "e' prossimo a terminare.";
      }
      else if(product.getQuantity() == 0){
        text = "Gentile farmacista, " +  "\n" +
                    "la informiamo che il prodotto: " + product.getName() + "\n" +
                    "e' terminato.";
      }
      else{
        text = "";
      }

      if(!text.equals("")){
        emailService.sendSimpleEmail(getEmail(), "Informazioni sul prodotto che hai creato!", text);
      }
    }
}
