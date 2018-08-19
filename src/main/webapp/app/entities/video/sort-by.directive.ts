import { Directive, Host, HostListener, Input, ElementRef, Renderer, AfterViewInit } from '@angular/core';
import { JhiConfigService, JhiSortDirective } from 'ng-jhipster';

@Directive({
    selector: '[ndbSortBy]'
})
export class NdbSortByDirective implements AfterViewInit {
    @Input() ndbSortBy: string;

    sortIcon = 'fa-sort';
    sortAscIcon = 'fa-sort-up';
    sortDescIcon = 'fa-sort-down';

    jhiSort: JhiSortDirective;

    constructor(@Host() jhiSort: JhiSortDirective, private el: ElementRef, private renderer: Renderer, configService: JhiConfigService) {
        this.jhiSort = jhiSort;
        const config = configService.getConfig();
        this.sortAscIcon = config.sortAscIcon;
        this.sortDescIcon = config.sortDescIcon;
    }

    ngAfterViewInit(): void {
        if (this.jhiSort.predicate && this.jhiSort.predicate !== '_score' && this.jhiSort.predicate === this.ndbSortBy) {
            this.applyClass();
        }
    }

    @HostListener('click')
    onClick() {
        if (this.jhiSort.predicate && this.jhiSort.predicate !== '_score') {
            this.jhiSort.sort(this.ndbSortBy);
            this.applyClass();
        }
    }

    private applyClass() {
        const childSpan = this.el.nativeElement.children[0].children[2];
        let add;
        if (this.jhiSort.ascending) {
            add = this.sortAscIcon;
        } else {
            add = this.sortDescIcon;
        }
        this.renderer.setElementClass(childSpan, this.sortIcon, false);
        this.renderer.setElementClass(childSpan, add, true);
    }
}
