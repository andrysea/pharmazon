package com.andreamarino.pharmazon.service.implementationClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.andreamarino.pharmazon.dto.CartItemDto;
import com.andreamarino.pharmazon.dto.ProductDto;
import com.andreamarino.pharmazon.exception.NotFoundException;
import com.andreamarino.pharmazon.model.Cart;
import com.andreamarino.pharmazon.model.CartItem;
import com.andreamarino.pharmazon.model.Client;
import com.andreamarino.pharmazon.model.Product;
import com.andreamarino.pharmazon.model.designPattern.state.State;
import com.andreamarino.pharmazon.model.designPattern.state.StateDelivered;
import com.andreamarino.pharmazon.repository.CartItemRepository;
import com.andreamarino.pharmazon.repository.CartRepository;
import com.andreamarino.pharmazon.repository.ProductRepository;
import com.andreamarino.pharmazon.repository.StateRepository;
import com.andreamarino.pharmazon.repository.UserRepository;
import com.andreamarino.pharmazon.service.interfaceForClass.CartService;
import com.andreamarino.pharmazon.services.mail.service.EmailService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService{

    @Autowired
    private final CartRepository cartRepository;

    @Autowired
    private final CartItemRepository cartItemRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ProductRepository productRepository;

    @Autowired
    private final StateRepository stateRepository;

    @Override
    @Transactional
    public ProductDto insertProductDto(ProductDto productDto, String username) {
        if(productDto == null){
            throw new NotFoundException("L'oggetto relativo al prodotto non può essere nullo.");
        }

        if(productDto.getCode() == null || productDto.getCode().isEmpty()){
            throw new NotFoundException("Il codice prodotto e' nullo o vuoto.");
        }

        if(username == null || username.isEmpty()){
            throw new NotFoundException("Lo username e' nullo o vuoto.");
        }

        Product product = productRepository.findByCode(productDto.getCode())
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun prodotto con questo codice: " + productDto.getCode()));

        if (product.getQuantity() == 0 || !product.getActiveProduct()) {
            throw new IllegalArgumentException("Il prodotto con codice " + productDto.getCode() + " non e' disponibile.");
        }

        Client client = (Client) userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun utente con questo username: " + username));
        
        Cart cart = cartRepository.getCartOfClient(username)
        .orElse(null);

        if(cart == null){
            cart = new Cart();
            cart.setClient(client);
            
            CartItem cartItem = new CartItem();
            cartItem.setName(product.getName());
            cartItem.setPrice(product.getPrice());
            cartItem.setCode(product.getCode());
            cartItem.setQuantity(1);
            cartItem.setCart(cart);
            cartItem.setProduct(product);

            cart.setCartItems(new ArrayList<>());
            cart.getCartItems().add(cartItem);
            cart = cartRepository.save(cart);
        }
        else{
            CartItem cartItemNew = cartItemRepository.existCartItemInCart(product.getId(), cart.getId())
            .orElse(null);

            if(cartItemNew==null){
                CartItem cartItem = new CartItem();
                cartItem.setCart(cart);
                cartItem.setProduct(product);
                cartItem.setName(product.getName());
                cartItem.setCode(product.getCode());
                cartItem.setPrice(product.getPrice());
                cartItem.setQuantity(1);
    
                cart.setCartItems(new ArrayList<>());
                cart.getCartItems().add(cartItem);
                cart = cartRepository.save(cart);
            }
            else{
                if (cartItemNew.getProduct().getQuantity() > cartItemNew.getQuantity()) {
                    cartItemNew.setQuantity(cartItemNew.getQuantity() + 1);
                    cartItemRepository.save(cartItemNew);
                }
                else{
                    throw new IllegalStateException("Il prodotto con codice: " + productDto.getCode() + " e' gia' nel carrello con la quantita' massima.");
                }
            }
        }
        return productDto;
    }

    @Override
    @Transactional
    public List<CartItemDto> getCartItemsDtoCart(String username) {
        if(username == null || username.isEmpty()){
            throw new NotFoundException("Lo username e' nullo o vuoto.");
        }

        Client client = (Client) userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun utente con questo username: " + username));
        
        Cart cart = cartRepository.getCartOfClient(username)
        .orElse(null);

        List<CartItem> listCartItem = null;

        if(cart == null){
            cart = new Cart();
            cart.setClient(client);
            cart = cartRepository.save(cart);
        }
        
        listCartItem = cartItemRepository.findCartItemsInCart(cart.getId(), client.getId());
        if (listCartItem.isEmpty()) {
            throw new NoSuchElementException();
        }

        listCartItem.forEach(cartItem -> {
            if (cartItem.getQuantity() > cartItem.getProduct().getQuantity()) {
                cartItem.setQuantity(cartItem.getProduct().getQuantity());
                cartItemRepository.save(cartItem);
            }
        });
     
        List<CartItemDto> listCartItemDto = listCartItem.stream().map(entity -> new CartItemDto(entity)).collect(Collectors.toList());
        return listCartItemDto;
    }

    @Override
    @Transactional      
    public void removeProduct(String username, String code) {
        if(username == null || username.isEmpty()){
            throw new NotFoundException("Lo username e' nullo o vuoto.");
        }

        if(code == null || code.isEmpty()){
            throw new NotFoundException("Il codice e' nullo o vuoto.");
        }

        Product product = productRepository.findByCode(code)
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun prodotto con questo codice: " + code));

        Cart cart = cartRepository.getCartOfClient(username)
        .orElse(null);

        if(cart == null){
            throw new IllegalArgumentException("Non hai ancora inserito nessun prodotto nel tuo carrello.");
        }
        else{
            CartItem cartItemNew = cartItemRepository.existCartItemInCart(product.getId(), cart.getId())
            .orElse(null);

            if(cartItemNew != null){
                if(cartItemNew.getQuantity() > 1){
                    cartItemNew.setQuantity(cartItemNew.getQuantity() - 1 );
                    cartRepository.save(cart);
                }
                else{
                    cartItemRepository.deleteCartItemFromCart(cart.getId(), cartItemNew.getId());
                }
            }
            else{
                throw new IllegalArgumentException("Il prodotto non è presente nel carrello.");
            }
        }
    } 

    @Override
    public List<CartItemDto> getCartItemListDeliveredClient(String username){
        if(username == null || username.isEmpty()){
            throw new NotFoundException("Lo username e' nullo o vuoto.");
        }

        State stateDelivered = stateRepository.findByState(new StateDelivered().getState())
        .orElseThrow(() -> new InternalError("Errore nella ricerca dello stato."));
        
        List<CartItem> listCartItem = cartItemRepository.findCartItemsInDeliveredOrderForClient(stateDelivered.getId(), username);
        if (listCartItem.isEmpty()) {
            throw new NoSuchElementException();
        }
        List<CartItem> filteredCartItems = filterCartItems(listCartItem);

        List<CartItemDto> listCartItemDto = filteredCartItems.stream()
            .map(CartItemDto::new)
            .collect(Collectors.toList());
        
        return listCartItemDto;
    }

    private List<CartItem> filterCartItems(List<CartItem> cartItems) {
        // Mappa per tenere traccia dei cartItem con stesso name e code
        Map<String, List<CartItem>> groupedItems = new HashMap<>();

        // Raggruppa i cartItem per name e code
        for (CartItem cartItem : cartItems) {
            String key = cartItem.getName() + ":" + cartItem.getCode();
            groupedItems.computeIfAbsent(key, k -> new ArrayList<>()).add(cartItem);
        }

        // Lista filtrata per i cartItem
        List<CartItem> filteredItems = new ArrayList<>();

        // Itera sui gruppi e applica il filtro
        for (Map.Entry<String, List<CartItem>> entry : groupedItems.entrySet()) {
            List<CartItem> group = entry.getValue();
            boolean hasFeedback = group.stream().anyMatch(item -> !item.getFeedbacks().isEmpty());

            // Se nessuno dei cartItem ha feedback, aggiungi uno solo alla lista filtrata
            if (!hasFeedback) {
                // Aggiungi solo un elemento del gruppo per evitare duplicati
                filteredItems.add(group.get(0));
            }
        }
        return filteredItems;
    }

    @Override
    public void deleteCart(Long id) {
        if(id == null || id <= 0){
            throw new NotFoundException("L'id e' nullo o <= di 0.");
        }

        Cart cart = cartRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun cart con questo id: " + id));
        cartRepository.delete(cart);
    }
}
