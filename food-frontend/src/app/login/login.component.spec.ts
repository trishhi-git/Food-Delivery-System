import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { ApiService } from '../services/api.service';
import { Router, provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let apiSpy: jasmine.SpyObj<ApiService>;
  let routerSpy: Router;

  beforeEach(async () => {
    const apiServiceSpy = jasmine.createSpyObj('ApiService', ['login']);

    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        { provide: ApiService, useValue: apiServiceSpy },
        provideRouter([])
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    apiSpy = TestBed.inject(ApiService) as jasmine.SpyObj<ApiService>;
    routerSpy = TestBed.inject(Router);
    spyOn(routerSpy, 'navigate');
    fixture.detectChanges();
    spyOn(localStorage, 'setItem');
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not call login without email and password', () => {
    component.email = '';
    component.password = '';
    component.login();
    expect(component.message).toBe('Please enter email and password');
    expect(apiSpy.login).not.toHaveBeenCalled();
  });

  it('should login and navigate to user-dashboard for USER', () => {
    component.email = 'test@test.com';
    component.password = 'password';
    // token for { role: 'USER', userId: 1 }
    const token = 'header.eyJyb2xlIjoiVVNFUiIsInVzZXJJZCI6MX0.sig';
    apiSpy.login.and.returnValue(of(token));

    component.login();

    expect(localStorage.setItem).toHaveBeenCalledWith('token', token);
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/user-dashboard']);
  });

  it('should login and navigate to owner-dashboard for OWNER', () => {
    component.email = 'test@test.com';
    component.password = 'password';
    // token for { role: 'OWNER', userId: 1 }
    const token = 'header.eyJyb2xlIjoiT1dORVIiLCJ1c2VySWQiOjF9.sig';
    apiSpy.login.and.returnValue(of(token));

    component.login();

    expect(localStorage.setItem).toHaveBeenCalledWith('token', token);
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/owner-dashboard']);
  });

  it('should handle invalid token received', () => {
    component.email = 'test@test.com';
    component.password = 'password';
    const invalidToken = 'invalid.token';
    apiSpy.login.and.returnValue(of(invalidToken));

    component.login();

    expect(component.message).toBe('Invalid token received');
  });

  it('should handle login error 401', () => {
    component.email = 'test@test.com';
    component.password = 'password';
    apiSpy.login.and.returnValue(throwError(() => ({ status: 401 })));

    component.login();

    expect(component.message).toBe('Invalid email or password');
  });

  it('should handle login error with message', () => {
    component.email = 'test@test.com';
    component.password = 'password';
    apiSpy.login.and.returnValue(throwError(() => ({ error: 'Some error' })));

    component.login();

    expect(component.message).toBe('Some error');
  });

  it('should handle generic login error', () => {
    component.email = 'test@test.com';
    component.password = 'password';
    apiSpy.login.and.returnValue(throwError(() => ({})));

    component.login();

    expect(component.message).toBe('Login failed. Please try again');
  });
});
