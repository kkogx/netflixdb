import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPrzelewy24Trx } from 'app/shared/model/przelewy-24-trx.model';

@Component({
    selector: 'jhi-przelewy-24-trx-detail',
    templateUrl: './przelewy-24-trx-detail.component.html'
})
export class Przelewy24TrxDetailComponent implements OnInit {
    przelewy24Trx: IPrzelewy24Trx;

    constructor(protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ przelewy24Trx }) => {
            this.przelewy24Trx = przelewy24Trx;
        });
    }

    previousState() {
        window.history.back();
    }
}
