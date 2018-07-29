import { Component, OnInit } from '@angular/core';

@Component({
    selector: 'jhi-footer',
    templateUrl: './footer.component.html',
    styleUrls: ['footer.css']
})
export class FooterComponent implements OnInit {
    baseUrl: String;

    ngOnInit(): void {
        this.baseUrl = 'www.netflixdb.pl';
    }
}
