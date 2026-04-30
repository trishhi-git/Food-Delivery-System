import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RegisterComponent } from './register.component';
import { ApiService } from '../services/api.service';
import { Router, provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let apiSpy: jasmine.SpyObj<ApiService>;
  let routerSpy: Router;

  beforeEach(async () => {
    const apiServiceSpy = jasmine.createSpyObj('ApiService', ['register', 'login', 'addRestaurant']);

    await TestBed.configureTestingModule({
      imports: [RegisterComponent],
      providers: [
        { provide: ApiService, useValue: apiServiceSpy },
        provideRouter([])
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    apiSpy = TestBed.inject(ApiService) as jasmine.SpyObj<ApiService>;
    routerSpy = TestBed.inject(Router);
    spyOn(routerSpy, 'navigate');
    fixture.detectChanges();
    spyOn(window, 'alert');
    spyOn(localStorage, 'setItem');
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should require all fields', () => {
    component.user = { name: '', email: '', password: '', role: 'USER' };
    component.register();
    expect(component.message).toBe('All fields are required');
    expect(apiSpy.register).not.toHaveBeenCalled();
  });

  it('should require restaurant fields for OWNER', () => {
    component.user = { name: 'n', email: 'e', password: 'p', role: 'OWNER' };
    component.restaurantName = '';
    component.register();
    expect(component.message).toBe('Restaurant name and location are required for owners');
    expect(apiSpy.register).not.toHaveBeenCalled();
  });

  it('should register USER and navigate', () => {
    component.user = { name: 'n', email: 'e', password: 'p', role: 'USER' };
    apiSpy.register.and.returnValue(of({}));

    component.register();

    expect(window.alert).toHaveBeenCalledWith('Registered successfully!');
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/']);
  });

  it('should show message on register failure', () => {
    component.user = { name: 'n', email: 'e', password: 'p', role: 'USER' };
    apiSpy.register.and.returnValue(throwError(() => ({ error: 'Error' })));

    component.register();

    expect(component.message).toBe('Error');
  });

  it('should register OWNER, auto-login, add restaurant and navigate', () => {
    component.user = { name: 'n', email: 'e', password: 'p', role: 'OWNER' };
    component.restaurantName = 'RN';
    component.restaurantLocation = 'RL';
    
    apiSpy.register.and.returnValue(of({}));
    const token = 'header.eyJyb2xlIjoiT1dORVIiLCJ1c2VySWQiOjF9.sig';
    apiSpy.login.and.returnValue(of(token));
    apiSpy.addRestaurant.and.returnValue(of({}));

    component.register();

    expect(localStorage.setItem).toHaveBeenCalledWith('token', token);
    expect(window.alert).toHaveBeenCalledWith('Registered and Restaurant Created successfully!');
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/owner-dashboard']);
  });

  it('should handle add restaurant failure for OWNER', () => {
    component.user = { name: 'n', email: 'e', password: 'p', role: 'OWNER' };
    component.restaurantName = 'RN';
    component.restaurantLocation = 'RL';
    
    apiSpy.register.and.returnValue(of({}));
    const token = 'header.eyJyb2xlIjoiT1dORVIiLCJ1c2VySWQiOjF9.sig';
    apiSpy.login.and.returnValue(of(token));
    apiSpy.addRestaurant.and.returnValue(throwError(() => ({})));

    component.register();

    expect(component.message).toBe('Registered, but failed to create restaurant. Please contact support.');
  });

  it('should handle auto-login failure for OWNER', () => {
    component.user = { name: 'n', email: 'e', password: 'p', role: 'OWNER' };
    component.restaurantName = 'RN';
    component.restaurantLocation = 'RL';
    
    apiSpy.register.and.returnValue(of({}));
    apiSpy.login.and.returnValue(throwError(() => ({})));

    component.register();

    expect(component.message).toBe('Registered successfully, but failed to auto-login. Please login manually.');
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/']);
  });
});
