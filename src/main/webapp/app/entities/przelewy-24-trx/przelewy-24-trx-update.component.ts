import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { IPrzelewy24Trx } from 'app/shared/model/przelewy-24-trx.model';
import { Przelewy24TrxService } from './przelewy-24-trx.service';

@Component({
    selector: 'jhi-przelewy-24-trx-update',
    templateUrl: './przelewy-24-trx-update.component.html'
})
export class Przelewy24TrxUpdateComponent implements OnInit {
    private _przelewy24Trx: IPrzelewy24Trx;
    isSaving: boolean;
    createdDate: string;
    confirmedDate: string;

    constructor(private przelewy24TrxService: Przelewy24TrxService, private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ przelewy24Trx }) => {
            this.przelewy24Trx = przelewy24Trx;
        });
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        this.przelewy24Trx.createdDate = moment(this.createdDate, DATE_TIME_FORMAT);
        this.przelewy24Trx.confirmedDate = moment(this.confirmedDate, DATE_TIME_FORMAT);
        if (this.przelewy24Trx.id !== undefined) {
            this.subscribeToSaveResponse(this.przelewy24TrxService.update(this.przelewy24Trx));
        } else {
            this.subscribeToSaveResponse(this.przelewy24TrxService.create(this.przelewy24Trx));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<IPrzelewy24Trx>>) {
        result.subscribe((res: HttpResponse<IPrzelewy24Trx>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    private onSaveError() {
        this.isSaving = false;
    }
    get przelewy24Trx() {
        return this._przelewy24Trx;
    }

    set przelewy24Trx(przelewy24Trx: IPrzelewy24Trx) {
        this._przelewy24Trx = przelewy24Trx;
        this.createdDate = moment(przelewy24Trx.createdDate).format(DATE_TIME_FORMAT);
        this.confirmedDate = moment(przelewy24Trx.confirmedDate).format(DATE_TIME_FORMAT);
    }
}
