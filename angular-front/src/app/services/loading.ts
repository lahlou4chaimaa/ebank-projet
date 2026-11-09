import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class LoadingService {

  private loading = signal<boolean>(false);

  public isLoading = this.loading.asReadonly();

  public setLoading(value: boolean): void {
    this.loading.set(value);
  }
}
