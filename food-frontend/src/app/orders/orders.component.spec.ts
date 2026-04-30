import { ComponentFixture, TestBed } from '@angular/core/testing';
import { OrdersComponent } from './orders.component';
import { ApiService } from '../services/api.service';
import { of, throwError } from 'rxjs';

describe('OrdersComponent', () => {
  let component: OrdersComponent;
  let fixture: ComponentFixture<OrdersComponent>;
  let apiSpy: jasmine.SpyObj<ApiService>;

  beforeEach(async () => {
    const apiServiceSpy = jasmine.createSpyObj('ApiService', ['getOrders']);

    await TestBed.configureTestingModule({
      imports: [OrdersComponent],
      providers: [
        { provide: ApiService, useValue: apiServiceSpy }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OrdersComponent);
    component = fixture.componentInstance;
    apiSpy = TestBed.inject(ApiService) as jasmine.SpyObj<ApiService>;
  });

  it('should create', () => {
    apiSpy.getOrders.and.returnValue(of([]));
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should load orders successfully', () => {
    const mockOrders = [{ id: 1 }, { id: 2 }];
    apiSpy.getOrders.and.returnValue(of(mockOrders));
    
    fixture.detectChanges();
    
    expect(component.orders).toEqual(mockOrders);
  });

  it('should handle load orders error', () => {
    apiSpy.getOrders.and.returnValue(throwError(() => new Error('Error')));
    
    fixture.detectChanges();
    
    expect(component.message).toBe('Failed to load orders');
  });
});
