import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { WelcomeComponent } from './components/welcome/welcome.component';
import { ErrorComponent } from './components/error/error.component';
import { GridProductsComponent } from './components/gridProducts/gridProducts.component';
import { PasswordForgetComponent } from './components/password-forget/password-forget.component';
import { ProductAdderComponent } from './components/product-adder/product-adder.component';
import { ProductModifierHubComponent } from './components/product-modifier-hub/product-modifier-hub.component';
import { ServiceComponent } from './components/service/service.component';
import { ServiceAdderComponent } from './components/service-adder/service-adder.component';
import { ServiceModifierComponent } from './components/service-modifier/service-modifier.component';
import { CalendarComponent } from './components/calendar/calendar.component';
import { CartComponent } from './components/cart/cart.component';
import { MatCardModule } from '@angular/material/card';
import { OrderComponent } from './components/order/order.component';
import { ChatComponent } from './components/chat/chat.component';
import { OrderUserComponent } from './components/order-user/order-user.component';
import { FeedbackComponent } from './components/feedback/feedback.component';
import { FeedbackAdminComponent } from './components/feedback-admin/feedback-admin.component';
import { PersonalComponent } from './components/personal/personal.component';
import { RegistrationComponent } from './components/registration/registration.component';
import { ProductShowComponent } from './components/product-show/product-show.component';
import { AddressAdderComponent } from './components/address-adder/address-adder.component';
import { AddressModifierComponent } from './components/address-modifier/address-modifier.component';
import { CreditcardAdderComponent } from './components/creditcard-adder/creditcard-adder.component';
import { CreditcardModifierComponent } from './components/creditcard-modifier/creditcard-modifier.component';
import { UserModifierComponent } from './components/user-modifier/user-modifier.component';
import { ServiceShowComponent } from './components/service-show/service-show.component';
import { CategoryComponent } from './components/category/category.component';
import { LogoutComponent } from './components/logout/logout.component';
import { AdminGuardService } from './admin-guard.service';
import { ClientGuardService } from './client-guard.service';
import { ChangePasswordComponent } from './components/change-password/change-password.component';

const routes: Routes = [
  {path: '', component: ErrorComponent},
  {path: 'welcome', component: WelcomeComponent},
  {path: 'login', component: LoginComponent},
  {path: 'chat', component: ChatComponent},
  {path: 'calendar', component: CalendarComponent, canActivate: [AdminGuardService]},
  {path: 'catalog', component: GridProductsComponent},
  {path: 'category', component: CategoryComponent, canActivate: [AdminGuardService]},
  {path: 'cart', component: CartComponent, canActivate: [ClientGuardService]},
  {path: 'feedback', component: FeedbackComponent, canActivate: [ClientGuardService]},
  {path: 'feedbackAdmin', component: FeedbackAdminComponent, canActivate: [AdminGuardService]},
  {path: 'order', component: OrderComponent, canActivate: [AdminGuardService]},
  {path: 'orderUser', component: OrderUserComponent, canActivate: [ClientGuardService]},
  {path: 'addProduct', component: ProductAdderComponent, canActivate: [AdminGuardService]},
  {path: 'showProduct', component: ProductShowComponent},
  {path: 'showService', component: ServiceShowComponent},
  {path: 'personal', component: PersonalComponent},
  {path: 'changePassword', component: ChangePasswordComponent},
  {path: 'modifyUser', component: UserModifierComponent},
  {path: 'addAddress', component: AddressAdderComponent, canActivate: [ClientGuardService]},
  {path: 'modifyAddress', component: AddressModifierComponent, canActivate: [ClientGuardService]},
  {path: 'addCreditCard', component: CreditcardAdderComponent, canActivate: [ClientGuardService]},
  {path: 'modifyCreditCard', component: CreditcardModifierComponent, canActivate: [ClientGuardService]},
  {path: 'registration', component: RegistrationComponent},
  {path: 'modifyProduct', component: ProductModifierHubComponent, canActivate: [AdminGuardService]},
  {path: 'addService', component: ServiceAdderComponent, canActivate: [AdminGuardService]},
  {path: 'modifyService', component: ServiceModifierComponent, canActivate: [AdminGuardService]},
  {path: 'service', component: ServiceComponent},
  {path: 'logout', component: LogoutComponent},
  {path: 'forgetPassword', component: PasswordForgetComponent},
  {path: 'error', component: ErrorComponent},
  {path: '**', component: ErrorComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes),
    MatCardModule],
  exports: [RouterModule]
})
export class AppRoutingModule {}

