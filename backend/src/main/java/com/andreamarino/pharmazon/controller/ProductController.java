package com.andreamarino.pharmazon.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.andreamarino.pharmazon.dto.ProductDto;
import com.andreamarino.pharmazon.service.interfaceForClass.ProductService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
@CrossOrigin(origins = "https://localhost:4200")
public class ProductController {

    private final ProductService productService;

    /**
     * Gestisce la richiesta HTTP POST per l'inserimento di un nuovo prodotto.
     * 
     * @param productDto l'oggetto ProductDto contenente i dettagli del prodotto da inserire, non può essere nullo
     * @param username il nome utente associato al prodotto, non può essere nullo
     * @param userDetails i dettagli dell'utente autenticato
     * @return una ResponseEntity contenente un messaggio di successo e l'oggetto ProductDto inserito, con uno stato di CREATED (201)
     */
    @PostMapping("/insert")
    public ResponseEntity<?> insertProductDto(@RequestBody(required = true) ProductDto productDto, @RequestParam(required = true) String username, @AuthenticationPrincipal UserDetails userDetails){
        if(userDetails.getUsername().equals(username)){
            ProductDto productDtoInsert = productService.insertProductDto(productDto, username);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Inserimento del prodotto avvenuto con successo.", "productDto", productDtoInsert));
        }
        else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Username inserito come parametro, non e' conforme all'username legato al token."));
        }
    }


    /**
     * Gestisce la richiesta HTTP PUT per aggiornare un prodotto esistente.
     * 
     * @param productDtoNew l'oggetto ProductDto contenente i nuovi dettagli del prodotto, non può essere nullo
     * @param oldCode il vecchio codice identificativo del prodotto, non può essere nullo
     * @param userDetails i dettagli dell'utente autenticato
     * @return una ResponseEntity contenente un messaggio di successo e l'oggetto ProductDto aggiornato, con uno stato di OK (200)
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateProductDto(@RequestBody(required = true) ProductDto productDtoNew, @RequestParam(required = true) String oldCode, @AuthenticationPrincipal UserDetails userDetails){
        ProductDto productDtoUpdated = productService.updateProductDto(productDtoNew, oldCode, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Modifica del prodotto avvenuto con successo.", "productDto", productDtoUpdated));
    }


    /**
     * Gestisce la richiesta HTTP GET per ottenere la lista dei prodotti associati ad un utente specifico.
     * 
     * @param username il nome utente associato ai prodotti, non può essere nullo
     * @return una ResponseEntity contenente la lista di ProductDto dell'utente specificato, con uno stato di OK (200)
     */
    @GetMapping("/list")
    public ResponseEntity<?> getProductDto(@RequestParam(required = true) String username){
        List<ProductDto> listProductDto = new ArrayList<>();
        listProductDto = productService.getProductListDto(username);
        return ResponseEntity.status(HttpStatus.OK).body(listProductDto);
    }


    /**
     * Gestisce la richiesta HTTP GET per ottenere la lista dei prodotti per nome.
     * 
     * @param name il nome dei prodotti da cercare, non può essere nullo
     * @param userDetails i dettagli dell'utente autenticato
     * @return una ResponseEntity contenente la lista di ProductDto dei prodotti trovati, con uno stato di OK (200)
     */
    @GetMapping("/listByName")
    public ResponseEntity<?> getProductDtoName(@RequestParam(required = true) String name, @AuthenticationPrincipal UserDetails userDetails){
        List<ProductDto> listProductDto = new ArrayList<>();
        listProductDto = productService.getProductListDtoByName(name, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(listProductDto);
    }


    /**
     * Gestisce la richiesta HTTP GET per ottenere i dettagli di un prodotto tramite il suo codice.
     * 
     * @param code il codice identificativo del prodotto, non può essere nullo
     * @param userDetails i dettagli dell'utente autenticato
     * @return una ResponseEntity contenente l'oggetto ProductDto del prodotto trovato, con uno stato di OK (200)
     */
    @GetMapping("/getProduct")
    public ResponseEntity<?> getProductDtoCode(@RequestParam(required = true) String code, @AuthenticationPrincipal UserDetails userDetails){
        ProductDto productDto = productService.getProductDtoCode(code, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(productDto);
    }


    /**
     * Gestisce la richiesta HTTP GET per ottenere la lista dei prodotti per categoria.
     * 
     * @param name il nome della categoria dei prodotti da cercare, non può essere nullo
     * @param userDetails i dettagli dell'utente autenticato
     * @return una ResponseEntity contenente la lista di ProductDto dei prodotti trovati nella categoria, con uno stato di OK (200)
     */
    @GetMapping("/listByCategory")
    public ResponseEntity<?> getProductDtoCategory(@RequestParam(required = true) String name, @AuthenticationPrincipal UserDetails userDetails){
        List<ProductDto> listProductDto = new ArrayList<>();
        listProductDto = productService.getProductListDtoByCategory(name, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(listProductDto);
    }

    /**
     * Gestisce la richiesta HTTP PUT per attivare un prodotto.
     * 
     * @param code il codice identificativo del prodotto da attivare, non può essere nullo
     * @return una ResponseEntity contenente un messaggio di successo con uno stato di OK (200)
     */
    @PutMapping("/activate")
    public ResponseEntity<?> activateProductDto(@RequestBody(required = true) String code){
        productService.activateProductDto(code);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Cambiamento di stato del prodotto avvenuto con successo."));
    }
}
