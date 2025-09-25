import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { WelcomeComponent } from './components/welcome/welcome.component';
import { LoginComponent } from './components/login/login.component';
import { FormsModule } from '@angular/forms';
import { ErrorComponent } from './components/error/error.component';
import { RegistrationComponent } from './components/registration/registration.component';
import { LogoutComponent } from './components/logout/logout.component';
import { MatIconModule } from '@angular/material/icon';
import { GridProductsComponent } from './components/gridProducts/gridProducts.component';
import { ProductsCardComponent } from './components/products-card/products-card.component';
import { HttpClientModule } from '@angular/common/http';
import { PasswordForgetComponent } from './components/password-forget/password-forget.component';
import { ProductModifierHubComponent } from './components/product-modifier-hub/product-modifier-hub.component';
import { ProductAdderComponent } from './components/product-adder/product-adder.component';
import { ServiceComponent } from './components/service/service.component';
import { ServicesCardComponent } from './components/services-card/services-card.component';
import { ServiceAdderComponent } from './components/service-adder/service-adder.component';
import { ServiceModifierComponent } from './components/service-modifier/service-modifier.component';
import { CoreModule } from './core/core.module';
import { CalendarComponent } from './components/calendar/calendar.component';
import { CartComponent } from './components/cart/cart.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { OrderComponent } from './components/order/order.component';
import { ChatComponent } from './components/chat/chat.component';
import { OrderUserComponent } from './components/order-user/order-user.component';
import { FeedbackComponent } from './components/feedback/feedback.component';
import { FeedbackAdminComponent } from './components/feedback-admin/feedback-admin.component';
import { PersonalComponent } from './components/personal/personal.component';
import { ProductShowComponent } from './components/product-show/product-show.component';
import { AddressAdderComponent } from './components/address-adder/address-adder.component';
import { AddressModifierComponent } from './components/address-modifier/address-modifier.component';
import { CreditcardAdderComponent } from './components/creditcard-adder/creditcard-adder.component';
import { CreditcardModifierComponent } from './components/creditcard-modifier/creditcard-modifier.component';
import { UserModifierComponent } from './components/user-modifier/user-modifier.component';
import { ServiceShowComponent } from './components/service-show/service-show.component';
import { CategoryComponent } from './components/category/category.component';
import { ChangePasswordComponent } from './components/change-password/change-password.component';

@NgModule({
  declarations: [
    AppComponent,
    WelcomeComponent,
    LoginComponent,
    ErrorComponent,
    RegistrationComponent,
    LogoutComponent,
    GridProductsComponent,
    ProductsCardComponent,
    PasswordForgetComponent,
    ProductModifierHubComponent,
    ProductAdderComponent,
    ServiceComponent,
    ServicesCardComponent,
    ServiceAdderComponent,
    ServiceModifierComponent,
    CalendarComponent,
    ChatComponent,
    CartComponent,
    OrderComponent,
    OrderUserComponent,
    FeedbackComponent,
    FeedbackAdminComponent,
    PersonalComponent,
    ProductShowComponent,
    AddressAdderComponent,
    AddressModifierComponent,
    CreditcardAdderComponent,
    CreditcardModifierComponent,
    UserModifierComponent,
    ServiceShowComponent,
    CategoryComponent,
    ChangePasswordComponent,
  ],
  imports: [
    BrowserModule,
    FormsModule,
    AppRoutingModule,
    CoreModule,
    MatIconModule,
    HttpClientModule,
    BrowserAnimationsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
