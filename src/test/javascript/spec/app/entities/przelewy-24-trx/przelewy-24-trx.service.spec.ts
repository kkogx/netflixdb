/* tslint:disable max-line-length */
import { getTestBed, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { map, take } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { Przelewy24TrxService } from 'app/entities/przelewy-24-trx/przelewy-24-trx.service';
import { IPrzelewy24Trx, Przelewy24Trx } from 'app/shared/model/przelewy-24-trx.model';

describe('Service Tests', () => {
    describe('Przelewy24Trx Service', () => {
        let injector: TestBed;
        let service: Przelewy24TrxService;
        let httpMock: HttpTestingController;
        let elemDefault: IPrzelewy24Trx;
        let currentDate: moment.Moment;
        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [HttpClientTestingModule]
            });
            injector = getTestBed();
            service = injector.get(Przelewy24TrxService);
            httpMock = injector.get(HttpTestingController);
            currentDate = moment();

            elemDefault = new Przelewy24Trx(0, currentDate, currentDate, 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 0);
        });

        describe('Service methods', async () => {
            it('should find an element', async () => {
                const returnedFromService = Object.assign(
                    {
                        createdDate: currentDate.format(DATE_TIME_FORMAT),
                        confirmedDate: currentDate.format(DATE_TIME_FORMAT)
                    },
                    elemDefault
                );
                service
                    .find(123)
                    .pipe(take(1))
                    .subscribe(resp => expect(resp).toMatchObject({ body: elemDefault }));

                const req = httpMock.expectOne({ method: 'GET' });
                req.flush(JSON.stringify(returnedFromService));
            });

            it('should create a Przelewy24Trx', async () => {
                const returnedFromService = Object.assign(
                    {
                        id: 0,
                        createdDate: currentDate.format(DATE_TIME_FORMAT),
                        confirmedDate: currentDate.format(DATE_TIME_FORMAT)
                    },
                    elemDefault
                );
                const expected = Object.assign(
                    {
                        createdDate: currentDate,
                        confirmedDate: currentDate
                    },
                    returnedFromService
                );
                service
                    .create(new Przelewy24Trx(null))
                    .pipe(take(1))
                    .subscribe(resp => expect(resp).toMatchObject({ body: expected }));
                const req = httpMock.expectOne({ method: 'POST' });
                req.flush(JSON.stringify(returnedFromService));
            });

            it('should update a Przelewy24Trx', async () => {
                const returnedFromService = Object.assign(
                    {
                        createdDate: currentDate.format(DATE_TIME_FORMAT),
                        confirmedDate: currentDate.format(DATE_TIME_FORMAT),
                        email: 'BBBBBB',
                        sessionId: 'BBBBBB',
                        description: 'BBBBBB',
                        currency: 'BBBBBB',
                        country: 'BBBBBB',
                        amount: 1
                    },
                    elemDefault
                );

                const expected = Object.assign(
                    {
                        createdDate: currentDate,
                        confirmedDate: currentDate
                    },
                    returnedFromService
                );
                service
                    .update(expected)
                    .pipe(take(1))
                    .subscribe(resp => expect(resp).toMatchObject({ body: expected }));
                const req = httpMock.expectOne({ method: 'PUT' });
                req.flush(JSON.stringify(returnedFromService));
            });

            it('should return a list of Przelewy24Trx', async () => {
                const returnedFromService = Object.assign(
                    {
                        createdDate: currentDate.format(DATE_TIME_FORMAT),
                        confirmedDate: currentDate.format(DATE_TIME_FORMAT),
                        email: 'BBBBBB',
                        sessionId: 'BBBBBB',
                        description: 'BBBBBB',
                        currency: 'BBBBBB',
                        country: 'BBBBBB',
                        amount: 1
                    },
                    elemDefault
                );
                const expected = Object.assign(
                    {
                        createdDate: currentDate,
                        confirmedDate: currentDate
                    },
                    returnedFromService
                );
                service
                    .query(expected)
                    .pipe(
                        take(1),
                        map(resp => resp.body)
                    )
                    .subscribe(body => expect(body).toContainEqual(expected));
                const req = httpMock.expectOne({ method: 'GET' });
                req.flush(JSON.stringify([returnedFromService]));
                httpMock.verify();
            });

            it('should delete a Przelewy24Trx', async () => {
                const rxPromise = service.delete(123).subscribe(resp => expect(resp.ok));

                const req = httpMock.expectOne({ method: 'DELETE' });
                req.flush({ status: 200 });
            });
        });

        afterEach(() => {
            httpMock.verify();
        });
    });
});
