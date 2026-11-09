import {HttpClient} from '@angular/common/http';
import { Component, inject } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import {catchError, map, Observable, of} from 'rxjs';
import {Account, AccountListState, RequestStatus} from '../model/account.model';
import {LoadingService} from '../services/loading';

@Component({
  standalone: true,
  imports: [AsyncPipe],
  selector: 'app-accounts',
  styleUrl: './accounts.css',
  templateUrl: './accounts.html',
})
export class Accounts {
  private http = inject(HttpClient);
  public loadingService = inject(LoadingService);
  accounts$ : Observable<AccountListState>  = this.http.get<Account[]>
  ("http://localhost:5555/EBANK-SERVICE/accounts")
    .pipe(
      map(response => {

        return {accounts: response, status: RequestStatus.SUCCESS}
      }),
      catchError((err, caught) => {
        return of({status: RequestStatus.ERROR, errorMessage: err.statusText});
      })
    );
  protected readonly RequestStatus = RequestStatus;
}
