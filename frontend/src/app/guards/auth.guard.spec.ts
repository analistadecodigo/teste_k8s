import { TestBed } from '@angular/core/testing';
import { authGuard } from './auth.guard';
import { Router } from '@angular/router';
import { PLATFORM_ID } from '@angular/core';

describe('authGuard', () => {
  let routerMock: { navigate: jest.Mock };

  beforeEach(() => {
    routerMock = { navigate: jest.fn() };

    TestBed.configureTestingModule({
      providers: [
        { provide: Router, useValue: routerMock },
        { provide: PLATFORM_ID, useValue: 'browser' }, 
      ],
    });
  });

  function runGuard(url: string, platformId: any, cookie: string) {
    Object.defineProperty(document, 'cookie', {
      writable: true,
      value: cookie,
    });

    TestBed.overrideProvider(PLATFORM_ID, { useValue: platformId });

    return TestBed.runInInjectionContext(() => {
      return authGuard(null as any, { url } as any);
    });
  }

  it('should allow navigation for public routes', () => {
    const result = runGuard('/login', 'browser', '');
    expect(result).toBe(true);
    expect(routerMock.navigate).not.toHaveBeenCalled();
  });

  it('should allow navigation if JWT cookie is present on browser platform', () => {
    const result = runGuard('/dashboard', 'browser', 'jwt=fake-token; other=value');
    expect(result).toBe(true);
    expect(routerMock.navigate).not.toHaveBeenCalled();
  });

  it('should deny navigation and redirect to login if no JWT cookie on browser platform', () => {
    const result = runGuard('/dashboard', 'browser', 'some=other; value=123');
    expect(result).toBe(false);
    expect(routerMock.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should deny navigation and redirect to login on non-browser platform', () => {
    const result = runGuard('/dashboard', 'server', '');
    expect(result).toBe(false);
    expect(routerMock.navigate).toHaveBeenCalledWith(['/login']);
  });
});
