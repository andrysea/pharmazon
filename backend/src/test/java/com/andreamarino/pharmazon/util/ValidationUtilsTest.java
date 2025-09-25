package com.andreamarino.pharmazon.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Calendar;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.andreamarino.pharmazon.security.user.Role;

@ExtendWith(MockitoExtension.class)
public class ValidationUtilsTest {

    @Test
    public void containsOnlyLetters_WhenValidInput_ReturnTrue() {
        //Setup
        String input = "abcd";
        String object = "testo esempio";

        //Test
        assertTrue(ValidationUtils.containsOnlyLetters(input, object));
    }

    @Test
    public void containsOnlyLetters_WhenInvalidInputValidMixedCase_ReturnTrue() {
        //Setup
        String input = "AbCdEf";
        String object = "testo esempio";

        //Test
        assertTrue(ValidationUtils.containsOnlyLetters(input, object));
    }

    
    @Test
    public void containsOnlyLetters_WhenInvalidInputContainsNumbers_IllegalArgumentException() {
        //Setup
        String input = "Ciao2";
        String object = "testo esempio";

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.containsOnlyLetters(input, object));
        
        assertEquals("Il valore di " + object + " inserito deve contenere solo lettere.",  exception.getMessage());  
    }

    @Test
    public void containsOnlyLetters_WhenInvalidInputContainsNumbersLength_IllegalArgumentException() {
        //Setup
        String input = "ValentinaRossiBellini";
        String object = "testo esempio";

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.containsOnlyLetters(input, object));
        
        assertEquals("Il valore di " + object + " inserito non puo' contenere più di 20 caratteri.",  exception.getMessage());  
    }


    @Test
    public void containsOnlyLetters_WhenInvalidInputContainsSpace_IllegalArgumentException() {
        //Setup
        String input = "Ciao ";
        String object = "testo esempio";

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.containsOnlyLetters(input, object));
        
        assertEquals("Il valore di " + object + " inserito non puo' contenere spazi.",  exception.getMessage());  
    }

    @Test
    public void containsOnlyLetters_WhenInvalidInputNull_IllegalArgumentException() {
        //Setup
        String input = null;
        String object = "testo esempio";

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.containsOnlyLetters(input, object));
        
        assertEquals("Il valore di " + object + " inserito non puo' essere nullo o vuoto.",  exception.getMessage());  
    }

    @Test
    public void containsOnlyLetters_WhenInvalidInputBlank_IllegalArgumentException() {
        //Setup
        String input = "";
        String object = "testo esempio";

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.containsOnlyLetters(input, object));
        
        assertEquals("Il valore di " + object + " inserito non puo' essere nullo o vuoto.",  exception.getMessage());  
    }

    @Test
    public void isValidCreditCard_WhenValidInput_ReturnTrue(){
        //Setup
        String number = "1234567890123456";
        String cartSecurityCode = "123";
        String expirationDate = "2030-05";

        //Test
        assertTrue(ValidationUtils.isValidCreditCard(number, cartSecurityCode, expirationDate));
    }

    @Test
    public void isValidCreditCard_WhenInvalidInputExpirationDate_IllegalArgumentException(){
        //Setup
        String number = "1234567890123456";
        String cartSecurityCode = "123";
        String expirationDate = "2024-04";

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.isValidCreditCard(number, cartSecurityCode, expirationDate));
        assertEquals("La data di scadenza della carta di credito, e' antecedente rispetto alla data attuale.",  exception.getMessage());  
    }

    @Test
    public void isValidCreditCard_WhenInvalidInputExpirationDateFormat_IllegalArgumentException(){
        //Setup
        String number = "1234567890123456";
        String cartSecurityCode = "123";
        String expirationDate = "2024";

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.isValidCreditCard(number, cartSecurityCode, expirationDate));
        assertEquals("La data di scadenza della carta di credito non e' valida. Giusto formato in cui inserirla: (yyyy-MM).",  exception.getMessage());  
    }

    @Test
    public void isValidCreditCard_WhenInvalidInputExpirationDateNull_IllegalArgumentException(){
        //Setup
        String number = "1234567890123456";
        String cartSecurityCode = "123";
        String expirationDate = null;

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.isValidCreditCard(number, cartSecurityCode, expirationDate));
        assertEquals("La data di scadenza non puo' essere nulla o vuota.",  exception.getMessage());  
    }

    @Test
    public void isValidCreditCard_WhenInvalidInputExpirationDateBlank_IllegalArgumentException(){
        //Setup
        String number = "1234567890123456";
        String cartSecurityCode = "123";
        String expirationDate = "";

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.isValidCreditCard(number, cartSecurityCode, expirationDate));
        assertEquals("La data di scadenza non puo' essere nulla o vuota.",  exception.getMessage());  
    }

    @Test
    public void isValidCreditCard_WhenInvalidInputNumberLength_IllegalArgumentException(){
        //Setup
        String number = "123456789012345";
        String cartSecurityCode = "123";
        String expirationDate = "2024-05";

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.isValidCreditCard(number, cartSecurityCode, expirationDate));
        assertEquals("Il numero associato alla carta di credito deve avere 16 caratteri.",  exception.getMessage());  
    }

    @Test
    public void isValidCreditCard_WhenInvalidInputCardSecurityCodeLength_IllegalArgumentException(){
        //Setup
        String number = "1234567890123456";
        String cartSecurityCode = "12";
        String expirationDate = "2024-05";

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.isValidCreditCard(number, cartSecurityCode, expirationDate));
        assertEquals("Il codice di sicurezza deve essere a tre cifre.",  exception.getMessage());  
    }

    @Test
    public void containsOnlyNumbers_WhenValidInput_ReturnTrue(){
        //Setup
        String input = "123";
        String object = "testo esempio";

        //Test
        assertTrue(ValidationUtils.containsOnlyNumbers(input, object)); 
    }

    @Test
    public void containsOnlyNumbers_WhenInvalidInputLetters_IllegalArgumentException(){
        //Setup
        String input = "123L";
        String object = "testo esempio";

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.containsOnlyNumbers(input, object));
        assertEquals("Il valore di " + object + " inserito deve contenere solo numeri.",  exception.getMessage());  
    }

    @Test
    public void containsOnlyNumbers_WhenInvalidInputSpaces_IllegalArgumentException(){
        //Setup
        String input = "123 ";
        String object = "testo esempio";

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.containsOnlyNumbers(input, object));
        assertEquals("Il valore di " + object + " inserito non puo' contenere spazi.",  exception.getMessage());  
    }

    @Test
    public void containsOnlyNumbers_WhenInvalidInputNull_IllegalArgumentException(){
        //Setup
        String input = null;
        String object = "testo esempio";

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.containsOnlyNumbers(input, object));
        assertEquals("Il valore di " + object + " inserito non puo' essere nullo o vuoto.",  exception.getMessage());  
    }

    @Test
    public void containsOnlyNumbers_WhenInvalidInputBlank_IllegalArgumentException(){
        //Setup
        String input = "";
        String object = "testo esempio";

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.containsOnlyNumbers(input, object));
        assertEquals("Il valore di " + object + " inserito non puo' essere nullo o vuoto.",  exception.getMessage());  
    }

    @Test
    public void isValidEmail_WhenValidInput_ReturnTrue(){
        //Setup
        String email = "user@user.com";

        //Test
        assertTrue(ValidationUtils.isValidEmail(email)); 
    }

    @Test
    public void isValidEmail_WhenInvalidFormat_IllegalArgumentException(){
        //Setup
        String email = "useruser.com";

        //Test        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.isValidEmail(email));
        assertEquals("Formato email non corretto: " + email,  exception.getMessage());  
    }

    @Test
    public void isValidEmail_WhenInvalidInputFormatUpperCase_IllegalArgumentException(){
        //Setup
        String email = "USER@user.com";

        //Test        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.isValidEmail(email));
        assertEquals("La email inserita non puo' contenere caratteri maiuscoli.",  exception.getMessage());  
    }

    @Test
    public void isValidEmail_WhenInvalidInputContainsSpaces_IllegalArgumentException(){
        //Setup
        String email = "USER@user.com ";

        //Test        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.isValidEmail(email));
        assertEquals("Formato email non corretto: " + email + "\nNon inserire spazi.",  exception.getMessage());  
    }

    @Test
    public void isValidEmail_WhenInvalidInputNull_IllegalArgumentException(){
        //Setup
        String email = null;

        //Test        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.isValidEmail(email));
        assertEquals("La email non puo' essere nulla o vuota.",  exception.getMessage());  
    }

    @Test
    public void isValidEmail_WhenInvalidInputBlank_IllegalArgumentException(){
        //Setup
        String email = "";

        //Test        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.isValidEmail(email));
        assertEquals("La email non puo' essere nulla o vuota.",  exception.getMessage());  
    }

    @Test
    public void isValidRole_WhenValidInputClient_ReturnTrue(){
        //Test
        assertTrue(ValidationUtils.isValidRole(Role.CLIENT.name())); 
    }

    
    @Test
    public void isValidRole_WhenValidInputAdmin_ReturnTrue(){
        //Test
        assertTrue(ValidationUtils.isValidRole("ADMIN")); 
    }

    @Test
    public void isValidRole_WhenInvalidInput_IllegalArgumentException(){
        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.isValidRole("ruolo"));
        assertEquals("Valore di ruolo non puo' essere nullo o diverso da: " + Role.ADMIN.name() + " o " + Role.CLIENT.name() + ".",  exception.getMessage());  
    }

    @Test
    public void isValidRole_WhenInvalidInputNull_IllegalArgumentException(){        
        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.isValidRole(null));
        assertEquals("Valore di ruolo non puo' essere nullo o diverso da: " + Role.ADMIN.name() + " o " + Role.CLIENT.name() + ".",  exception.getMessage());  
    }

    @Test
    public void checkAdult_WhenValidInput_ReturnTrue(){
        //Setup
        String dateString = "2001-10-03";

        //Test
        assertTrue(ValidationUtils.checkAdult(dateString)); 
    }

    @Test
    public void checkAdult_WhenInvalidInputParse_IllegalArgumentException(){
        //Setup
        String dateString = "2001-10";

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ValidationUtils.checkAdult(dateString));
        assertEquals("La data non è nel formato corretto (yyyy-MM-dd): " + dateString,  exception.getMessage());  
    }

    
    @Test
    public void checkAdult_WhenInvalidInputYear_IllegalArgumentException(){
        //Setup
        String dateString = "1800-10-03";

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ValidationUtils.checkAdult(dateString));
        assertEquals("Data non valida: " + dateString,  exception.getMessage());  
    }

    @Test
    public void checkAdult_WhenInvalidInputMonth_IllegalArgumentException(){
        //Setup
        String dateString = "1800-12-03";

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ValidationUtils.checkAdult(dateString));
        assertEquals("Data non valida: " + dateString,  exception.getMessage());  
    }

    @Test
    public void checkAdult_WhenValidInputNo18years_IllegalArgumentException(){
        //Setup
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String dateString = currentYear + "-10-31";

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ValidationUtils.checkAdult(dateString));
        assertEquals("La persona deve avere almeno 18 anni: " + dateString,  exception.getMessage());  
    }

    @Test
    public void checkAdult_WhenInvalidInputNull_IllegalArgumentException(){
        //Setup
        String dateString = null;

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ValidationUtils.checkAdult(dateString));
        assertEquals("La data di nascita non può essere nulla o vuota.",  exception.getMessage());  
    }

    @Test
    public void checkAdult_WhenInvalidInputBlank_IllegalArgumentException(){
        //Setup
        String dateString = "";

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ValidationUtils.checkAdult(dateString));
        assertEquals("La data di nascita non può essere nulla o vuota.",  exception.getMessage());  
    }

    @Test
    public void isValidBase64_WhenValidInputPNG_ReturnTrue(){
        //Setup
        byte[] pngHeader = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};

        //Test
        assertTrue(ValidationUtils.isValidBase64(pngHeader)); 
    }

    @Test
    public void isValidBase64_WhenValidInputJPEG_ReturnTrue(){
        //Setup
        byte[] jpegHeader = {(byte) 0xFF, (byte) 0xD8};

        //Test
        assertTrue(ValidationUtils.isValidBase64(jpegHeader)); 
    }

    @Test
    public void isValidBase64_WhenValidInputJPG_ReturnTrue(){
        //Setup
        byte[] jpgHeader = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};

        //Test
        assertTrue(ValidationUtils.isValidBase64(jpgHeader)); 
    }

    @Test
    public void isValidBase64_WhenInvalidInput_IllegalArgumentException(){
        //Setup
        byte[] header = {(byte) 0xFF};
        
        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.isValidBase64(header));
        assertEquals("Immagine non valida.",  exception.getMessage()); 
    }

    @Test
    public void isValidBase64_WhenInvalidInputNull_IllegalArgumentException(){
        //Setup
        byte[] header = null;
        
        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.isValidBase64(header));
        assertEquals("L'immagine della prescrizione, non puo' essere nulla o vuota.",  exception.getMessage()); 
    }

    @Test
    public void isValidBase64_WhenInvalidInputLength0_IllegalArgumentException(){
        //Setup
        byte[] header = new byte[0];
        
        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.isValidBase64(header));
        assertEquals("L'immagine della prescrizione, non puo' essere nulla o vuota.",  exception.getMessage()); 
    }

    @Test
    public void checkDateEarly_WhenValidInput_ReturnTrue(){
        //Setup
        String date = "2024-10-03 15:00";
        
        //Test
        assertTrue(ValidationUtils.checkDateEarly(date)); 
    }

    @Test
    public void checkDateEarly_WhenInvalidInputParse_ReturnTrue(){
        //Setup
        String date = "2024-10-03";
        
        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.checkDateEarly(date));
        assertEquals("La data non e' nel giusto formato: " + date.toString(),  exception.getMessage()); 
    }

    @Test
    public void checkDateEarly_WhenInvalidInputNull_ReturnTrue(){
        //Setup
        String date = null;
        
        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.checkDateEarly(date));
        assertEquals("La data e l'orario inseriti, non puo' essere nulli.",  exception.getMessage()); 
    }
    
    @Test
    public void checkDateEarly_WhenInvalidInputBlank_ReturnTrue(){
        //Setup
        String date = "";
        
        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.checkDateEarly(date));
        assertEquals("La data e l'orario inseriti, non puo' essere nulli.",  exception.getMessage()); 
    }

    @Test
    public void checkTaxId_WhenValidInput_ReturnTrue(){
        //Setup
        String tax_id = "1234567890123456";

        //Test
        assertTrue(ValidationUtils.checkTaxId(tax_id)); 
    }

    @Test
    public void checkTaxId_WhenInvalidInputLetters_IllegalArgumentException(){
        //Setup
        String tax_id = "123456789012345Q";

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ValidationUtils.checkTaxId(tax_id));
        assertEquals("Il codice fiscale non puo' contenere le lettere I, O, Q.",  exception.getMessage()); 
    }

    @Test
    public void checkTaxId_WhenInvalidInputSpecialCharacters_IllegalArgumentException(){
        //Setup
        String tax_id = "123456789012345@";

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ValidationUtils.checkTaxId(tax_id));
        assertEquals("Il codice fiscale contiene caratteri non validi.",  exception.getMessage()); 
    }

    @Test
    public void checkTaxId_WhenInvalidInputLength_IllegalArgumentException(){
        //Setup
        String tax_id = "1234567890123";

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ValidationUtils.checkTaxId(tax_id));
        assertEquals("Il codice fiscale inserito non e' corretto.\nRicorda, massimo 16 caratteri alfanumerici e niente spazi.",  exception.getMessage()); 
    }

    @Test
    public void checkTaxId_WhenInvalidInputContainsSpaces_IllegalArgumentException(){
        //Setup
        String tax_id = "1234567890123 ";

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ValidationUtils.checkTaxId(tax_id));
        assertEquals("Il codice fiscale inserito non e' corretto.\nRicorda, massimo 16 caratteri alfanumerici e niente spazi.",  exception.getMessage()); 
    }

    @Test
    public void checkTaxId_WhenInvalidInputNull_IllegalArgumentException(){
        //Setup
        String tax_id = null;

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ValidationUtils.checkTaxId(tax_id));
        assertEquals("Il codice fiscale inserito non puo' essere nullo o vuoto.",  exception.getMessage()); 
    }

    
    @Test
    public void checkTaxId_WhenInvalidInputBlank_IllegalArgumentException(){
        //Setup
        String tax_id = " ";

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ValidationUtils.checkTaxId(tax_id));
        assertEquals("Il codice fiscale inserito non puo' essere nullo o vuoto.",  exception.getMessage()); 
    }
  
    @Test
    public void validateString_WhenValidInputBlank_ReturnTrue(){
        //Setup
        String text = "Ciao!";
        String object = "testo esempio";

        //Test
        assertTrue(ValidationUtils.validateString(text, object)); 
    }

    @Test
    public void validateString_WhenInvalidInputContainsSpaces_ReturnTrue(){
        //Setup
        String text = "Ciao mondo!";
        String object = "testo esempio";

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.validateString(text, object));
        assertEquals("Il valore inserito di " + object + " non puo' contenere spazi.",  exception.getMessage());  
    }

    @Test
    public void validateString_WhenInvalidInputNull_ReturnTrue(){
        //Setup
        String text = null;
        String object = "testo esempio";

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.validateString(text, object));
        assertEquals("Il valore di " + object + " inserito non puo' essere nullo o vuoto.",  exception.getMessage());  
    }

    @Test
    public void validateString_WhenInvalidInputBlank_ReturnTrue(){
        //Setup
        String text = " ";
        String object = "testo esempio";

        //Test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ValidationUtils.validateString(text, object));
        assertEquals("Il valore di " + object + " inserito non puo' essere nullo o vuoto.",  exception.getMessage());  
    }
}
