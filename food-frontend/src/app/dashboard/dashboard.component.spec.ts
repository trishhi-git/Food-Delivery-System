import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DashboardComponent } from './dashboard.component';
import { ApiService } from '../services/api.service';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let apiSpy: jasmine.SpyObj<ApiService>;

  beforeEach(async () => {
    const apiServiceSpy = jasmine.createSpyObj('ApiService', ['getRestaurants', 'placeOrder']);

    await TestBed.configureTestingModule({
      imports: [DashboardComponent],
      providers: [
        { provide: ApiService, useValue: apiServiceSpy },
        provideRouter([])
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    apiSpy = TestBed.inject(ApiService) as jasmine.SpyObj<ApiService>;
  });

  it('should create', () => {
    apiSpy.getRestaurants.and.returnValue(of([]));
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should load restaurants successfully', () => {
    const mockRestaurants = [{ id: 1, name: 'Rest 1' }];
    apiSpy.getRestaurants.and.returnValue(of(mockRestaurants));
    fixture.detectChanges();
    expect(component.restaurants).toEqual(mockRestaurants);
  });

  it('should handle load restaurants error', () => {
    apiSpy.getRestaurants.and.returnValue(throwError(() => new Error('Error')));
    fixture.detectChanges();
    expect(component.message).toBe('Failed to load restaurants');
  });

  it('should fail place order if invalid restaurant', () => {
    apiSpy.getRestaurants.and.returnValue(of([]));
    fixture.detectChanges();
    component.placeOrder(null);
    expect(component.message).toBe('Invalid restaurant selected');

    component.placeOrder({});
    expect(component.message).toBe('Invalid restaurant selected');
  });

  it('should place order successfully', () => {
    apiSpy.getRestaurants.and.returnValue(of([]));
    fixture.detectChanges();
    spyOn(localStorage, 'getItem').and.returnValue('header.eyJ1c2VySWQiOjEsInJvbGUiOiJVU0VSIn0=.sig');
    apiSpy.placeOrder.and.returnValue(of({}));

    component.placeOrder({ id: 1 });

    expect(component.message).toBe('Order placed successfully!');
  });

  it('should handle place order error', () => {
    apiSpy.getRestaurants.and.returnValue(of([]));
    fixture.detectChanges();
    spyOn(localStorage, 'getItem').and.returnValue('header.eyJ1c2VySWQiOjEsInJvbGUiOiJVU0VSIn0=.sig');
    apiSpy.placeOrder.and.returnValue(throwError(() => new Error('Error')));

    component.placeOrder({ id: 1 });

    expect(component.message).toBe('Order failed!');
  });

  it('should get userId from token or return 0 if no token', () => {
    apiSpy.getRestaurants.and.returnValue(of([]));
    fixture.detectChanges();
    spyOn(localStorage, 'getItem').and.returnValue(null);
    expect(component.getUserIdFromToken()).toBe(0);
  });
});
