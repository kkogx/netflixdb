import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';

import { SERVER_API_URL } from 'app/app.constants';
import { Przelewy24Trx } from 'app/layouts/navbar/donate.model';
import { Observable } from 'rxjs/index';

@Injectable({ providedIn: 'root' })
export class DonateService {
    private donateUrl = SERVER_API_URL + 'api/donate';

    constructor(private http: HttpClient) {}

    txnRegisterP24(req: Przelewy24Trx): Observable<HttpResponse<any>> {
        return this.http.post<any>(this.donateUrl + '/p24/txnRegister', req);
    }
}
