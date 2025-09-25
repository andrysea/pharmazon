package com.andreamarino.pharmazon.service.implementationClass;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.andreamarino.pharmazon.dto.ProductDto;
import com.andreamarino.pharmazon.exception.DuplicateException;
import com.andreamarino.pharmazon.exception.NotFoundException;
import com.andreamarino.pharmazon.model.CartItem;
import com.andreamarino.pharmazon.model.Category;
import com.andreamarino.pharmazon.model.Client;
import com.andreamarino.pharmazon.model.Order;
import com.andreamarino.pharmazon.model.Pharmacist;
import com.andreamarino.pharmazon.model.Product;
import com.andreamarino.pharmazon.model.User;
import com.andreamarino.pharmazon.model.designPattern.state.State;
import com.andreamarino.pharmazon.model.designPattern.state.StateDeleted;
import com.andreamarino.pharmazon.model.designPattern.state.StateDelivered;
import com.andreamarino.pharmazon.model.designPattern.state.StateWaitingApproval;
import com.andreamarino.pharmazon.repository.CategoryRepository;
import com.andreamarino.pharmazon.repository.OrderRepository;
import com.andreamarino.pharmazon.repository.ProductRepository;
import com.andreamarino.pharmazon.repository.StateRepository;
import com.andreamarino.pharmazon.repository.UserRepository;
import com.andreamarino.pharmazon.security.user.Role;
import com.andreamarino.pharmazon.service.interfaceForClass.ProductService;
import com.andreamarino.pharmazon.services.mail.service.EmailService;
import com.andreamarino.pharmazon.util.ValidationUtils;
import jakarta.transaction.Transactional;
import com.andreamarino.pharmazon.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import java.util.Iterator;
import org.springframework.security.core.userdetails.UserDetails;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
    
    @Autowired
    private final ProductRepository productRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final OrderRepository orderRepository;

    @Autowired
    private final CategoryRepository categoryRepository;

    @Autowired
    private final EmailService emailService;

    @Autowired 
    private final CartItemRepository cartItemRepository;

    @Override
    @Transactional
    public ProductDto insertProductDto(ProductDto productDto, String username) { 
        if(username == null || username.isEmpty()){
            throw new NotFoundException("Lo username e' nullo o vuoto.");
        }
        this.checkProduct(productDto);
        
        if(productRepository.findByCode(productDto.getCode()).isPresent()){
            throw new DuplicateException("Il nuovo codice relativo al prodotto, fa riferimento ad un altro prodotto.");
        }

        Category category = categoryRepository.findByName(productDto.getCategoryDto().getName())
        .orElseThrow(() -> new NotFoundException("Non e' stata trovata nessuna categoria con questo nome."));

        Pharmacist pharmacist = (Pharmacist) userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun utente con questo username."));

        Product product = new Product(productDto);
        product.setCategory(category);
        product.setPharmacist(pharmacist);
        return new ProductDto(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductDto updateProductDto(ProductDto productDtoNew, String oldCode, UserDetails userDetails) {    
        String username = userDetails.getUsername();
        boolean flag = false;

        this.checkProduct(productDtoNew);
        Product productOld = productRepository.findByCode(oldCode)
        .orElseThrow(() -> new IllegalArgumentException("Non e' stato trovato nessun prodotto con questo codice: " + oldCode));

        //Verifico che il prodotto venga modificato
        //soltanto quando non ci sono ordini che devono essere consegnati
        for(CartItem cartItem: productOld.getCartItems()){
            for(Order order: cartItem.getCart().getOrders()){
                if(order.getState() instanceof StateWaitingApproval){
                    throw new IllegalArgumentException("Prodotto non modificabile.\nCi sono degli ordini che devono essere ancora approvati.");
                }
            }
        }

        Pharmacist pharmacist = (Pharmacist) userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun utente con questo username: " + userDetails.getUsername()));
        productOld.setPharmacist(pharmacist);

        if (!productDtoNew.getDescription().equals(productOld.getDescription())) {
            flag = true;
            productOld.setDescription(productDtoNew.getDescription());
        }

        if (!productDtoNew.getImage().equals(productOld.getImage())) {
            productOld.setImage(productDtoNew.getImage());
        }

        if (!productDtoNew.getName().equals(productOld.getName())) {
            flag = true;
            productOld.setName(productDtoNew.getName());
        }

        if (productDtoNew.getPrescription().booleanValue() != productOld.getPrescription().booleanValue()) {
            flag = true;
            productOld.setPrescription(productDtoNew.getPrescription());
        }

        if (productDtoNew.getActiveProduct().booleanValue() != productOld.getActiveProduct().booleanValue()) {
            flag = true;
            productOld.setActiveProduct(productDtoNew.getActiveProduct());
        }

        if (productDtoNew.getPrice().compareTo(productOld.getPrice()) != 0) {
            flag = true;
            productOld.setPrice(productDtoNew.getPrice());
        }

        if (!productDtoNew.getCode().equals(productOld.getCode())) {
            flag = true;
            if(productRepository.findByCode(productDtoNew.getCode()).isPresent()){
                throw new DuplicateException("Il nuovo codice relativo al prodotto, fa riferimento ad un altro prodotto.");
            }
            productOld.setCode(productDtoNew.getCode());
        }

        if (!productDtoNew.getCategoryDto().getName().equals(productOld.getCategory().getName())){
            flag = true;
            Category category = categoryRepository.findByName(productDtoNew.getCategoryDto().getName())
            .orElseThrow(() -> new IllegalArgumentException("Non e' stata trovata nessuna categoria con questo nome."));
            productOld.setCategory(category);
        }

        if (!productDtoNew.getProducer().equals(productOld.getProducer())) {
            flag = true;
            productOld.setProducer(productDtoNew.getProducer());
        }

        if (!productDtoNew.getActiveIngredient().equals(productOld.getActiveIngredient())) {
            flag = true;
            productOld.setActiveIngredient(productDtoNew.getActiveIngredient());
        }

        if (productDtoNew.getQuantity() != productOld.getQuantity()) {
            flag = true;
            productOld.setQuantity(productDtoNew.getQuantity());

            //Pattern Observer
            List<Client> listClient = orderRepository.findAllClientWhoHaveBuyADeliveredProduct(new StateDelivered().getState(), productOld.getCode(), productOld.getName()); 
            
            productOld.addObserver(productOld.getPharmacist());
            productOld.getObserversProduct().addAll(listClient);
            productOld.notifyObservers(emailService);
        }

        //Se il prodotto ha subito modifiche avviso il cliente e rimuovo il prodotto dal carrello
        if(flag){
            Iterator<CartItem> iterator = productOld.getCartItems().iterator();
            while (iterator.hasNext()) {
                CartItem cartItem = iterator.next();

                if (!cartItem.isFlag()) {
                    String text = "Gentile cliente, " +  "\n" +
                    "la informiamo che il prodotto da lei inserito nel carrello: " + productOld.getName() + "\n" +
                    "e' stato modificato e verra' rimosso dal carrello.";

                    emailService.sendSimpleEmail(cartItem.getCart().getClient().getEmail(), "ELIMINAZIONE PRODOTTO DAL CARRELLO", text);

                    iterator.remove();
                    cartItemRepository.delete(cartItem);
                }
            }
        }

        Product newProduct = productRepository.save(productOld);
        return new ProductDto(newProduct);
    }

    private void checkProduct(ProductDto productDto) {     
        if (productDto == null) {
            throw new NotFoundException("Il prodotto inserito non puo' essere nullo.");
        }
        
        ValidationUtils.validateString(productDto.getCode(), "codice prodotto");
        if(productDto.getCode().length() > 40){
            throw new IllegalArgumentException("Il codice del prodotto puo' avere massimo 40 caratteri.");
        }

        ValidationUtils.isValidBase64(productDto.getImage());

        if(productDto.getName() == null || productDto.getName().isEmpty()){
            throw new IllegalArgumentException("Il nome del prodotto e' nullo o vuoto.");
        }

        if(productDto.getName().length() > 40){
            throw new IllegalArgumentException("Il nome del prodotto puo' avere massimo 40 caratteri.");
        }

        if(productDto.getProducer() == null || productDto.getProducer().isEmpty()){
            throw new IllegalArgumentException("Il nome del produttore e' nullo o vuoto.");
        }

        if(productDto.getProducer().length() > 30){
            throw new IllegalArgumentException("Il nome del produttore puo' avere massimo 30 caratteri.");
        }

        if(productDto.getActiveIngredient() == null || productDto.getActiveIngredient().isEmpty()){
            throw new IllegalArgumentException("Il nome dell'ingrediente attivo, e' nullo o vuoto.");
        }

        if(productDto.getActiveIngredient().length() > 20){
            throw new IllegalArgumentException("Il nome dell'ingrediente attivo puo' avere massimo 20 caratteri.");
        }

        if(productDto.getPrescription() == null){
            throw new IllegalArgumentException("Il valore relativo alla richiesta o meno di una prescrizione, non puo' essere nullo.");
        }

        if(productDto.getActiveProduct() == null){
            throw new IllegalArgumentException("Il valore relativo all'attivazione o meno del prodotto, non puo' essere nullo.");
        }
        
        if(productDto.getDescription() == null || productDto.getDescription().isBlank()){
            throw new IllegalArgumentException("La descrizione del prodotto inserito non puo' essere nullo o vuota.");
        }

        if(productDto.getDescription().length() > 200){
            throw new IllegalArgumentException("La descrizione del prodotto puo' avere massimo 200 caratteri.");
        }

        if(productDto.getPrice() == null || productDto.getPrice() <= 0){
            throw new IllegalArgumentException("Il prezzo del prodotto inserito non puo' essere nullo o <= 0.");
        }

        if(productDto.getCategoryDto() == null){
            throw new IllegalArgumentException("La categoria del prodotto inserito non puo' essere nulla.");
        }
        ValidationUtils.containsOnlyLetters(productDto.getCategoryDto().getName(), "nome categoria");

        if(productDto.getQuantity() == null || productDto.getQuantity() < 0){
            throw new IllegalArgumentException("Il valore dell'ammontare del prodotto inserito non puo' essere nullo o < 0.");
        }
    }

    @Override
    public List<ProductDto> getProductListDto(String username) {  
        List<Product> listProduct = new ArrayList<>();
        if(username == null || username.isEmpty()){
            throw new NotFoundException("Lo username e' nullo o vuoto.");
        }

        User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun utente con questo username."));

        if(user.getRole().equals(Role.ADMIN)){
            listProduct = productRepository.findAll();
        }
        else{
            listProduct = productRepository.findByActiveProductAndQuantity();
        }

        if (listProduct.isEmpty()) {
            throw new NoSuchElementException();
        }
        List<ProductDto> listProductDto = listProduct.stream().map(entity -> new ProductDto(entity)).collect(Collectors.toList());
        return listProductDto;
    }

    @Override
    public List<ProductDto> getProductListDtoByName(String name, UserDetails userDetails) {    
        if(name == null){
            throw new NotFoundException("Il nome del prodotto e' nullo.");
        }

        User user = (User) userRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun utente con questo username: " + userDetails.getUsername()));

        List<Product> listProduct = productRepository.findByName(name);

        if(user.getRole().equals(Role.CLIENT)){
            listProduct.removeIf(product -> product.getQuantity() == 0 || (product.getActiveProduct() == false));
        }

        if (listProduct.isEmpty()) {
            throw new NoSuchElementException();
        }
   
        List<ProductDto> listProductDto = listProduct.stream().map(entity -> new ProductDto(entity)).collect(Collectors.toList());
        return listProductDto;
    }

    @Override
    public ProductDto getProductDtoCode(String code, UserDetails userDetails) {     
        if(code == null || code.isEmpty()){
            throw new NotFoundException("Il codice del prodotto e' nullo o vuoto.");
        }

        User user = (User) userRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun utente con questo username: " + userDetails.getUsername()));

        Product product = productRepository.findByCode(code)
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun prodotto con questo codice: " + code));

        if(user.getRole().equals(Role.CLIENT)){
            if(product.getQuantity() == 0 || !product.getActiveProduct()){
                throw new NotFoundException("Non e' stato trovato nessun prodotto disponibile con questo codice: " + code);
            }
        }
  
        ProductDto productDto = new ProductDto(product);
        return productDto;
    }

    @Override
    public List<ProductDto> getProductListDtoByCategory(String name, UserDetails userDetails) {     
        if(name == null || name.isEmpty()){
            throw new NotFoundException("Il nome della categoria e' nullo o vuoto.");
        }

        User user = (User) userRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun utente con questo username: " + userDetails.getUsername()));

        List<Product> listProduct = new ArrayList<>();

        if(user.getRole().equals(Role.CLIENT)){
            listProduct = productRepository.findByCategoryCheckQuantity(name);
        }
        else{
            listProduct = productRepository.findByCategory(name);
        }

        if (listProduct.isEmpty()) {
            throw new NoSuchElementException();
        }
        List<ProductDto> listProductDto = listProduct.stream().map(entity -> new ProductDto(entity)).collect(Collectors.toList());
        return listProductDto;
    }

    @Override
    @Transactional
    public void activateProductDto(String code) {
        if(code == null || code.isEmpty()){
            throw new NotFoundException("Il codice del prodotto e' nullo o vuoto.");
        }

        Product product= productRepository.findByCode(code)
        .orElseThrow(() -> new NotFoundException("Non e' stato trovato nessun prodotto con questo codice: " + code));
        
        if(product.getActiveProduct()){
            product.setActiveProduct(false);
           
            Iterator<CartItem> iterator = product.getCartItems().iterator();
            while (iterator.hasNext()) {
                CartItem cartItem = iterator.next();

                //Se il prodotto da disattivare è già presente in un carrello, ma non
                //è stato effettuato l'ordine dal cliente, lo rimuovo
                if (!cartItem.isFlag()) {
                    String text = "Gentile cliente, " +  "\n" +
                    "la informiamo che il prodotto da lei inserito nel carrello: " + product.getName() + "\n" +
                    "non e' piu' disponibile, verra' rimosso dal carrello.";

                    emailService.sendSimpleEmail(cartItem.getCart().getClient().getEmail(), "ELIMINAZIONE PRODOTTO DAL CARRELLO", text);

                    iterator.remove();
                    cartItemRepository.delete(cartItem);
                }
            }
        }
        else{
            product.setActiveProduct(true);
        }
        productRepository.save(product);
    }
}
