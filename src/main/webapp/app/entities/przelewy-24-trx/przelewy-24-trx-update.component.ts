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
    przelewy24Trx: IPrzelewy24Trx;
    isSaving: boolean;
    createdDate: string;
    confirmedDate: string;

    constructor(protected przelewy24TrxService: Przelewy24TrxService, protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ przelewy24Trx }) => {
            this.przelewy24Trx = przelewy24Trx;
            this.createdDate = this.przelewy24Trx.createdDate != null ? this.przelewy24Trx.createdDate.format(DATE_TIME_FORMAT) : null;
            this.confirmedDate =
                this.przelewy24Trx.confirmedDate != null ? this.przelewy24Trx.confirmedDate.format(DATE_TIME_FORMAT) : null;
        });
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        this.przelewy24Trx.createdDate = this.createdDate != null ? moment(this.createdDate, DATE_TIME_FORMAT) : null;
        this.przelewy24Trx.confirmedDate = this.confirmedDate != null ? moment(this.confirmedDate, DATE_TIME_FORMAT) : null;
        if (this.przelewy24Trx.id !== undefined) {
            this.subscribeToSaveResponse(this.przelewy24TrxService.update(this.przelewy24Trx));
        } else {
            this.subscribeToSaveResponse(this.przelewy24TrxService.create(this.przelewy24Trx));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<IPrzelewy24Trx>>) {
        result.subscribe((res: HttpResponse<IPrzelewy24Trx>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    protected onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    protected onSaveError() {
        this.isSaving = false;
    }
}
