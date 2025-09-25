package com.andreamarino.pharmazon.service.implementationClass;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.andreamarino.pharmazon.services.mail.service.EmailService;
import com.andreamarino.pharmazon.dto.CartItemDto;
import com.andreamarino.pharmazon.dto.CreditCardDto;
import com.andreamarino.pharmazon.dto.OrderDto;
import com.andreamarino.pharmazon.exception.NotFoundException;
import com.andreamarino.pharmazon.model.Address;
import com.andreamarino.pharmazon.model.Cart;
import com.andreamarino.pharmazon.model.CartItem;
import com.andreamarino.pharmazon.model.Client;
import com.andreamarino.pharmazon.model.CreditCard;
import com.andreamarino.pharmazon.model.Order;
import com.andreamarino.pharmazon.model.designPattern.state.State;
import com.andreamarino.pharmazon.model.designPattern.state.StateDeleted;
import com.andreamarino.pharmazon.model.designPattern.state.StateDelivered;
import com.andreamarino.pharmazon.model.designPattern.state.StateInTransit;
import com.andreamarino.pharmazon.model.designPattern.state.StatePreparation;
import com.andreamarino.pharmazon.model.designPattern.state.StateWaitingApproval;
import com.andreamarino.pharmazon.repository.AddressRepository;
import com.andreamarino.pharmazon.repository.CartItemRepository;
import com.andreamarino.pharmazon.repository.CartRepository;
import com.andreamarino.pharmazon.repository.CreditCardRepository;
import com.andreamarino.pharmazon.repository.OrderRepository;
import com.andreamarino.pharmazon.repository.ProductRepository;
import com.andreamarino.pharmazon.repository.StateRepository;
import com.andreamarino.pharmazon.repository.UserRepository;
import com.andreamarino.pharmazon.service.interfaceForClass.OrderService;
import com.andreamarino.pharmazon.util.ValidationUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    @Autowired
    private final OrderRepository orderRepository;

    @Autowired
    private final CartRepository cartRepository;

    @Autowired
    private final CreditCardRepository creditCardRepository;

    @Autowired
    private final ProductRepository productRepository;

    @Autowired
    private final StateRepository stateRepository;

    @Autowired
    private final CartItemRepository cartItemRepository;

    @Autowired
    private final AddressRepository addressRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final EmailService emailService;

    @Override
    @Transactional
    public OrderDto insertOrderDto(OrderDto orderDto, String username) {
        this.checkOrder(orderDto);
        
        if(username == null || username.isEmpty()){
            throw new NotFoundException("Lo username e' nullo o vuoto.");
        }
        
        Order order = new Order();
        order.setCode(this.generateCode());

        // Verifico e aggiungo l'indirizzo all'ordine
        Address address = addressRepository.findByCode(orderDto.getAddressDto().getCode())
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato l'indirizzo con codice: " + orderDto.getAddressDto().getCode()));
        
        if(!address.isActive()){
            throw new IllegalStateException("L'indirizzo inserito non e' attivo.");
        }
        
        order.setAddress(address);
        
        Client client = (Client) userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun utente con questo username:" + username));

        Cart cart = cartRepository.getCartOfClient(client.getUsername())
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun carrello associato a questo cliente con questo username:" + username));
        cart.setActiveCart(false);

        // Verifico che la carta di credito inserita esisti effettivamente e abbia quei valori inseriti
        CreditCardDto creditCardDto = orderDto.getClientDto().getCreditCardsDto().get(0);

        CreditCard creditCard = creditCardRepository.findByNumber(creditCardDto.getNumber())
        .filter(creditCardDb -> creditCardDto.getNumber().equals(creditCardDb.getNumber())
                && creditCardDto.getCardSecurityCode().equals(creditCardDb.getCardSecurityCode())
                && creditCardDto.getExpirationDate().equals(creditCardDb.getExpirationDate())
                && creditCardDto.getName().equals(creditCardDb.getName())
                && creditCardDto.getSurname().equals(creditCardDb.getSurname())
                && creditCardDb.isActive())
        .orElseThrow(() -> new NotFoundException("Non e' stata trovata nessuna carta di credito."));

        // Verifico che la carta di credito appartenga al cliente
        boolean isCreditCardOwnedByClient = client.getCreditCards().stream()
        .anyMatch(card -> card.getNumber().equals(creditCard.getNumber())
            && card.getCardSecurityCode().equals(creditCard.getCardSecurityCode())
            && card.getExpirationDate().equals(creditCard.getExpirationDate())
            && card.getName().equals(creditCard.getName())
            && card.getSurname().equals(creditCard.getSurname()));

        if (!isCreditCardOwnedByClient) {
            throw new NotFoundException("La carta di credito non appartiene a questo cliente.");
        }

        if(!ValidationUtils.isValidExpirationDate(creditCard.getExpirationDate())){
            throw new IllegalArgumentException("La carta di credito e' scaduta.");
        }

        Double total = 0.0;
        int count = 0;
        boolean flag = false;

        List<CartItem> listCartItems = orderDto.getCartDto().getCartItemsDto().stream().map(entity -> new CartItem(entity))
        .collect(Collectors.toList());

        List<Client> listClient = new ArrayList<>();

        // Verifico che i prodotti nel carrello siano effettivamente quelli inseriti nell'ordine dall'utente
        List<CartItem> listCartItemsDb = cartItemRepository.findCartItemsInCart(cart.getId(), client.getId());
        
        if(listCartItemsDb.size() == listCartItems.size()){
            for(CartItem cartItemDb: listCartItemsDb){
                for(CartItem cartItem: listCartItems){
                    if(cartItemDb.getProduct().getCode().equals(cartItem.getProduct().getCode())){
                        
                        // Verifico la quantità prodotto e se è appunto disponibile
                        if((cartItem.getQuantity() == null) || (cartItemDb.getProduct().getQuantity() < cartItem.getQuantity()) || (cartItem.getQuantity() <= 0) ){
                            throw new IllegalArgumentException("La quantita' inserita per il prodotto con codice: " + cartItem.getProduct().getCode() + " non e' corretta.");
                        }
                        total = total + (cartItem.getQuantity() * cartItem.getProduct().getPrice());
                        cartItemDb.getProduct().setQuantity(cartItemDb.getProduct().getQuantity() - cartItem.getQuantity());

                        // Prendo tutti gli utenti che hanno acquistato il prodotto ed è stato consegnato
                        // Utilizzo per pattern observer
                        listClient = orderRepository.findAllClientWhoHaveBuyADeliveredProduct(new StateDelivered().getState(), cartItemDb.getProduct().getCode(), cartItemDb.getProduct().getName()); 
                        cartItemDb.getProduct().getObserversProduct().addAll(listClient);
                        cartItemDb.getProduct().addObserver(cartItemDb.getProduct().getPharmacist());
                        cartItemDb.setFlag(true);

                        // Se è richiesta la prescrizione per un prodotto, allora controllo l'immagine se è valida
                        if (cartItemDb.getProduct().getPrescription() == true) {
                            ValidationUtils.isValidBase64(cartItem.getImagePrescription());
                            cartItemDb.setImagePrescription(cartItem.getImagePrescription());
                            flag = true;
                        }
                        count ++;
                    }
                }
            }

            if(count != listCartItemsDb.size()){
                throw new IllegalArgumentException("Hai inserito qualche prodotto che non fa parte del carrello.");
            }
        }
        else{
            throw new IllegalArgumentException("Il numero di elementi presentati, non corrisponde agli elementi effettivamente presenti nel carrello.");
        }

        if((total != orderDto.getTotal().doubleValue())){
            throw new IllegalArgumentException("Il totale inserito non e' corretto."); 
        }

        if (total > creditCard.getBalance()) {
            throw new IllegalArgumentException("Il saldo della carta di credito, deve essere maggiore o uguale di " + total + "€.");
        }

        order.setCreditCard(creditCard);
        order.setClient(client);
        order.setCart(cart);
        order.setTotal(total);

        //Pattern Observer
        order.getCart().getCartItems().forEach(cartItem -> cartItem.getProduct().notifyObservers(emailService));

        // Se almeno un prodotto dell'ordine richiede prescrizione
        if (flag) {
            //Pattern State
            StateWaitingApproval stateWatingApproval = (StateWaitingApproval) stateRepository
            .findByState(new StateWaitingApproval().getState())
            .orElseThrow(() -> new InternalError("Errore nella ricerca dello stato."));

            order.setState(stateWatingApproval);
            order.getCart().setCartItems(listCartItemsDb);
            orderRepository.save(order);

            emailService.sendSimpleEmail(order.getClient().getEmail(), "Order: " + order.getCode() + " - Status Update.",
                "Il tuo ordine dal codice: " + order.getCode() + " in stato: " + order.getState().getState().toUpperCase() + ".");
            return orderDto;
        }
        else {                        
            //Pattern State
            StatePreparation statePreparation = (StatePreparation) 
            stateRepository.findByState(new StatePreparation().getState())
            .orElseThrow(() -> new InternalError("Errore nella ricerca dello stato."));

            order.setState(statePreparation);
            order.getCart().setCartItems(listCartItemsDb);
            orderRepository.save(order);

            creditCard.setBalance(creditCard.getBalance() - total);
            creditCardRepository.save(creditCard);

            emailService.sendSimpleEmail(order.getClient().getEmail(), "Order: " + order.getCode() + " - Status Update.",
            "Il tuo ordine dal codice: " + order.getCode() + " in stato: " + order.getState().getState().toUpperCase() + ".");
            
            return orderDto;
        }
    }

    private void checkOrder(OrderDto orderDto) {
        if (orderDto == null) {
            throw new NotFoundException("L'ordine inserito non puo' essere nullo.");
        }

        if (orderDto.getCartDto() == null) {
            throw new NotFoundException("Il carello non puo' essere nullo o vuoto.");
        }

        if (orderDto.getCartDto().getCartItemsDto() == null || orderDto.getCartDto().getCartItemsDto().isEmpty()) {
            throw new NotFoundException("La lista dei prodotti, inseriti nel carrello, non puo' essere nulla o vuota.");
        }

        if (orderDto.getAddressDto() == null || orderDto.getAddressDto().getCode() == null || orderDto.getAddressDto().getCode().isEmpty()) {
            throw new NotFoundException("L'indirizzo inserito e' nullo o vuoto.");
        }
        
        for (CartItemDto cartItemDto : orderDto.getCartDto().getCartItemsDto()) {
            if (cartItemDto == null || cartItemDto.getProductDto() == null) {
                throw new NotFoundException("Non puoi inserire degli oggetti nel carrello nulli.");
            }
            
            if(cartItemDto.getProductDto().getCode() != null && !cartItemDto.getProductDto().getCode().isEmpty()){
                productRepository.findByCode(cartItemDto.getProductDto().getCode()).
                orElseThrow(() -> new NotFoundException("Valori inseriti errati, per il prodotto nel carrello con codice: " + cartItemDto.getProductDto().getCode()));
            }
            else{
                throw new NotFoundException("Non e' stato inserito il codice di un prodotto, presente nel carrello.");
            }
        }

        if (orderDto.getTotal() == null){
            throw new NotFoundException("Il totale inserito non puo' essere nullo.");
        }

        if ((orderDto.getClientDto() == null)){
            throw new NotFoundException("Le informazioni sull'utente non sono state inserite");
        }

        if ((orderDto.getClientDto().getCreditCardsDto() == null)){
            throw new NotFoundException("Il valore della carta di credito inserita, non puo' essere nullo.");
        }

        if ((orderDto.getClientDto().getCreditCardsDto().size() != 1)){
            throw new IllegalArgumentException("Ricordati di inserire una sola carta di credito.");
        }
    }

    @Override
    @Transactional
    public OrderDto updateOrderDto(OrderDto orderDto, boolean flag) {
        if (orderDto == null) {
            throw new NotFoundException("L'ordine inserito non puo' essere nullo.");
        }

        if(orderDto.getCode() == null || orderDto.getCode().isEmpty()){
            throw new NotFoundException("Il codice dell'ordine e' nullo o vuoto.");
        }

        if(orderDto.getStateDto() == null || orderDto.getStateDto().getState() == null || orderDto.getStateDto().getState().isEmpty()){
            throw new NotFoundException("Lo stato dell'ordine e' nullo o vuoto.");
        }

        Order order = orderRepository.findByCode(orderDto.getCode())
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun ordine con questo codice: " + orderDto.getCode()));

        State state = stateRepository.findByState(orderDto.getStateDto().getState())
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessuno stato con questo nome: " + (orderDto.getStateDto().getState())));
        
        if(order.getState() instanceof StateWaitingApproval){
            throw new IllegalArgumentException("L'ordine non puo' essere modificato, perche' prima deve essere approvato o meno.");
        }

        if(order.getState() instanceof StateDeleted){
            throw new IllegalArgumentException("L'ordine non puo' essere modificato, perche' è stato eliminato.");
        }

        State currentState = order.getState();

        //Pattern State
        if(state instanceof StateDeleted){
            List<CartItem> listCartItems = order.getCart().getCartItems();
            Double total = order.getTotal();
            CreditCard creditCard = order.getCreditCard();
            currentState.delete(order);

            for (CartItem cartItem : listCartItems) {
                if(cartItem.getCode().equals(cartItem.getProduct().getCode()) ||
                   cartItem.getName().equals(cartItem.getProduct().getName())){
                    cartItem.getProduct().setQuantity(cartItem.getProduct().getQuantity() + cartItem.getQuantity());
                    productRepository.save(cartItem.getProduct());
                }
            }

            creditCard.setBalance(creditCard.getBalance() + total);
            order.getCart().setCartItems(listCartItems);
        }
        else if(flag){
            if(currentState instanceof StateDelivered){
                throw new IllegalArgumentException("Non e' possibile andare allo stato successivo.");
            }
            currentState.next(order);
        }
        else{
            if(currentState instanceof StatePreparation){
                throw new IllegalArgumentException("Non e' possibile ritornare allo stato precedente.");
            }
            currentState.previous(order);
        }

        emailService.sendSimpleEmail(order.getClient().getEmail(), "Order: " + order.getCode() + " - Status Update.",
        "Il tuo ordine dal codice: " + order.getCode() + " in stato: " + order.getState().getState().toUpperCase() + ".");

        return new OrderDto(orderRepository.save(order));
    }

    @Override
    public List<OrderDto> getListOrderWithoutSpecificState() {
        State statePreparation = stateRepository.findByState(new StatePreparation().getState())
        .orElseThrow(() -> new InternalError("Errore nella ricerca dello stato."));

        State stateInTransit = stateRepository.findByState(new StateInTransit().getState())
        .orElseThrow(() -> new InternalError("Errore nella ricerca dello stato."));
        
        List<Order> listOrder = orderRepository.findAllWithoutStates(statePreparation.getId(), stateInTransit.getId());
        if (listOrder.isEmpty()) {
            throw new NoSuchElementException();
        }
        List<OrderDto> listOrderDto = listOrder.stream().map(entity -> new OrderDto(entity))
                .collect(Collectors.toList());
        return listOrderDto;
    }

    @Override
    public List<OrderDto> getListOrderWaiting() {
        State state = stateRepository.findByState(new StateWaitingApproval().getState())
        .orElseThrow(() -> new InternalError("Errore nella ricerca dello stato."));

        List<Order> listOrder = orderRepository.findWaitingOrders(state.getId());
        if (listOrder.isEmpty()) {
            throw new NoSuchElementException();
        }
        List<OrderDto> listOrderDto = listOrder.stream().map(entity -> new OrderDto(entity))
                .collect(Collectors.toList());
        return listOrderDto;
    }

    @Override
    public List<OrderDto> getListOrderHistory() {
        State stateDeleted = stateRepository.findByState(new StateDeleted().getState())
        .orElseThrow(() -> new InternalError("Errore nella ricerca dello stato."));

        State stateDelivered = stateRepository.findByState(new StateDelivered().getState())
        .orElseThrow(() -> new InternalError("Errore nella ricerca dello stato."));

        List<Order> listOrder = orderRepository.findAllWithoutStates(stateDeleted.getId(), stateDelivered.getId());
        if (listOrder.isEmpty()) {
            throw new NoSuchElementException();
        }
        List<OrderDto> listOrderDto = listOrder.stream().map(entity -> new OrderDto(entity))
                .collect(Collectors.toList());
        return listOrderDto;
    }

    @Override
    public List<OrderDto> getListOrderUser(String username) {
        if(username == null || username.isEmpty()){
            throw new NotFoundException("Lo username e' nullo o vuoto.");
        }

        List<Order> listOrder = orderRepository.findAllByClientUsername(username);
        if (listOrder.isEmpty()) {
            throw new NoSuchElementException();
        }
        List<OrderDto> listOrderDto = listOrder.stream().map(entity -> new OrderDto(entity))
                .collect(Collectors.toList());
        return listOrderDto;
    }

    @Override
    @Transactional
    public void approvedOrNotOrder(String code, Boolean value) {
        if(value == null){
            throw new IllegalArgumentException("Il valore booleano, non puo' essere nullo.");
        }

        if(code == null || code.isEmpty()){
            throw new NotFoundException("Il codice dell'ordine e' nullo o vuoto.");
        }

        Order order = orderRepository.findByCode(code)
        .orElseThrow(() -> new IllegalArgumentException("Non e' stato trovato nessun ordine con questo codice: " + code));

        Double total = order.getTotal();
        List<CartItem> listCartItems = order.getCart().getCartItems();

        if (value) {
            CreditCard creditCard = order.getCreditCard();
            //Pattern state
            State state = order.getState();
            state.next(order);
            order.getCart().setCartItems(listCartItems);
            
            if (total > creditCard.getBalance()) {
                throw new IllegalStateException("Il bilancio della carta di credito, deve essere >= di " + total + "!");
            }
            creditCard.setBalance(creditCard.getBalance() - total);
            
            orderRepository.save(order);

            emailService.sendSimpleEmail(order.getClient().getEmail(), "Order: " + order.getCode() + " - Status Update.",
            "Il tuo ordine dal codice: " + order.getCode() + " in stato: " + order.getState().getState().toUpperCase() + ".");
        } else {
            List<Client> listClient = new ArrayList<>();
            //Pattern Observer
            for (CartItem cartItem : listCartItems) {
                listClient = orderRepository.findAllClientWhoHaveBuyADeliveredProduct(new StateDelivered().getState(), cartItem.getProduct().getCode(), cartItem.getProduct().getName()); 
                cartItem.getProduct().getObserversProduct().addAll(listClient);
                cartItem.getProduct().addObserver(cartItem.getProduct().getPharmacist());

                if(cartItem.getCode().equals(cartItem.getProduct().getCode()) &&
                   cartItem.getName().equals(cartItem.getProduct().getName())){
                    cartItem.getProduct().setQuantity(cartItem.getProduct().getQuantity() + cartItem.getQuantity());
                    productRepository.save(cartItem.getProduct());
                }
            }

            order.getCart().getCartItems().forEach(cartItem -> cartItem.getProduct().notifyObservers(emailService));
            
            //Pattern state
            State state = order.getState();
            state.delete(order);
            order.getCart().setCartItems(listCartItems);

            orderRepository.save(order);

            emailService.sendSimpleEmail(order.getClient().getEmail(),
            "Order: " + order.getCode() + " - Status Update.",
            "Il tuo ordine dal codice: " + order.getCode() + " in stato: " + order.getState().getState().toUpperCase() + ".\n" +
            "Ricrea l'ordine e ricorda di inserire delle ricette leggibili.");
        }
    }

    private String generateCode() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] token = new byte[16];
        secureRandom.nextBytes(token);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(token);
    }

}
