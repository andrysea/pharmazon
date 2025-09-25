package com.andreamarino.pharmazon.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.andreamarino.pharmazon.dto.OrderDto;
import com.andreamarino.pharmazon.service.interfaceForClass.OrderService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@CrossOrigin(origins = "https://localhost:4200")
public class OrderController {
    private final OrderService orderService;

    /**
     * Gestisce la richiesta HTTP POST per l'inserimento di un nuovo ordine.
     * 
     * @param orderDto l'oggetto OrderDto contenente i dettagli dell'ordine da inserire, non può essere nullo
     * @param username il nome utente associato all'ordine, non può essere nullo
     * @return una ResponseEntity contenente un messaggio di successo e l'oggetto OrderDto inserito, con uno stato di CREATED (201)
     */
    @PostMapping("/insert")
    public ResponseEntity<?> insertOrderDto(@RequestBody(required = true) OrderDto orderDto, @RequestParam(required = true) String username){
        OrderDto orderDtoInsert = orderService.insertOrderDto(orderDto, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Inserimento dell'ordine avvenuto con successo.", "orderDto", orderDtoInsert));
    }


    /**
     * Gestisce la richiesta HTTP PUT per aggiornare un ordine esistente.
     * 
     * @param orderDto l'oggetto OrderDto contenente i dettagli dell'ordine da aggiornare, non può essere nullo
     * @param flag il flag che indica lo stato dell'ordine
     * @return una ResponseEntity contenente un messaggio di successo e l'oggetto OrderDto aggiornato, con uno stato di OK (200)
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateOrderDto(@RequestBody(required = true) OrderDto orderDto, @RequestParam(required = true) boolean flag){
        OrderDto orderDtoUpdated = orderService.updateOrderDto(orderDto, flag);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Modifica dell'ordine avvenuta con successo.", "orderDto", orderDtoUpdated));
    }


    /**
     * Gestisce la richiesta HTTP GET per ottenere la lista degli ordini senza specifici stati.
     * 
     * @return una ResponseEntity contenente la lista di OrderDto senza specifici stati, con uno stato di OK (200)
     */
    @GetMapping("/listOrder/noSomeStates")
    public ResponseEntity<?> getListOrderWithoutSpecificState(){
        List<OrderDto> listOrderDto = new ArrayList<>();
        listOrderDto = orderService.getListOrderWithoutSpecificState();
        return ResponseEntity.status(HttpStatus.OK).body(listOrderDto);
    }


    /**
     * Gestisce la richiesta HTTP GET per ottenere la lista della cronologia degli ordini.
     * 
     * @return una ResponseEntity contenente la lista di OrderDto storici, con uno stato di OK (200)
     */
    @GetMapping("/listOrder/history")
    public ResponseEntity<?> getListOrderHistory(){
        List<OrderDto> listOrderDto = new ArrayList<>();
        listOrderDto = orderService.getListOrderHistory();
        return ResponseEntity.status(HttpStatus.OK).body(listOrderDto);
    }


    /**
     * Gestisce la richiesta HTTP GET per ottenere la lista degli ordini in attesa.
     * 
     * @return una ResponseEntity contenente la lista di OrderDto in attesa, con uno stato di OK (200)
     */
    @GetMapping("/listOrder/waiting")
    public ResponseEntity<?> getListOrderWaiting(){
        List<OrderDto> listOrderDto = new ArrayList<>();
        listOrderDto = orderService.getListOrderWaiting();
        return ResponseEntity.status(HttpStatus.OK).body(listOrderDto);
    }


    /**
     * Gestisce la richiesta HTTP GET per ottenere la lista degli ordini di un utente specifico.
     * 
     * @param username il nome utente associato agli ordini, non può essere nullo
     * @return una ResponseEntity contenente la lista di OrderDto dell'utente specificato, con uno stato di OK (200)
     */
    @GetMapping("/listOrder/user")
    public ResponseEntity<?> getListOrderUser(@RequestParam(required = true) String username){  
        List<OrderDto> listOrderDto = new ArrayList<>();
        listOrderDto = orderService.getListOrderUser(username);
        return ResponseEntity.status(HttpStatus.OK).body(listOrderDto);
    }


    /**
     * Gestisce la richiesta HTTP PUT per approvare o rifiutare un ordine.
     * 
     * @param code il codice identificativo dell'ordine, non può essere nullo
     * @param value il valore booleano che indica se l'ordine è approvato o meno
     * @return una ResponseEntity contenente un messaggio di successo con uno stato di OK (200)
     */
    @PutMapping("/checkOrder")
    public ResponseEntity<?> approvedOrNotOrder(@RequestParam(required = true) String code, @RequestParam(required = true) boolean value){
        orderService.approvedOrNotOrder(code, value);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Modifica dell'ordine avvenuta con successo."));
    }
}
