
package com.andreamarino.pharmazon.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.andreamarino.pharmazon.security.user.Role;

public class ValidationUtils {

    public static boolean containsOnlyLetters(String input, String object) {
        if (input == null || input.isBlank()){
            throw new IllegalArgumentException("Il valore di " + object + " inserito non puo' essere nullo o vuoto.");
        }
        if(input.contains(" ")){
            throw new IllegalArgumentException("Il valore di " + object + " inserito non puo' contenere spazi.");
        }
        if (input.matches("^[a-zA-Z\\s]+$")) {
            input = input.toLowerCase();
            input = input.substring(0, 1).toUpperCase() + input.substring(1);

            if(input.length() > 20){
                throw new IllegalArgumentException("Il valore di " + object + " inserito non puo' contenere più di 20 caratteri.");
            }
            return true;
        } 
        else {
            throw new IllegalArgumentException("Il valore di " + object + " inserito deve contenere solo lettere.");
        }
    }

    public static boolean isValidCreditCard(String number, String cardSecurityCode, String expirationDate) {
        containsOnlyNumbers(cardSecurityCode, "codice di sicurezza");
        if (!cardSecurityCode.matches("\\d{3}")) {
            throw new IllegalArgumentException("Il codice di sicurezza deve essere a tre cifre.");
        }

        containsOnlyNumbers(number, "numero carta di credito");
        if(number.length() != 16){
            throw new IllegalArgumentException("Il numero associato alla carta di credito deve avere 16 caratteri.");
        }

        if(expirationDate == null || expirationDate.isBlank()){
            throw new IllegalArgumentException("La data di scadenza non puo' essere nulla o vuota.");
        }

        if (!isValidExpirationDate(expirationDate)) {
            throw new IllegalArgumentException("La data di scadenza della carta di credito, e' antecedente rispetto alla data attuale.");
        }

        return true;
    }

    public static boolean isValidExpirationDate(String expirationDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        dateFormat.setLenient(false);

        try {
            Date parsedDate = dateFormat.parse(expirationDate);
        
            Calendar currentCalendar = Calendar.getInstance();
            Calendar expirationCalendar = Calendar.getInstance();
            
            expirationCalendar.setTime(parsedDate);
            expirationCalendar.set(Calendar.DAY_OF_MONTH, expirationCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
    
            int currentMonth = currentCalendar.get(Calendar.MONTH);
            int currentYear = currentCalendar.get(Calendar.YEAR);
    
            int expirationMonth = expirationCalendar.get(Calendar.MONTH);
            int expirationYear = expirationCalendar.get(Calendar.YEAR);
    
            return (expirationYear > currentYear || (expirationYear == currentYear && expirationMonth >= currentMonth));
        } catch (ParseException e) {
            throw new IllegalArgumentException("La data di scadenza della carta di credito non e' valida. Giusto formato in cui inserirla: (yyyy-MM).");
        }
    }

    public static boolean containsOnlyNumbers(String input, String object) {
        if(input == null || input.isBlank()){
            throw new IllegalArgumentException("Il valore di " + object + " inserito non puo' essere nullo o vuoto.");
        }
        if(input.contains(" ")){
            throw new IllegalArgumentException("Il valore di " + object + " inserito non puo' contenere spazi.");
        }
        if (input.matches("^[0-9]*$")) {
            return true;
        } else {
            throw new IllegalArgumentException("Il valore di " + object + " inserito deve contenere solo numeri.");
        }
    }
    
    public static boolean isValidEmail(String email) {
        if(email == null || email.isBlank()){
            throw new IllegalArgumentException("La email non puo' essere nulla o vuota.");
        }

        if(email.contains(" ")){
            throw new IllegalArgumentException("Formato email non corretto: " + email + "\nNon inserire spazi.");
        }
        if(email.chars().anyMatch(Character::isUpperCase)) {
            throw new IllegalArgumentException("La email inserita non puo' contenere caratteri maiuscoli.");
        }

        int minLength = 5;
        int maxLength = 254;

        if (email.length() < minLength || email.length() > maxLength) {
            throw new IllegalArgumentException("La lunghezza dell'email deve essere tra " + minLength + " e " + maxLength + " caratteri.");
        }

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if(email.matches(emailRegex)){
            return true;
        }
        else{
            throw new IllegalArgumentException("Formato email non corretto: " + email);
        }
    }

    public static boolean isValidRole(String role){
        if(role == null || (!role.equals(Role.CLIENT.name()) && !role.equals(Role.ADMIN.name()))){
            throw new IllegalArgumentException("Valore di ruolo non puo' essere nullo o diverso da: " + Role.ADMIN.name() + " o " + Role.CLIENT.name() + ".");
        }
        return true;
    }
    
    public static boolean checkAdult(String dateString) {
        if (dateString == null || dateString.isBlank()) {
            throw new IllegalArgumentException("La data di nascita non può essere nulla o vuota.");
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);

        try {
            Date date = dateFormat.parse(dateString);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            if (isValidYear(year) && isValidMonth(month) && isValidDay(day, month, year)) {
                Calendar eighteenYearsAgo = Calendar.getInstance();
                eighteenYearsAgo.add(Calendar.YEAR, -18);

                if (calendar.before(eighteenYearsAgo)) {
                    return true;
                } else {
                    throw new IllegalArgumentException("La persona deve avere almeno 18 anni: " + dateString);
                }
            } else {
                throw new IllegalArgumentException("Data non valida: " + dateString);
            }

        } catch (ParseException e) {
            throw new IllegalArgumentException("La data non è nel formato corretto (yyyy-MM-dd): " + dateString);
        }
    }

    private static boolean isValidYear(int year) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        return year >= 1900 && year <= currentYear;
    }

