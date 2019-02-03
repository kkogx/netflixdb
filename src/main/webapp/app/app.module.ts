import './vendor.ts';

import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { Ng2Webstorage } from 'ngx-webstorage';
import { NgJhipsterModule } from 'ng-jhipster';
import { NgbDatepickerConfig } from '@ng-bootstrap/ng-bootstrap';

import { AuthInterceptor } from './blocks/interceptor/auth.interceptor';
import { AuthExpiredInterceptor } from './blocks/interceptor/auth-expired.interceptor';
import { ErrorHandlerInterceptor } from './blocks/interceptor/errorhandler.interceptor';
import { NotificationInterceptor } from './blocks/interceptor/notification.interceptor';
import { NetflixdbSharedModule } from 'app/shared';
import { NetflixdbCoreModule } from 'app/core';
import { NetflixdbAppRoutingModule } from './app-routing.module';
import { NetflixdbHomeModule } from './home/home.module';
import { NetflixdbAccountModule } from './account/account.module';
import { NetflixdbEntityModule } from './entities/entity.module';
import { Ng4LoadingSpinnerModule } from 'ng4-loading-spinner';
import * as moment from 'moment';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import {
    ActiveMenuDirective,
    DonateModalComponent,
    DonateModalContentComponent,
    ErrorComponent,
    FooterComponent,
    JhiMainComponent,
    NavbarComponent,
    PageRibbonComponent
} from './layouts';

@NgModule({
    imports: [
        BrowserModule,
        Ng2Webstorage.forRoot({ prefix: 'jhi', separator: '-' }),
        NgJhipsterModule.forRoot({
            // set below to true to make alerts look like toast
            alertAsToast: false,
            alertTimeout: 5000,
            i18nEnabled: true,
            defaultI18nLang: 'pl'
        }),
        NetflixdbSharedModule.forRoot(),
        NetflixdbCoreModule,
        NetflixdbHomeModule,
        NetflixdbAccountModule,
        NetflixdbEntityModule,
        NetflixdbAppRoutingModule,
        // jhipster-needle-angular-add-module JHipster will add new module here
        Ng4LoadingSpinnerModule.forRoot()
    ],
    declarations: [
        JhiMainComponent,
        NavbarComponent,
        ErrorComponent,
        PageRibbonComponent,
        ActiveMenuDirective,
        FooterComponent,
        DonateModalComponent,
        DonateModalContentComponent
    ],
    entryComponents: [DonateModalContentComponent],
    providers: [
        {
            provide: HTTP_INTERCEPTORS,
            useClass: AuthInterceptor,
            multi: true
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: AuthExpiredInterceptor,
            multi: true
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: ErrorHandlerInterceptor,
            multi: true
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: NotificationInterceptor,
            multi: true
        }
    ],
    bootstrap: [JhiMainComponent]
})
export class NetflixdbAppModule {
    constructor(private dpConfig: NgbDatepickerConfig) {
        this.dpConfig.minDate = { year: moment().year() - 100, month: 1, day: 1 };
    }
}
