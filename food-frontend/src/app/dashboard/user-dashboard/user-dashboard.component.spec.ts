import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UserDashboardComponent } from './user-dashboard.component';
import { ApiService } from '../../services/api.service';
import { of } from 'rxjs';

describe('UserDashboardComponent', () => {
  let component: UserDashboardComponent;
  let fixture: ComponentFixture<UserDashboardComponent>;
  let apiSpy: jasmine.SpyObj<ApiService>;

  beforeEach(async () => {
    const apiServiceSpy = jasmine.createSpyObj('ApiService', [
      'getRestaurants', 'getOrders', 'getMenuByRestaurant', 'placeOrder'
    ]);

    await TestBed.configureTestingModule({
      imports: [UserDashboardComponent],
      providers: [
        { provide: ApiService, useValue: apiServiceSpy }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserDashboardComponent);
    component = fixture.componentInstance;
    apiSpy = TestBed.inject(ApiService) as jasmine.SpyObj<ApiService>;
    spyOn(window, 'alert');
  });

  it('should create', () => {
    apiSpy.getRestaurants.and.returnValue(of([]));
    apiSpy.getOrders.and.returnValue(of([]));
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should load restaurants and user orders on init', () => {
    spyOn(localStorage, 'getItem').and.returnValue('1');
    apiSpy.getRestaurants.and.returnValue(of([{ id: 1 }]));
    apiSpy.getOrders.and.returnValue(of([{ id: 10, userId: 1 }, { id: 20, userId: 2 }]));

    fixture.detectChanges();

    expect(component.restaurants.length).toBe(1);
    expect(component.orders.length).toBe(1);
    expect(component.orders[0].id).toBe(10);
  });

  it('should select restaurant and load menu items', () => {
    apiSpy.getRestaurants.and.returnValue(of([]));
    apiSpy.getOrders.and.returnValue(of([]));
    fixture.detectChanges();

    apiSpy.getMenuByRestaurant.and.returnValue(of([{ id: 1, name: 'Item 1' }]));
    
    component.selectRestaurant({ id: 1 });
    
    expect(component.selectedRestaurant.id).toBe(1);
    expect(apiSpy.getMenuByRestaurant).toHaveBeenCalledWith(1);
    expect(component.menuItems.length).toBe(1);
    expect(component.menuItems[0].quantity).toBe(0);
  });

  it('should close menu and reset state', () => {
    apiSpy.getRestaurants.and.returnValue(of([]));
    apiSpy.getOrders.and.returnValue(of([]));
    fixture.detectChanges();

    component.selectedRestaurant = { id: 1 };
    component.menuItems = [{}];
    component.selectedItems.set(1, {});
    component.cartTotal = 10;

    component.closeMenu();

    expect(component.selectedRestaurant).toBeNull();
    expect(component.menuItems.length).toBe(0);
    expect(component.selectedItems.size).toBe(0);
    expect(component.cartTotal).toBe(0);
  });

  it('should handle quantity changes and total', () => {
    apiSpy.getRestaurants.and.returnValue(of([]));
    apiSpy.getOrders.and.returnValue(of([]));
    fixture.detectChanges();

    const item = { id: 1, name: 'Burger', price: 10, quantity: 0 };
    
    component.increaseQuantity(item);
    expect(item.quantity).toBe(1);
    expect(component.cartTotal).toBe(10);
    expect(component.selectedItems.has(1)).toBeTrue();

    component.decreaseQuantity(item);
    expect(item.quantity).toBe(0);
    expect(component.cartTotal).toBe(0);
    expect(component.selectedItems.has(1)).toBeFalse();
  });

  it('should not decrease quantity below 0', () => {
    apiSpy.getRestaurants.and.returnValue(of([]));
    apiSpy.getOrders.and.returnValue(of([]));
    fixture.detectChanges();

    const item = { id: 1, name: 'Burger', price: 10, quantity: 0 };
    component.decreaseQuantity(item);
    expect(item.quantity).toBe(0);
  });

  it('should maintain item in map if quantity > 0 after decrease', () => {
    apiSpy.getRestaurants.and.returnValue(of([]));
    apiSpy.getOrders.and.returnValue(of([]));
    fixture.detectChanges();

    const item = { id: 1, name: 'Burger', price: 10, quantity: 2 };
    component.decreaseQuantity(item);
    expect(item.quantity).toBe(1);
    expect(component.selectedItems.has(1)).toBeTrue();
  });

  it('should not place order if cart is empty or no restaurant selected', () => {
    apiSpy.getRestaurants.and.returnValue(of([]));
    apiSpy.getOrders.and.returnValue(of([]));
    fixture.detectChanges();

    component.cartTotal = 0;
    component.placeOrder();
    expect(apiSpy.placeOrder).not.toHaveBeenCalled();

    component.cartTotal = 10;
    component.selectedRestaurant = null;
    component.placeOrder();
    expect(apiSpy.placeOrder).not.toHaveBeenCalled();
  });

  it('should place order successfully and reload orders', () => {
    apiSpy.getRestaurants.and.returnValue(of([]));
    apiSpy.getOrders.and.returnValue(of([]));
    fixture.detectChanges();

    spyOn(localStorage, 'getItem').and.returnValue('1');
    component.selectedRestaurant = { id: 1 };
    component.cartTotal = 20;
    component.selectedItems.set(1, { id: 1, name: 'Burger', quantity: 2, price: 10 });

    apiSpy.placeOrder.and.returnValue(of({}));
    
    component.placeOrder();

    expect(apiSpy.placeOrder).toHaveBeenCalledWith({
      userId: '1',
      restaurantId: 1,
      totalAmount: 20,
      items: '2x Burger'
    });
    expect(window.alert).toHaveBeenCalledWith('Order placed successfully!');
    expect(apiSpy.getOrders).toHaveBeenCalled(); // via loadMyOrders
  });
});
