package com.andreamarino.pharmazon.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.andreamarino.pharmazon.dto.CartItemDto;
import com.andreamarino.pharmazon.dto.ProductDto;
import com.andreamarino.pharmazon.service.interfaceForClass.CartService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@CrossOrigin(origins = "https://localhost:4200")
public class CartController {

    private final CartService cartService;

    /**
     * Gestisce la richiesta HTTP POST per l'inserimento di un nuovo prodotto nel carrello.
     * 
     * @param productDto l'oggetto ProductDto contenente i dettagli del prodotto da inserire, non può essere nullo
     * @param username il nome utente associato al carrello, non può essere nullo
     * @return una ResponseEntity contenente un messaggio di successo e l'oggetto ProductDto inserito, con uno stato di CREATED (201)
     */
    @PostMapping("/insertProduct")
    public ResponseEntity<?> insertProductDto(@RequestBody(required = true) ProductDto productDto, @RequestParam(required = true) String username){
        ProductDto productDtoInsert = cartService.insertProductDto(productDto, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Inserimento del prodotto nel carrello, avvenuto con successo.", "productDto", productDtoInsert));
    }
    

    /**
     * Gestisce la richiesta HTTP DELETE per rimuovere un prodotto dal carrello.
     * 
     * @param username il nome utente associato al carrello, non può essere nullo
     * @param code il codice del prodotto da rimuovere, non può essere nullo
     * @return una ResponseEntity contenente un messaggio di successo con uno stato di OK (200)
     */
    @DeleteMapping("/removeProduct")
    public ResponseEntity<?> removeProduct(@RequestParam(required = true) String username, @RequestParam(required = true) String code){
        cartService.removeProduct(username, code);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Rimozione del prodotto nel carrello, avvenuto con successo."));
    }


    /**
     * Gestisce la richiesta HTTP GET per ottenere tutti gli elementi del carrello di un utente.
     * 
     * @param username il nome utente associato al carrello, non può essere nullo
     * @return una ResponseEntity contenente la lista di tutti gli oggetti CartItemDto nel carrello, con uno stato di OK (200)
     */
    @GetMapping("/list")
    public ResponseEntity<?> getCartItemsDtoCart(@RequestParam(required = true) String username){
        List<CartItemDto> listCartItemsDto = new ArrayList<>();
        listCartItemsDto = cartService.getCartItemsDtoCart(username);
        return ResponseEntity.status(HttpStatus.OK).body(listCartItemsDto);
    }

    /**
     * Gestisce la richiesta HTTP GET per ottenere la lista dei cart_items (prodotti del carrello) consegnati ad un cliente specifico.
     * 
     * @param username il nome utente associato ai prodotti consegnati, non può essere nullo
     * @return una ResponseEntity contenente la lista di CartItemDto dei prodotti consegnati all'utente specificato, con uno stato di OK (200)
     */
    @GetMapping("/getCartItem/delivered")
    public ResponseEntity<?> getCartItemListDeliveredClient(@RequestParam(required = true) String username){
        List<CartItemDto> listCartItemDto = new ArrayList<>();
        listCartItemDto = cartService.getCartItemListDeliveredClient(username);
        return ResponseEntity.status(HttpStatus.OK).body(listCartItemDto);
    }

    
    /**
     * Gestisce la richiesta HTTP DELETE per eliminare un carrello.
     * 
     * @param id l'identificativo del carrello da eliminare, non può essere nullo
     * @return una ResponseEntity contenente un messaggio di successo con uno stato di OK (200)
     */
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteCart(@RequestParam(required = true) Long id){
        cartService.deleteCart(id);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Il carrello è stato eliminato."));
    }
}
