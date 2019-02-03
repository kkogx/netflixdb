import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    // prettier-ignore
    imports: [
        RouterModule.forChild([
            {
                path: '',
                loadChildren: './video/video.module#NetflixdbVideoModule'
            },
            {
                path: 'przelewy-24-trx',
                loadChildren: './przelewy-24-trx/przelewy-24-trx.module#NetflixdbPrzelewy24TrxModule'
            }
            /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
        ])
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class NetflixdbEntityModule {}
