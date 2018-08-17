import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { of } from 'rxjs';
import { map } from 'rxjs/operators';
import { IPrzelewy24Trx, Przelewy24Trx } from 'app/shared/model/przelewy-24-trx.model';
import { Przelewy24TrxService } from './przelewy-24-trx.service';
import { Przelewy24TrxComponent } from './przelewy-24-trx.component';
import { Przelewy24TrxDetailComponent } from './przelewy-24-trx-detail.component';
import { Przelewy24TrxUpdateComponent } from './przelewy-24-trx-update.component';
import { Przelewy24TrxDeletePopupComponent } from './przelewy-24-trx-delete-dialog.component';

@Injectable({ providedIn: 'root' })
export class Przelewy24TrxResolve implements Resolve<IPrzelewy24Trx> {
    constructor(private service: Przelewy24TrxService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(map((przelewy24Trx: HttpResponse<Przelewy24Trx>) => przelewy24Trx.body));
        }
        return of(new Przelewy24Trx());
    }
}

export const przelewy24TrxRoute: Routes = [
    {
        path: 'przelewy-24-trx',
        component: Przelewy24TrxComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Przelewy24Trxes'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'przelewy-24-trx/:id/view',
        component: Przelewy24TrxDetailComponent,
        resolve: {
            przelewy24Trx: Przelewy24TrxResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Przelewy24Trxes'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'przelewy-24-trx/new',
        component: Przelewy24TrxUpdateComponent,
        resolve: {
            przelewy24Trx: Przelewy24TrxResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Przelewy24Trxes'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'przelewy-24-trx/:id/edit',
        component: Przelewy24TrxUpdateComponent,
        resolve: {
            przelewy24Trx: Przelewy24TrxResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Przelewy24Trxes'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const przelewy24TrxPopupRoute: Routes = [
    {
        path: 'przelewy-24-trx/:id/delete',
        component: Przelewy24TrxDeletePopupComponent,
        resolve: {
            przelewy24Trx: Przelewy24TrxResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Przelewy24Trxes'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
