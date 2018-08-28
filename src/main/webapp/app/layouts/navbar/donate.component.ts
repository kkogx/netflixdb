import { Component, Inject, Input, OnInit } from '@angular/core';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DonateService } from 'app/layouts/navbar/donate.service';
import { Przelewy24Trx } from 'app/layouts/navbar/donate.model';
import { HttpClient } from '@angular/common/http';
import { DOCUMENT } from '@angular/common';
import { Router } from '@angular/router';
import { Ng4LoadingSpinnerService } from 'ng4-loading-spinner';

@Component({
    selector: 'jhi-donate-modal-content',
    template: `
        <div class="modal-header">
            <h4 class="modal-title" jhiTranslate="donate.title"></h4>
            <button type="button" class="close" aria-label="Close" (click)="activeModal.dismiss('Cross click')">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <div class="modal-body">
            <ng4-loading-spinner></ng4-loading-spinner>
            <form ngNoForm action="https://www.paypal.com/cgi-bin/webscr" method="post" target="_top">
                <input type="hidden" name="cmd" value="_s-xclick">
                <input type="hidden" name="hosted_button_id" value="AUSN8EHWBSDGS">
                <input type="image" src="https://www.paypalobjects.com/pl_PL/PL/i/btn/btn_donateCC_LG.gif" border="0" name="submit" alt="PayPal – Płać wygodnie i bezpiecznie">
                <img alt="" border="0" src="https://www.paypalobjects.com/pl_PL/i/scr/pixel.gif" width="1" height="1">
            </form>
            <!--<img src="../../../content/images/przelewy24_150.png">-->
            <!--<form class="form-inline">-->
                <!--<div class="donate-form input-group mt-3">-->
                    <!--<div class="input-group-prepend">-->
                        <!--<span class="input-group-text" jhiTranslate="donate.form.amount"></span>-->
                    <!--</div>-->
                    <!--<input type="number" class="form-control" name="amount" required [(ngModel)]="amount"/>-->
                    <!--<div class="input-group-append"><span class="input-group-text">{{currency}}</span></div>-->
                <!--</div>-->
                <!--<div class="donate-form input-group mt-3">-->
                    <!--<div class="input-group-prepend">-->
                        <!--<span class="input-group-text" jhiTranslate="donate.form.email"></span>-->
                    <!--</div>-->
                    <!--<input type="email" class="form-control" name="email"  required [(ngModel)]="email"/>-->
                <!--</div>-->
                <!--<div class="donate-form input-group mt-3">-->
                    <!--<div class="input-group-prepend">-->
                        <!--<span class="input-group-text" jhiTranslate="donate.form.description"></span>-->
                    <!--</div>-->
                    <!--<input type="text" class="form-control" name="description" [(ngModel)]="description"/>-->
                <!--</div>-->
            <!--</form>-->
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-secondary" (click)="activeModal.close('Close click')"
                    jhiTranslate="donate.cancel"></button>
            <!--<button type="button" class="btn btn-primary" (click)=send() jhiTranslate="donate.send"></button>-->
            <!--<button type="button" class="btn btn-success" (click)=redirect() jhiTranslate="donate.pay"-->
                    <!--[disabled]="!showResponse"></button>-->
        </div>
    `
})
export class DonateModalContentComponent implements OnInit {
    public static DONATED_PARAM = 'donated';

    @Input() name;

    public amount: number;
    public readonly currency: string;
    public description: string;
    public email: string;
    public paymentLink: string;
    public showResponse: boolean;

    constructor(
        @Inject(DOCUMENT) private document: any,
        public activeModal: NgbActiveModal,
        private http: HttpClient,
        private router: Router,
        private donateService: DonateService,
        private spinnerService: Ng4LoadingSpinnerService
    ) {
        this.currency = 'PLN';
        this.showResponse = false;
    }

    ngOnInit(): void {
        this.description = 'I love Netflixdb.pl!';
    }

    public send() {
        const baseUrl = document.location.protocol + '//' + document.location.host + '/';
        const p24 = new Przelewy24Trx();
        p24.country = 'PL';
        p24.language = 'pl';
        p24.currency = this.currency;
        p24.description = this.description;
        p24.email = this.email;
        p24.transferLabel = this.description;
        p24.urlReturn = baseUrl + `?${DonateModalContentComponent.DONATED_PARAM}=true`;
        p24.amount = this.amount;

        this.spinnerService.show();
        this.donateService
            .txnRegisterP24(p24)
            .toPromise()
            .then(
                res => {
                    this.paymentLink = res['url'];
                    this.showResponse = true;
                    this.spinnerService.hide();
                },
                err => {
                    console.error(err);
                    this.spinnerService.hide();
                }
            );
    }

    public redirect() {
        document.location.href = this.paymentLink;
    }
}

@Component({
    selector: 'jhi-donate-modal-component',
    templateUrl: './donate.component.html'
})
export class DonateModalComponent {
    constructor(private modalService: NgbModal) {}

    open() {
        const modalRef = this.modalService.open(DonateModalContentComponent);
        modalRef.componentInstance.name = 'Donate';
    }
}
