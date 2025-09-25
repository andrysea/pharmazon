import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-error',
  templateUrl: './error.component.html',
  styleUrls: ['./error.component.css']
})
export class ErrorComponent implements OnInit{

  protected statusCode: number = 0;
  protected message = "";
  protected errorMessage: string = "";

  private errorMessages: { [key: string]: string }  = {
    0: 'Si è verificato un errore sconosciuto.',
    400: 'Richiesta non valida.',
    403: 'Permssi insufficienti.',
    404: 'La pagina che stai cercando non esiste.',
    500: 'Errore interno del server.',
  };

  constructor(private route: Router) {
    const navigation = this.route.getCurrentNavigation();
    if (navigation?.extras.state) {
      this.statusCode = navigation.extras.state['status'];
      this.errorMessage = this.errorMessages[this.statusCode] || 'Errore sconosciuto';
      this.message = "CODICE ERRORE: " + this.statusCode +"\n MESSAGGIO ERRORE: " + this.errorMessage +
      "\nA BREVE VERRAI INDIRIZZATO ALLA PAGINA DI LOGIN.";
    }
    else{
      this.statusCode = 404;
      this.errorMessage = this.errorMessages[this.statusCode];

      this.message = "CODICE ERRORE: " + this.statusCode +"\n MESSAGGIO ERRORE: " + this.errorMessage +
      "\nA BREVE VERRAI INDIRIZZATO ALLA PAGINA DI LOGIN.";
    }
  }

  ngOnInit(): void {
    setTimeout(() => {
      this.route.navigate(['/logout'])
    }, 4000)
  }
}