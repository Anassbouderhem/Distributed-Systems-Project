import {Component, inject} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {AsyncPipe} from '@angular/common';
import {MarkdownComponent} from 'ngx-markdown';
import {LoadingService} from '../services/loading';

@Component({
  selector: 'app-bot-ui',
  imports: [
    FormsModule,
    AsyncPipe,
    MarkdownComponent
  ],
  templateUrl: './bot-ui.html',
  styleUrl: './bot-ui.css',
})
export class BotUi {
  query : any;
  http = inject(HttpClient);
  response$! : Observable<any>
  public loadingService = inject(LoadingService);
 askAgent() {
   this.response$ = this.http
     .get("http://localhost:9999/EBANK-BOT/chat?query="+this.query,
       {responseType:'text'})
  }
}
