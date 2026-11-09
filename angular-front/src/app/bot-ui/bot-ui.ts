
import { Component, inject } from '@angular/core';
import { LoadingService } from '../services/loading';
import { FormsModule } from '@angular/forms';
import {
  HttpClient,
  HttpDownloadProgressEvent,
  HttpEvent,
  HttpEventType
} from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { MarkdownComponent } from 'ngx-markdown';
import { AsyncPipe } from '@angular/common';

@Component({
  standalone: true,
  imports: [
    AsyncPipe,
    FormsModule,
    MarkdownComponent
  ],
  selector: 'app-bot-ui',
  styleUrl: './bot-ui.css',
  templateUrl: './bot-ui.html',
})
export class BotUi {

  public loadingService = inject(LoadingService);

  query: string = '';

  http = inject(HttpClient);

  response$!: Observable<any>;

  askAgent(): void {
    const url =
      'http://localhost:5555/EBANK-BOT/chat?query='
      + encodeURIComponent(this.query);

    this.response$ = this.http.get(url, {
      responseType: 'text'
    });
  }

  askAgentStream(): void {
    const url =
      'http://localhost:5555/EBANK-BOT/chatStream?query='
      + encodeURIComponent(this.query);

    this.response$ = this.http.get(url, {
      responseType: 'text',
      observe: 'events',
      reportProgress: true
    }).pipe(
      map((event: HttpEvent<string>) => {

        switch (event.type) {

          case HttpEventType.Sent:
            return {
              type: 'Sent'
            };

          case HttpEventType.DownloadProgress:
            return {
              type: 'Response',
              content: (event as HttpDownloadProgressEvent).partialText ?? ''
            };

          case HttpEventType.Response:
            return {
              type: 'Response',
              content: event.body ?? ''
            };

          default:
            return {
              type: 'Other',
              data: event
            };
        }
      })
    );
  }
}
