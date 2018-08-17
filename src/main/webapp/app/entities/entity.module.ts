import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';

import { NetflixdbVideoModule } from './video/video.module';

import { NetflixdbPrzelewy24TrxModule } from './przelewy-24-trx/przelewy-24-trx.module';

/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    // prettier-ignore
    imports: [
        NetflixdbVideoModule,
        NetflixdbPrzelewy24TrxModule,
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class NetflixdbEntityModule {}
