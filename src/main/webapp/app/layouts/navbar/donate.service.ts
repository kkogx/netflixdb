import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';

import { SERVER_API_URL } from 'app/app.constants';
import { map } from 'rxjs/operators';
import { Przelewy24 } from 'app/layouts/navbar/donate.model';
import { Observable } from 'rxjs/index';

@Injectable({ providedIn: 'root' })
export class DonateService {
    private infoUrl = SERVER_API_URL + 'api/donate';
    private przelewy24Promise: Promise<Przelewy24>;
    private przelewy24: Przelewy24;

    constructor(private http: HttpClient) {}

    getPrzelewy24(): Promise<Przelewy24> {
        if (!this.przelewy24Promise) {
            this.przelewy24Promise = this.http
                .get<Przelewy24>(this.infoUrl + '/p24', { observe: 'response' })
                .pipe(
                    map((res: HttpResponse<Przelewy24>) => {
                        const data = res.body;
                        const pi = new Przelewy24();
                        pi.crc = data['crc'];
                        pi.host = data['host'];
                        pi.merchantId = data['merchantId'];
                        this.przelewy24 = pi;
                        return pi;
                    })
                )
                .toPromise();
        }
        return this.przelewy24Promise;
    }

    txnRegisterP24(req: any): Observable<HttpResponse<any>> {
        let url = this.przelewy24.host;
        if (!url.endsWith('/')) {
            url += '/';
        }
        url += 'trnRegister';
        return this.http.post<any>(url, req);
    }
}