    private static boolean isValidMonth(int month) {
        return month >= 0 && month <= 11;
    }

    private static boolean isValidDay(int day, int month, int year) {
        if (month >= 0 && month <= 11) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, 1);
            int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            return day >= 1 && day <= maxDay;
        }
        return false;
    }

    public static boolean isValidBase64(byte[] imageData) {
        if(imageData == null || imageData.length == 0){
            throw new IllegalArgumentException("L'immagine della prescrizione, non puo' essere nulla o vuota.");
        }

        // Verifico se l'intestazione del file corrisponde a un file PNG
        byte[] pngHeader = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
        if (startsWith(imageData, pngHeader)) {
            return true;
        }

        // Verifico se l'intestazione del file corrisponde a un file JPEG
        byte[] jpegHeader = {(byte) 0xFF, (byte) 0xD8};
        if (startsWith(imageData, jpegHeader)) {
            return true;
        }

        // Verifico se l'intestazione del file corrisponde a un file JPG
        byte[] jpgHeader = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        if (startsWith(imageData, jpgHeader)) {
            return true;
        }
        throw new IllegalArgumentException("Immagine non valida.");
    }

    private static boolean startsWith(byte[] array, byte[] prefix) {
        if (array.length < prefix.length) {
            return false;
        }
        for (int i = 0; i < prefix.length; i++) {
            if (array[i] != prefix[i]) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkDateEarly(String date) {
        if(date == null || date.isBlank()){
            throw new IllegalArgumentException("La data e l'orario inseriti, non puo' essere nulli.");
        }
        date = date.replace("T", " ");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        dateFormat.setLenient(false);
        try {
            Date dateChosen =  dateFormat.parse(date);
            Date currentDateTime = new Date();

            if (dateChosen.before(currentDateTime)) {
                throw new IllegalArgumentException("La data e/o l'orario inseriti non sono corretti.\n Ricorda che la data e l'ora non possono essere antecedenti o uguali alla data e all'orario attuale.");
            }

        } catch (ParseException  e) {
            throw new IllegalArgumentException("La data non e' nel giusto formato: " + date.toString());
        }
        return true;
    }

    public static boolean checkTaxId(String taxId) {
        if(taxId == null || taxId.isBlank()){
            throw new IllegalArgumentException("Il codice fiscale inserito non puo' essere nullo o vuoto.");
        }

        if (taxId.length() != 16 || taxId.contains(" ")) {
            throw new IllegalArgumentException("Il codice fiscale inserito non e' corretto.\nRicorda, massimo 16 caratteri alfanumerici e niente spazi.");
        } 
        
        for (char c : taxId.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                throw new IllegalArgumentException("Il codice fiscale contiene caratteri non validi.");
            }
            if ("IOQ".indexOf(Character.toUpperCase(c)) != -1) {
                throw new IllegalArgumentException("Il codice fiscale non puo' contenere le lettere I, O, Q.");
            }
        }

        return true;
    }

    public static boolean validateString(String text, String object) {
        if(text == null || text.isBlank()){
            throw new IllegalArgumentException("Il valore di " + object + " inserito non puo' essere nullo o vuoto.");
        }
        if (text.contains(" ")) {
            throw new IllegalArgumentException("Il valore inserito di " + object + " non puo' contenere spazi.");
        }
        return true;
    }
}
