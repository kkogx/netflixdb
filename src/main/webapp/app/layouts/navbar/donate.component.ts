import { Component, Input, OnInit } from '@angular/core';
import { Md5 } from 'ts-md5/dist/md5';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DonateService } from 'app/layouts/navbar/donate.service';
import { Przelewy24 } from 'app/layouts/navbar/donate.model';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

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
            <img src="../../../content/images/przelewy24_150.png">
            <form class="form-inline">
                <div class="donate-form input-group mt-3">
                    <div class="input-group-prepend"><span class="input-group-text"
                                                           jhiTranslate="donate.form.amount"></span></div>
                    <input type="number" class="form-control" name="amount" [(ngModel)]="amount"/>
                    <div class="input-group-append"><span class="input-group-text">{{currency}}</span></div>
                </div>
                <div class="donate-form input-group mt-3">
                    <div class="input-group-prepend"><span class="input-group-text"
                                                           jhiTranslate="donate.form.email"></span></div>
                    <input type="email" class="form-control" name="email" [(ngModel)]="email"/>
                </div>
                <div class="donate-form input-group mt-3">
                    <div class="input-group-prepend"><span class="input-group-text"
                                                           jhiTranslate="donate.form.description"></span></div>
                    <input type="text" class="form-control" name="description" [(ngModel)]="description"
                           value="I love Netflixdb!"/>
                </div>
            </form>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-secondary" (click)="activeModal.close('Close click')"
                    jhiTranslate="donate.cancel"></button>
            <button type="button" class="btn btn-primary" (click)=send() jhiTranslate="donate.send"></button>
        </div>
    `
})
export class DonateModalContentComponent implements OnInit {
    @Input() name;

    private przelewy24: Przelewy24;
    private amount: number;
    private currency: string;
    private description: string;
    private email: string;

    constructor(
        public activeModal: NgbActiveModal,
        private http: HttpClient,
        private donateService: DonateService,
        private router: Router
    ) {
        this.currency = 'PLN';
    }

    ngOnInit(): void {
        this.donateService.getPrzelewy24().then(przelewy24 => {
            this.przelewy24 = przelewy24;
        });
    }

    public send() {
        console.log(this.przelewy24.host);

        const uuid =
            Math.random()
                .toString(36)
                .substring(2, 15) +
            Math.random()
                .toString(36)
                .substring(2, 15);
        const sessionId = Md5.hashStr(Date.now().toString() + uuid);
        const amount = Math.trunc(this.amount * 100);
        const req = {
            p24_merchant_id: this.przelewy24.merchantId,
            p24_pos_id: this.przelewy24.merchantId,
            p24_session_id: Md5.hashStr(Date.now().toString() + uuid),
            p24_amount: amount,
            p24_currency: this.currency,
            p24_description: this.description,
            p24_email: this.email,
            p24_country: 'PL',
            p24_url_return: this.router.url,
            p24_transfer_label: this.description,
            p24_sign: Md5.hashStr(`${sessionId}|${this.przelewy24.merchantId}|${amount}|${this.currency}|${this.przelewy24.crc}`),
            p24_api_version: '3.2'
        };

        this.donateService
            .txnRegisterP24(req)
            .toPromise()
            .then(
                res => {
                    console.log(res);
                },
                err => {
                    console.error(err);
                }
            );
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
        modalRef.componentInstance.name = 'World';
    }
}
