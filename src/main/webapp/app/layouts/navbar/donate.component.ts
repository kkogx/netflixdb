import { Component, Inject, Input, OnInit } from '@angular/core';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DonateService } from 'app/layouts/navbar/donate.service';
import { Przelewy24, Przelewy24Trx } from 'app/layouts/navbar/donate.model';
import { HttpClient } from '@angular/common/http';
import { DOCUMENT } from '@angular/common';

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
        <div *ngIf="showResponse">
            <a href="{{paymentLink}}">{{paymentLink}}</a>
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
    private paymentLink: string;
    private showResponse: boolean;

    constructor(
        @Inject(DOCUMENT) private document: any,
        public activeModal: NgbActiveModal,
        private http: HttpClient,
        private donateService: DonateService
    ) {
        this.currency = 'PLN';
        this.showResponse = false;
    }

    ngOnInit(): void {
        this.donateService.getPrzelewy24().then(przelewy24 => {
            this.przelewy24 = przelewy24;
        });
    }

    public send() {
        console.log(this.przelewy24.host);

        const p24 = new Przelewy24Trx();
        p24.country = 'PL';
        p24.currency = this.currency;
        p24.description = this.description;
        p24.email = this.email;
        p24.transferLabel = this.description;
        p24.urlReturn = document.location.href;
        p24.amount = this.amount;

        this.donateService
            .txnRegisterP24(p24)
            .toPromise()
            .then(
                res => {
                    this.paymentLink = res.body;
                    this.showResponse = true;
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
