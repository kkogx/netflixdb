import { Component, Input } from '@angular/core';

import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';

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
            <div class="input-group mt-3">
                <div class="input-group-prepend"><span class="input-group-text" jhiTranslate="donate.form.amount"></span></div>
                <input type="number" class="form-control" name="amount" [(ngModel)]="amount"/>
            </div>
            <div class="input-group mt-3">
                <div class="input-group-prepend"><span class="input-group-text" jhiTranslate="donate.form.currency"></span></div>
                <input type="text" class="form-control" name="currency" [(ngModel)]="currency" [readonly]="true"/>
            </div>
            <div class="input-group mt-3">
                <div class="input-group-prepend"><span class="input-group-text" jhiTranslate="donate.form.description"></span></div>
                <input type="text" class="form-control" name="description" [(ngModel)]="description" value="I love Netflixdb!"/>
            </div>
            <div class="input-group mt-3">
                <div class="input-group-prepend"><span class="input-group-text" jhiTranslate="donate.form.email"></span></div>
                <input type="text" class="form-control" name="email" [(ngModel)]="email"/>
            </div>
        </form>
    </div>
    <div class="modal-footer">
      <button type="button" class="btn btn-secondary" (click)="activeModal.close('Close click')" jhiTranslate="donate.cancel"></button>
      <button type="button" class="btn btn-primary" (click)="send()" jhiTranslate="donate.send"></button>
    </div>
  `
})
export class DonateModalContentComponent {
    @Input() name;

    constructor(public activeModal: NgbActiveModal) {}
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

    send() {}
}
