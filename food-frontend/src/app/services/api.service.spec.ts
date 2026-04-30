import { TestBed } from '@angular/core/testing';
import { ApiService } from './api.service';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

describe('ApiService', () => {
  let service: ApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ApiService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(ApiService);
    httpMock = TestBed.inject(HttpTestingController);
    spyOn(localStorage, 'getItem').and.returnValue('fake-token');
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call register', () => {
    const dummyUser = { name: 'Test' };
    service.register(dummyUser).subscribe(res => {
      expect(res).toEqual(dummyUser);
    });
    const req = httpMock.expectOne(`${service.baseUrl}/users`);
    expect(req.request.method).toBe('POST');
    req.flush(dummyUser);
  });

  it('should call login', () => {
    service.login({ username: 'test', password: 'password' }).subscribe(res => {
      expect(res).toBe('token');
    });
    const req = httpMock.expectOne(`${service.baseUrl}/auth/login`);
    expect(req.request.method).toBe('POST');
    req.flush('token');
  });

  it('should get restaurants', () => {
    service.getRestaurants().subscribe(res => {
      expect(res.length).toBe(1);
    });
    const req = httpMock.expectOne(`${service.baseUrl}/restaurants`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe('Bearer fake-token');
    req.flush([{ id: 1 }]);
  });

  it('should add restaurant', () => {
    service.addRestaurant({ name: 'Rest' }).subscribe(res => {
      expect(res).toBeTruthy();
    });
    const req = httpMock.expectOne(`${service.baseUrl}/restaurants`);
    expect(req.request.method).toBe('POST');
    req.flush({ id: 1 });
  });

  it('should add menu item', () => {
    service.addMenuItem({ name: 'Item' }).subscribe(res => {
      expect(res).toBeTruthy();
    });
    const req = httpMock.expectOne(`${service.baseUrl}/menu`);
    expect(req.request.method).toBe('POST');
    req.flush({ id: 1 });
  });

  it('should get menu by restaurant', () => {
    service.getMenuByRestaurant(1).subscribe(res => {
      expect(res.length).toBe(1);
    });
    const req = httpMock.expectOne(`${service.baseUrl}/menu/1`);
    expect(req.request.method).toBe('GET');
    req.flush([{ id: 1 }]);
  });

  it('should place order', () => {
    service.placeOrder({ items: [] }).subscribe(res => {
      expect(res).toBeTruthy();
    });
    const req = httpMock.expectOne(`${service.baseUrl}/orders`);
    expect(req.request.method).toBe('POST');
    req.flush({ id: 1 });
  });

  it('should get orders', () => {
    service.getOrders().subscribe(res => {
      expect(res.length).toBe(1);
    });
    const req = httpMock.expectOne(`${service.baseUrl}/orders`);
    expect(req.request.method).toBe('GET');
    req.flush([{ id: 1 }]);
  });

  it('should get orders by restaurant', () => {
    service.getOrdersByRestaurant(1).subscribe((res: any) => {
      expect(res).toBeTruthy();
    });
    const req = httpMock.expectOne(`${service.baseUrl}/orders?restaurantId=1`);
    expect(req.request.method).toBe('GET');
    req.flush([{ id: 1 }]);
  });

  it('should update order', () => {
    service.updateOrder(1, { status: 'DELIVERED' }).subscribe(res => {
      expect(res).toBeTruthy();
    });
    const req = httpMock.expectOne(`${service.baseUrl}/orders/1`);
    expect(req.request.method).toBe('PUT');
    req.flush({ id: 1 });
  });
});
