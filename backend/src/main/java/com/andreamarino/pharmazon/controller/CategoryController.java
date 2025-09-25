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
import org.springframework.web.bind.annotation.RestController;
import com.andreamarino.pharmazon.dto.CategoryDto;
import com.andreamarino.pharmazon.service.interfaceForClass.CategoryService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
@CrossOrigin(origins = "https://localhost:4200")
public class CategoryController {

    private final CategoryService categoryService;
    

    /**
     * Gestisce la richiesta HTTP POST per l'inserimento di una nuova categoria.
     * 
     * @param categoryDto l'oggetto CategoryDto contenente i dettagli della categoria da inserire, non può essere nullo
     * @return una ResponseEntity contenente un messaggio di successo e l'oggetto CategoryDto inserito, con uno stato di CREATED (201)
     */
    @PostMapping("/insert")
    public ResponseEntity<?> insertCategoryDto(@RequestBody(required = true) CategoryDto categoryDto){   
        CategoryDto categoryDtoInsert = categoryService.insertCategoryDto(categoryDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Inserimento della categoria avvenuto con successo.", "categoryDto", categoryDtoInsert));
    }


    /**
     * Gestisce la richiesta HTTP PUT per aggiornare una categoria esistente.
     * 
     * @param categoryDto l'oggetto CategoryDto contenente i dettagli della categoria da aggiornare, non può essere nullo
     * @return una ResponseEntity contenente un messaggio di successo e l'oggetto CategoryDto aggiornato, con uno stato di OK (200)
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateCategoryDto(@RequestBody(required = true) CategoryDto categoryDto){
        CategoryDto categoryDtoUpdated = categoryService.updateCategoryDto(categoryDto);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Modifica della categoria avvenuto con successo.", "categoryDto", categoryDtoUpdated));
    }


    /**
     * Gestisce la richiesta HTTP GET per ottenere tutte le categorie.
     * 
     * @return una ResponseEntity contenente la lista di tutte le CategoryDto, con uno stato di OK (200)
     */
    @GetMapping("/list")
    public ResponseEntity<?> getCategoryDto(){
        List<CategoryDto> listCategoryDto = new ArrayList<>();
        listCategoryDto = categoryService.getCategoryDto();
        return ResponseEntity.status(HttpStatus.OK).body(listCategoryDto);
    }
}
