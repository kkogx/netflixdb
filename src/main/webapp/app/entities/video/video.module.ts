import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormsModule } from '@angular/forms';
import { LazyLoadImageModule } from 'ng-lazyload-image';
import { JhiLanguageService } from 'ng-jhipster';
import { JhiLanguageHelper } from 'app/core';

import { NetflixdbSharedModule } from 'app/shared';
import {
    VideoComponent,
    VideoDeleteDialogComponent,
    VideoDeletePopupComponent,
    VideoDetailComponent,
    videoPopupRoute,
    videoRoute,
    VideoUpdateComponent
} from './';

const ENTITY_STATES = [...videoRoute, ...videoPopupRoute];

@NgModule({
    imports: [NetflixdbSharedModule, RouterModule.forChild(ENTITY_STATES), NgSelectModule, FormsModule, LazyLoadImageModule],
    declarations: [VideoComponent, VideoDetailComponent, VideoUpdateComponent, VideoDeleteDialogComponent, VideoDeletePopupComponent],
    entryComponents: [VideoComponent, VideoUpdateComponent, VideoDeleteDialogComponent, VideoDeletePopupComponent],
    providers: [{ provide: JhiLanguageService, useClass: JhiLanguageService }],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class NetflixdbVideoModule {
    constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
        this.languageHelper.language.subscribe((languageKey: string) => {
            if (languageKey !== undefined) {
                this.languageService.changeLanguage(languageKey);
            }
        });
    }
}
