import { TestBed } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { provideRouter, Router } from '@angular/router';

describe('AppComponent', () => {
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    const rSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [AppComponent],
      providers: [
        { provide: Router, useValue: rSpy }
      ]
    }).compileComponents();

    routerSpy = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it(`should have the 'food-frontend' title`, () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app.title).toEqual('food-frontend');
  });

  it('should check if logged in', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    
    spyOn(localStorage, 'getItem').and.returnValue('token');
    expect(app.isLoggedIn()).toBeTrue();
    
    // @ts-ignore
    localStorage.getItem.and.returnValue(null);
    expect(app.isLoggedIn()).toBeFalse();
  });

  it('should logout and navigate to home', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    
    spyOn(localStorage, 'clear');
    
    app.logout();
    
    expect(localStorage.clear).toHaveBeenCalled();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/']);
  });
});
