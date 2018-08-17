/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { NetflixdbTestModule } from '../../../test.module';
import { Przelewy24TrxDetailComponent } from 'app/entities/przelewy-24-trx/przelewy-24-trx-detail.component';
import { Przelewy24Trx } from 'app/shared/model/przelewy-24-trx.model';

describe('Component Tests', () => {
    describe('Przelewy24Trx Management Detail Component', () => {
        let comp: Przelewy24TrxDetailComponent;
        let fixture: ComponentFixture<Przelewy24TrxDetailComponent>;
        const route = ({ data: of({ przelewy24Trx: new Przelewy24Trx(123) }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [NetflixdbTestModule],
                declarations: [Przelewy24TrxDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(Przelewy24TrxDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(Przelewy24TrxDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.przelewy24Trx).toEqual(jasmine.objectContaining({ id: 123 }));
            });
        });
    });
});
