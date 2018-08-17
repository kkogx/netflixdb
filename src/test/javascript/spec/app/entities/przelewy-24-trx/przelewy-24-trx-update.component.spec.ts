/* tslint:disable max-line-length */
import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { of } from 'rxjs';

import { NetflixdbTestModule } from '../../../test.module';
import { Przelewy24TrxUpdateComponent } from 'app/entities/przelewy-24-trx/przelewy-24-trx-update.component';
import { Przelewy24TrxService } from 'app/entities/przelewy-24-trx/przelewy-24-trx.service';
import { Przelewy24Trx } from 'app/shared/model/przelewy-24-trx.model';

describe('Component Tests', () => {
    describe('Przelewy24Trx Management Update Component', () => {
        let comp: Przelewy24TrxUpdateComponent;
        let fixture: ComponentFixture<Przelewy24TrxUpdateComponent>;
        let service: Przelewy24TrxService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [NetflixdbTestModule],
                declarations: [Przelewy24TrxUpdateComponent]
            })
                .overrideTemplate(Przelewy24TrxUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(Przelewy24TrxUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(Przelewy24TrxService);
        });

        describe('save', () => {
            it(
                'Should call update service on save for existing entity',
                fakeAsync(() => {
                    // GIVEN
                    const entity = new Przelewy24Trx(123);
                    spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.przelewy24Trx = entity;
                    // WHEN
                    comp.save();
                    tick(); // simulate async

                    // THEN
                    expect(service.update).toHaveBeenCalledWith(entity);
                    expect(comp.isSaving).toEqual(false);
                })
            );

            it(
                'Should call create service on save for new entity',
                fakeAsync(() => {
                    // GIVEN
                    const entity = new Przelewy24Trx();
                    spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.przelewy24Trx = entity;
                    // WHEN
                    comp.save();
                    tick(); // simulate async

                    // THEN
                    expect(service.create).toHaveBeenCalledWith(entity);
                    expect(comp.isSaving).toEqual(false);
                })
            );
        });
    });
});
