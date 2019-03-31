import { Component, OnDestroy, OnInit } from '@angular/core';
import { HttpErrorResponse, HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable, Subscription } from 'rxjs';
import { JhiAlertService, JhiEventManager, JhiParseLinks } from 'ng-jhipster';

import { IVideo } from 'app/shared/model/video.model';
import { Account } from 'app/core/user/account.model';
import { AccountService } from 'app/core';

import { ITEMS_PER_PAGE } from 'app/shared';
import { VideoService } from './video.service';
import { IGenre } from 'app/shared/model/genre.model';
import { ISeenOption, SeenOption, SeenOptionId } from 'app/shared/model/seen-option.model';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'jhi-video',
    templateUrl: './video.component.html'
})
export class VideoComponent implements OnInit, OnDestroy {
    runtimeSortAttrs: Array<String> = ['title', 'fwebTitle', 'genre'];

    videos: IVideo[];
    currentAccount: Account;
    eventSubscriber: Subscription;
    itemsPerPage: number;
    links: any;
    page: any;
    predicate: any;
    reverse: any;
    totalItems: number;

    currentSearch: string;
    searchByType: string;
    imdbVoteMin: number;
    fwebVoteMin: number;
    releaseYearMin: number;

    genres: Observable<IGenre[]>;
    selectedGenres: IGenre[] = [];

    seenOptions: ISeenOption[] = [
        new SeenOption(SeenOptionId.YES, this.translateService.instant('videos.filter.seenYes')),
        new SeenOption(SeenOptionId.NO, this.translateService.instant('videos.filter.seenNo'))
    ];
    selectedSeen: ISeenOption;

    constructor(
        protected videoService: VideoService,
        protected jhiAlertService: JhiAlertService,
        protected eventManager: JhiEventManager,
        protected parseLinks: JhiParseLinks,
        protected activatedRoute: ActivatedRoute,
        protected accountService: AccountService,
        private translateService: TranslateService
    ) {
        this.videos = [];
        this.itemsPerPage = ITEMS_PER_PAGE;
        this.page = 0;
        this.links = {
            last: 0
        };
        this.predicate = 'id';
        this.reverse = true;
        this.currentSearch =
            this.activatedRoute.snapshot && this.activatedRoute.snapshot.params['search']
                ? this.activatedRoute.snapshot.params['search']
                : '';
        this.searchByType = this.activatedRoute.snapshot.data['searchByType'];
    }

    loadAll() {
        this.videoService
            .search(
                {
                    query: this.currentSearch,
                    fwebMin: this.fwebVoteMin ? this.fwebVoteMin : 0,
                    imdbMin: this.imdbVoteMin ? this.imdbVoteMin : 0,
                    yearMin: this.releaseYearMin ? this.releaseYearMin : 0,
                    genres: this.selectedGenres.map(value => value.id),
                    seen: this.selectedSeen ? this.selectedSeen.id : SeenOptionId.ANY,
                    page: this.page,
                    size: this.itemsPerPage,
                    sort: this.sort()
                },
                this.searchByType
            )
            .subscribe(
                (res: HttpResponse<IVideo[]>) => this.paginateVideos(res.body, res.headers),
                (res: HttpErrorResponse) => this.onError(res.message)
            );
        return;
    }

    reset() {
        this.page = 0;
        this.videos = [];
        this.loadAll();
    }

    loadPage(page) {
        this.page = page;
        this.loadAll();
    }

    clear() {
        this.videos = [];
        this.links = {
            last: 0
        };
        this.page = 0;
        this.predicate = 'id';
        this.reverse = true;
        this.currentSearch = '';
        this.loadAll();
    }

    private ensureRange(value: number, max: number, min?: number): number {
        if (min === undefined) {
            min = 0;
        }
        value = Math.max(min, value);
        value = Math.min(max, value);
        if (value === 0) {
            value = undefined;
        }
        return value;
    }

    private ensureRanges() {
        this.fwebVoteMin = this.ensureRange(this.fwebVoteMin, 1000000);
        this.imdbVoteMin = this.ensureRange(this.imdbVoteMin, 2000000);
        this.releaseYearMin = this.ensureRange(this.releaseYearMin, 2020, 1940);
    }

    // called whenever min search param is changed
    public searchBlurMin(query) {
        this.ensureRanges();
        this.search(query);
    }

    search(query) {
        // if (!query) {
        //     return this.clear();
        // }
        this.videos = [];
        this.links = {
            last: 0
        };
        this.page = 0;
        // this.predicate = null; // '_score';
        this.reverse = false;
        this.currentSearch = query;
        this.loadAll();
    }

    ngOnInit() {
        this.genres = this.videoService.genres(this.searchByType);

        this.loadAll();
        this.accountService.identity().then(account => {
            this.currentAccount = account;
        });
        this.registerChangeInVideos();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: IVideo) {
        return item.id;
    }

    registerChangeInVideos() {
        this.eventSubscriber = this.eventManager.subscribe('videoListModification', response => this.reset());
    }

    sort() {
        if (this.predicate != null && !this.runtimeSortAttrs.find(value => value === this.predicate)) {
            const result = [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
            if (this.predicate !== 'id') {
                result.push('id');
            }
            return result;
        }
        return [];
    }

    protected paginateVideos(data: IVideo[], headers: HttpHeaders) {
        this.links = this.parseLinks.parse(headers.get('link'));
        this.totalItems = parseInt(headers.get('X-Total-Count'), 10);
        if (this.predicate != null && this.runtimeSortAttrs.find(value => value === this.predicate)) {
            data.sort((a, b) => {
                if (a[this.predicate] > b[this.predicate]) {
                    return this.reverse ? -1 : 1;
                } else if (a[this.predicate] < b[this.predicate]) {
                    return this.reverse ? 1 : -1;
                } else {
                    return 0;
                }
            });
        }

        // data transformation
        const seenIds = new Set(this.currentAccount == null ? [] : this.currentAccount.seenVideoIds);
        for (let i = 0; i < data.length; i++) {
            data[i].fwebRating = Math.round(data[i].fwebRating * 10) / 10;
            data[i].seen = seenIds.has(data[i].id);
            this.videos.push(data[i]);
        }
    }

    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }

    isAuthenticated() {
        return this.accountService.isAuthenticated();
    }

    toggleSeen(video: IVideo) {
        video.seenAnim = true;
        const endpoint = video.seen ? this.accountService.addSeen : this.accountService.removeSeen;
        endpoint
            .call(this.accountService, video.id)
            .subscribe(response => {
                if (response.status === 200) {
                    video.seen = !video.seen;
                }
            })
            .add(() => {
                video.seenAnim = false;
            });
    }
}
