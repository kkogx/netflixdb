import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IPrzelewy24Trx } from 'app/shared/model/przelewy-24-trx.model';
import { Przelewy24TrxService } from './przelewy-24-trx.service';

@Component({
    selector: 'jhi-przelewy-24-trx-delete-dialog',
    templateUrl: './przelewy-24-trx-delete-dialog.component.html'
})
export class Przelewy24TrxDeleteDialogComponent {
    przelewy24Trx: IPrzelewy24Trx;

    constructor(
        private przelewy24TrxService: Przelewy24TrxService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.przelewy24TrxService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'przelewy24TrxListModification',
                content: 'Deleted an przelewy24Trx'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-przelewy-24-trx-delete-popup',
    template: ''
})
export class Przelewy24TrxDeletePopupComponent implements OnInit, OnDestroy {
    private ngbModalRef: NgbModalRef;

    constructor(private activatedRoute: ActivatedRoute, private router: Router, private modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ przelewy24Trx }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(Przelewy24TrxDeleteDialogComponent as Component, {
                    size: 'lg',
                    backdrop: 'static'
                });
                this.ngbModalRef.componentInstance.przelewy24Trx = przelewy24Trx;
                this.ngbModalRef.result.then(
                    result => {
                        this.router.navigate([{ outlets: { popup: null } }], { replaceUrl: true, queryParamsHandling: 'merge' });
                        this.ngbModalRef = null;
                    },
                    reason => {
                        this.router.navigate([{ outlets: { popup: null } }], { replaceUrl: true, queryParamsHandling: 'merge' });
                        this.ngbModalRef = null;
                    }
                );
            }, 0);
        });
    }

    ngOnDestroy() {
        this.ngbModalRef = null;
    }
}
