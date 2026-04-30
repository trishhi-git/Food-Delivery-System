import { ComponentFixture, TestBed } from '@angular/core/testing';
import { OwnerDashboardComponent } from './owner-dashboard.component';
import { ApiService } from '../../services/api.service';
import { of } from 'rxjs';

describe('OwnerDashboardComponent', () => {
  let component: OwnerDashboardComponent;
  let fixture: ComponentFixture<OwnerDashboardComponent>;
  let apiSpy: jasmine.SpyObj<ApiService>;

  beforeEach(async () => {
    const apiServiceSpy = jasmine.createSpyObj('ApiService', [
      'getRestaurants', 'addRestaurant', 'getOrdersByRestaurant', 'getMenuByRestaurant', 'addMenuItem', 'updateOrder'
    ]);

    await TestBed.configureTestingModule({
      imports: [OwnerDashboardComponent],
      providers: [
        { provide: ApiService, useValue: apiServiceSpy }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OwnerDashboardComponent);
    component = fixture.componentInstance;
    apiSpy = TestBed.inject(ApiService) as jasmine.SpyObj<ApiService>;
    spyOn(window, 'alert');
  });

  it('should create', () => {
    apiSpy.getRestaurants.and.returnValue(of([]));
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should init and filter restaurants', () => {
    spyOn(localStorage, 'getItem').and.returnValue('1');
    const mockRestaurants = [{ ownerId: 1, id: 10 }, { ownerId: 2, id: 20 }];
    apiSpy.getRestaurants.and.returnValue(of(mockRestaurants));
    apiSpy.getOrdersByRestaurant.and.returnValue(of([]));
    apiSpy.getMenuByRestaurant.and.returnValue(of([]));
    
    fixture.detectChanges();

    expect(component.restaurants.length).toBe(1);
    expect(component.restaurants[0].id).toBe(10);
    expect(apiSpy.getOrdersByRestaurant).toHaveBeenCalledWith(10);
  });

  it('should add restaurant', () => {
    apiSpy.getRestaurants.and.returnValue(of([]));
    fixture.detectChanges();
    apiSpy.addRestaurant.and.returnValue(of({}));
    
    component.restaurant = { name: 'Name', location: 'Loc' };
    component.addRestaurant();

    expect(window.alert).toHaveBeenCalledWith('Restaurant added');
  });

  it('should add menu item', () => {
    apiSpy.getRestaurants.and.returnValue(of([]));
    fixture.detectChanges();
    apiSpy.addMenuItem.and.returnValue(of({}));
    apiSpy.getMenuByRestaurant.and.returnValue(of([]));

    component.selectedRestaurantId = 1;
    component.newMenuItem = { name: 'Item', price: 10 as any };
    component.addMenuItem();

    expect(window.alert).toHaveBeenCalledWith('Menu Item added');
    expect(apiSpy.getMenuByRestaurant).toHaveBeenCalledWith(1);
  });

  it('should not add menu item if missing fields', () => {
    apiSpy.getRestaurants.and.returnValue(of([]));
    fixture.detectChanges();

    component.selectedRestaurantId = null;
    component.addMenuItem();
    expect(apiSpy.addMenuItem).not.toHaveBeenCalled();
  });

  it('should update status', () => {
    apiSpy.getRestaurants.and.returnValue(of([]));
    fixture.detectChanges();
    apiSpy.updateOrder.and.returnValue(of({}));
    apiSpy.getOrdersByRestaurant.and.returnValue(of([]));
    apiSpy.getMenuByRestaurant.and.returnValue(of([]));

    component.selectedRestaurantId = 1;
    component.updateStatus(100, 'DELIVERED');

    expect(apiSpy.updateOrder).toHaveBeenCalledWith(100, { status: 'DELIVERED' });
    expect(apiSpy.getOrdersByRestaurant).toHaveBeenCalledWith(1);
  });
});
