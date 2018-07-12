import { NgModule } from '@angular/core';

import { NetflixdbSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent } from './';

@NgModule({
    imports: [NetflixdbSharedLibsModule],
    declarations: [JhiAlertComponent, JhiAlertErrorComponent],
    exports: [NetflixdbSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent]
})
export class NetflixdbSharedCommonModule {}
