import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IPrzelewy24Trx } from 'app/shared/model/przelewy-24-trx.model';

type EntityResponseType = HttpResponse<IPrzelewy24Trx>;
type EntityArrayResponseType = HttpResponse<IPrzelewy24Trx[]>;

@Injectable({ providedIn: 'root' })
export class Przelewy24TrxService {
    public resourceUrl = SERVER_API_URL + 'api/przelewy-24-trxes';
    public resourceSearchUrl = SERVER_API_URL + 'api/_search/przelewy-24-trxes';

    constructor(protected http: HttpClient) {}

    create(przelewy24Trx: IPrzelewy24Trx): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(przelewy24Trx);
        return this.http
            .post<IPrzelewy24Trx>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    update(przelewy24Trx: IPrzelewy24Trx): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(przelewy24Trx);
        return this.http
            .put<IPrzelewy24Trx>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http
            .get<IPrzelewy24Trx>(`${this.resourceUrl}/${id}`, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<IPrzelewy24Trx[]>(this.resourceUrl, { params: options, observe: 'response' })
            .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    search(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<IPrzelewy24Trx[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
            .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
    }

    protected convertDateFromClient(przelewy24Trx: IPrzelewy24Trx): IPrzelewy24Trx {
        const copy: IPrzelewy24Trx = Object.assign({}, przelewy24Trx, {
            createdDate:
                przelewy24Trx.createdDate != null && przelewy24Trx.createdDate.isValid() ? przelewy24Trx.createdDate.toJSON() : null,
            confirmedDate:
                przelewy24Trx.confirmedDate != null && przelewy24Trx.confirmedDate.isValid() ? przelewy24Trx.confirmedDate.toJSON() : null
        });
        return copy;
    }

    protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
        if (res.body) {
            res.body.createdDate = res.body.createdDate != null ? moment(res.body.createdDate) : null;
            res.body.confirmedDate = res.body.confirmedDate != null ? moment(res.body.confirmedDate) : null;
        }
        return res;
    }

    protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
        if (res.body) {
            res.body.forEach((przelewy24Trx: IPrzelewy24Trx) => {
                przelewy24Trx.createdDate = przelewy24Trx.createdDate != null ? moment(przelewy24Trx.createdDate) : null;
                przelewy24Trx.confirmedDate = przelewy24Trx.confirmedDate != null ? moment(przelewy24Trx.confirmedDate) : null;
            });
        }
        return res;
    }
}
