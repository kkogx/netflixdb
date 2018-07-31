import { LOCALE_ID, NgModule } from '@angular/core';
import { DatePipe, registerLocaleData } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Title } from '@angular/platform-browser';
import locale from '@angular/common/locales/pl';

@NgModule({
    imports: [HttpClientModule],
    exports: [],
    declarations: [],
    providers: [
        Title,
        {
            provide: LOCALE_ID,
            useValue: 'pl'
        },
        DatePipe
    ]
})
export class NetflixdbCoreModule {
    constructor() {
        registerLocaleData(locale);
    }
}
