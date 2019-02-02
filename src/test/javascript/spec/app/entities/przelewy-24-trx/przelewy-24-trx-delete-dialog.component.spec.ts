/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { NetflixdbTestModule } from '../../../test.module';
import { Przelewy24TrxDeleteDialogComponent } from 'app/entities/przelewy-24-trx/przelewy-24-trx-delete-dialog.component';
import { Przelewy24TrxService } from 'app/entities/przelewy-24-trx/przelewy-24-trx.service';

describe('Component Tests', () => {
    describe('Przelewy24Trx Management Delete Component', () => {
        let comp: Przelewy24TrxDeleteDialogComponent;
        let fixture: ComponentFixture<Przelewy24TrxDeleteDialogComponent>;
        let service: Przelewy24TrxService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [NetflixdbTestModule],
                declarations: [Przelewy24TrxDeleteDialogComponent]
            })
                .overrideTemplate(Przelewy24TrxDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(Przelewy24TrxDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(Przelewy24TrxService);
            mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
            mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
        });

        describe('confirmDelete', () => {
            it('Should call delete service on confirmDelete', inject(
                [],
                fakeAsync(() => {
                    // GIVEN
                    spyOn(service, 'delete').and.returnValue(of({}));

                    // WHEN
                    comp.confirmDelete(123);
                    tick();

                    // THEN
                    expect(service.delete).toHaveBeenCalledWith(123);
                    expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                    expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
                })
            ));
        });
    });
});
