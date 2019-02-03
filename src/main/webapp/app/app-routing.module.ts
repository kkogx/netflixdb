import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { errorRoute, footerRoute, navbarRoute } from './layouts';
import { DEBUG_INFO_ENABLED } from 'app/app.constants';

const LAYOUT_ROUTES = [navbarRoute, footerRoute, ...errorRoute];

@NgModule({
    imports: [
        RouterModule.forRoot(
            [
                {
                    path: 'admin',
                    loadChildren: './admin/admin.module#NetflixdbAdminModule'
                },
                ...LAYOUT_ROUTES
            ],
            { useHash: true, enableTracing: DEBUG_INFO_ENABLED }
        )
    ],
    exports: [RouterModule]
})
export class NetflixdbAppRoutingModule {}
