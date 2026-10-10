import {Component, inject} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {HttpClient, HttpDownloadProgressEvent, HttpEventType} from '@angular/common/http';
import {map, Observable} from 'rxjs';
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

  askAgentStream() {
    this.response$ = this.http
      .get("http://localhost:9999/EBANK-BOT/chatStream?query="+this.query,
        {responseType:'text', observe:"events", reportProgress : true})
          .pipe(map(event=>{
            switch (event.type){
              case HttpEventType.Sent:
                return {'type':'Sent'}
              case HttpEventType.DownloadProgress:
                return {type: "Response", content : (event as HttpDownloadProgressEvent).partialText}
              case HttpEventType.Response:
                return {type: "Response", content : event.body}
              default :
                return {type : 'Other', data : event}
            }
          }))
  }
}
