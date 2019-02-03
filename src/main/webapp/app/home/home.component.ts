import { Component, OnInit } from '@angular/core';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { Account, AccountService, LoginModalService } from 'app/core';
import { ProfileService } from 'app/layouts/profiles/profile.service';
import { ActivatedRoute } from '@angular/router';
import { DonateModalContentComponent } from 'app/layouts/navbar/donate.component';

@Component({
    selector: 'jhi-home',
    templateUrl: './home.component.html',
    styleUrls: ['home.css']
})
export class HomeComponent implements OnInit {
    inProduction: boolean;
    account: Account;
    modalRef: NgbModalRef;
    registrationOpen: boolean;
    showThanks: boolean;

    constructor(
        private accountService: AccountService,
        private loginModalService: LoginModalService,
        private eventManager: JhiEventManager,
        private profileService: ProfileService,
        private route: ActivatedRoute
    ) {}

    ngOnInit() {
        this.accountService.identity().then((account: Account) => {
            this.account = account;
        });
        this.registerAuthenticationSuccess();
        this.profileService.getProfileInfo().then(profileInfo => {
            this.registrationOpen = !profileInfo.registrationClosed;
            this.inProduction = profileInfo.inProduction;
        });
        this.route.queryParams.subscribe(params => {
            if (params[DonateModalContentComponent.DONATED_PARAM]) {
                this.showThanks = true;
            }
        });
    }

    registerAuthenticationSuccess() {
        this.eventManager.subscribe('authenticationSuccess', message => {
            this.accountService.identity().then(account => {
                this.account = account;
            });
        });
    }

    isAuthenticated() {
        return this.accountService.isAuthenticated();
    }

    login() {
        this.modalRef = this.loginModalService.open();
    }
}
