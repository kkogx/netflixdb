import { Component, Inject, Input, OnInit } from '@angular/core';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DOCUMENT } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';

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
            <!--<ng4-loading-spinner></ng4-loading-spinner>-->
            <!--<form ngNoForm action="https://www.paypal.com/cgi-bin/webscr" method="post" target="_top">-->
                <!--<input type="hidden" name="cmd" value="_s-xclick">-->
                <!--<input type="hidden" name="hosted_button_id" value="AUSN8EHWBSDGS">-->
                <!--<input type="image" src="https://www.paypalobjects.com/pl_PL/PL/i/btn/btn_donateCC_LG.gif" border="0"-->
                       <!--name="submit" alt="PayPal – Płać wygodnie i bezpiecznie">-->
                <!--<img alt="" border="0" src="https://www.paypalobjects.com/pl_PL/i/scr/pixel.gif" width="1" height="1">-->
            <!--</form>-->
            <!--<hr>-->
            <img src="../../../content/images/dp_logo_whte_alpha_150.png">
            <form #donateForm="ngForm" class="form-inline">
                <div class="donate-form input-group mt-3">
                    <div class="input-group-prepend">
                        <span class="input-group-text" jhiTranslate="donate.form.amount"></span>
                    </div>
                    <input type="number" class="form-control" name="amount" required [(ngModel)]="amount"/>
                    <div class="input-group-append"><span class="input-group-text">{{currency}}</span></div>
                </div>
                <div class="donate-form input-group mt-3">
                    <div class="input-group-prepend">
                        <span class="input-group-text" jhiTranslate="donate.form.email"></span>
                    </div>
                    <input type="email" class="form-control" name="email" required [(ngModel)]="email"/>
                </div>
                <div class="donate-form input-group mt-3">
                    <div class="input-group-prepend">
                        <span class="input-group-text" jhiTranslate="donate.form.description"></span>
                    </div>
                    <input type="text" class="form-control" name="description" [(ngModel)]="description"/>
                </div>
            </form>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-secondary" (click)="activeModal.close('Close click')" jhiTranslate="donate.cancel"></button>
            <button type="button" class="btn btn-success" (click)=redirect() jhiTranslate="donate.pay" [disabled]="donateForm.form.invalid"></button>
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

    constructor(@Inject(DOCUMENT) private document: any, private translateService: TranslateService, public activeModal: NgbActiveModal) {
        this.currency = 'PLN';
    }

    ngOnInit(): void {
        this.description = 'I love Netflixdb.pl!';
    }

    public redirect() {
        const returnBaseUrl = document.location.protocol + '//' + document.location.host + '/';

        const params: URLSearchParams = new URLSearchParams();
        params.append('id', '275533');
        params.append('kwota', this.amount.toFixed(2));
        params.append('opis', this.description);
        params.append('waluta', this.currency);
        params.append('URL', returnBaseUrl + `?${DonateModalContentComponent.DONATED_PARAM}=true`);
        params.append('typ', '0');
        params.append('txtguzik', this.translateService.instant('donate.returnlabel'));
        params.append('email', this.email);
        params.append('imie', this.translateService.instant('donate.donor'));
        params.append('nazwisko', this.translateService.instant('donate.donor'));

        const paymentBaseUrl = 'https://ssl.dotpay.pl/t2/';
        const paymentUrl = paymentBaseUrl + '?' + params.toString();
        document.location.href = paymentUrl;
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
