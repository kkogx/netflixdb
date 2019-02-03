import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { JhiLanguageHelper } from 'app/core';

import { NetflixdbSharedModule } from 'app/shared';
import {
    Przelewy24TrxComponent,
    Przelewy24TrxDeleteDialogComponent,
    Przelewy24TrxDeletePopupComponent,
    Przelewy24TrxDetailComponent,
    przelewy24TrxPopupRoute,
    przelewy24TrxRoute,
    Przelewy24TrxUpdateComponent
} from './';

const ENTITY_STATES = [...przelewy24TrxRoute, ...przelewy24TrxPopupRoute];

@NgModule({
    imports: [NetflixdbSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        Przelewy24TrxComponent,
        Przelewy24TrxDetailComponent,
        Przelewy24TrxUpdateComponent,
        Przelewy24TrxDeleteDialogComponent,
        Przelewy24TrxDeletePopupComponent
    ],
    entryComponents: [
        Przelewy24TrxComponent,
        Przelewy24TrxUpdateComponent,
        Przelewy24TrxDeleteDialogComponent,
        Przelewy24TrxDeletePopupComponent
    ],
    providers: [{ provide: JhiLanguageService, useClass: JhiLanguageService }],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class NetflixdbPrzelewy24TrxModule {
    constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
        this.languageHelper.language.subscribe((languageKey: string) => {
            if (languageKey !== undefined) {
                this.languageService.changeLanguage(languageKey);
            }
        });
    }
}
