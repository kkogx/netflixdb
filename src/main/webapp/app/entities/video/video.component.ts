import { Component, OnDestroy, OnInit } from '@angular/core';
import { HttpErrorResponse, HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { JhiAlertService, JhiEventManager, JhiParseLinks } from 'ng-jhipster';

import { IVideo } from 'app/shared/model/video.model';
import { Principal } from 'app/core';

import { ITEMS_PER_PAGE } from 'app/shared';
import { VideoService } from './video.service';
import { Observable } from 'rxjs/Observable';

@Component({
    selector: 'jhi-video',
    templateUrl: './video.component.html'
})
export class VideoComponent implements OnInit, OnDestroy {
    runtimeSortAttrs: Array<String> = ['title', 'fwebTitle', 'genre'];

    videos: IVideo[];
    currentAccount: any;
    eventSubscriber: Subscription;
    itemsPerPage: number;
    links: any;
    page: any;
    predicate: any;
    reverse: any;
    totalItems: number;
    currentSearch: string;
    imdbVoteMin: number;
    imdbVoteMax: number;
    fwebVoteMin: number;
    fwebVoteMax: number;

    genres: Observable<any[]>;
    selectedGenres = [];

    constructor(
        private videoService: VideoService,
        private jhiAlertService: JhiAlertService,
        private eventManager: JhiEventManager,
        private parseLinks: JhiParseLinks,
        private activatedRoute: ActivatedRoute,
        private principal: Principal
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
    }

    loadAll() {
        if (this.currentSearch) {
            this.videoService
                .search({
                    query: this.currentSearch,
                    fwebMin: this.fwebVoteMin ? this.fwebVoteMin : 0,
                    fwebMax: this.fwebVoteMax ? this.fwebVoteMax : -1,
                    imdbMin: this.imdbVoteMin ? this.imdbVoteMin : 0,
                    imdbMax: this.imdbVoteMax ? this.imdbVoteMax : -1,
                    page: this.page,
                    size: this.itemsPerPage,
                    sort: this.sort()
                })
                .subscribe(
                    (res: HttpResponse<IVideo[]>) => this.paginateVideos(res.body, res.headers),
                    (res: HttpErrorResponse) => this.onError(res.message)
                );
            return;
        }
        this.videoService
            .query({
                page: this.page,
                size: this.itemsPerPage,
                sort: this.sort()
            })
            .subscribe(
                (res: HttpResponse<IVideo[]>) => this.paginateVideos(res.body, res.headers),
                (res: HttpErrorResponse) => this.onError(res.message)
            );
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

    private ensureRange(value: number): number {
        value = Math.max(0, value);
        value = Math.min(10000000, value);
        if (value === 0) {
            value = undefined;
        }
        return value;
    }

    private ensureRanges() {
        this.fwebVoteMin = this.ensureRange(this.fwebVoteMin);
        this.fwebVoteMax = this.ensureRange(this.fwebVoteMax);
        this.imdbVoteMin = this.ensureRange(this.imdbVoteMin);
        this.imdbVoteMax = this.ensureRange(this.imdbVoteMax);
    }

    // called whenever min search param is changed
    public searchBlurMin(query) {
        this.ensureRanges();
        if (this.fwebVoteMax <= this.fwebVoteMin) {
            this.fwebVoteMax = undefined;
        }
        if (this.imdbVoteMax <= this.imdbVoteMin) {
            this.imdbVoteMax = undefined;
        }
        this.search(query);
    }

    public searchBlurMax(query) {
        this.ensureRanges();
        if (this.fwebVoteMin >= this.fwebVoteMax) {
            this.fwebVoteMin = 0;
        }
        if (this.imdbVoteMin >= this.imdbVoteMax) {
            this.imdbVoteMin = 0;
        }
        this.search(query);
    }

    search(query) {
        if (!query) {
            return this.clear();
        }
        this.videos = [];
        this.links = {
            last: 0
        };
        this.page = 0;
        this.predicate = null; // '_score';
        this.reverse = false;
        this.currentSearch = query;
        this.loadAll();
    }

    ngOnInit() {
        this.genres = this.videoService.genres();

        this.loadAll();
        this.principal.identity().then(account => {
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

    private paginateVideos(data: IVideo[], headers: HttpHeaders) {
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
        for (let i = 0; i < data.length; i++) {
            data[i].fwebRating = Math.round(data[i].fwebRating * 10) / 10;
            this.videos.push(data[i]);
        }
    }

    private onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }
}
