import { TestBed } from '@angular/core/testing';
import { LoadingService } from './loading';

describe('LoadingService', () => {
  let service: LoadingService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LoadingService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should have loading set to false initially', () => {
    expect(service.isLoading()).toBe(false);
  });

  it('should update loading state to true', () => {
    service.setLoading(true);
    expect(service.isLoading()).toBe(true);
  });

  it('should update loading state to false', () => {
    service.setLoading(true);
    service.setLoading(false);
    expect(service.isLoading()).toBe(false);
  });
});
