package com.andreamarino.pharmazon.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.andreamarino.pharmazon.dto.ClientDto;
import com.andreamarino.pharmazon.exception.NotFoundException;
import com.andreamarino.pharmazon.model.designPattern.observer.service.ObserverService;
import com.andreamarino.pharmazon.model.designPattern.observer.product.ObserverProduct;
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
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id")
public class Client extends User implements ObserverService, ObserverProduct{

    public Client(User user){
      super(user);
    }

    public Client(ClientDto clientDto){
      super(clientDto);
      this.creditCards = clientDto.getCreditCardsDto().stream().map(entity -> new CreditCard(entity)).collect(Collectors.toList());
    }

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<CreditCard> creditCards = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Booking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Feedback> feedbacks = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Address> address = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Chat> chats = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Cart> carts = new ArrayList<>();

    @Override
    public void updateService(EmailService emailService, String nameService, String date) {
      String text = "Gentile cliente " + this.getName() + "." + 
      "\nLa contattiamo per avvisarla che il servizio: " + nameService + " con data e ora: " + date + 
      " non e' piu' disponibile. \nLa sua prenotazione e' stata cancellata.";

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
        text = "Gentile cliente, " + "\n" +
                    "la informiamo che il prodotto da lei acquistato in passato: " + product.getName() + "\n" +
                    "e' prossimo a terminare.";
      }
      else if(product.getQuantity() == 0){
        text = "Gentile cliente, " +  "\n" +
                    "la informiamo che il prodotto da lei acquistato in passato: " + product.getName() + "\n" +
                    "e' terminato.";
      }
      else{
        text = "";
      }

      if(!text.equals("")){
        emailService.sendSimpleEmail(getEmail(), "Informazioni sul prodotto che hai acquistato!", text);
      }
    }
}
