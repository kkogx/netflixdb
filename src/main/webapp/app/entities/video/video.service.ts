import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IVideo } from 'app/shared/model/video.model';
import { IGenre } from 'app/shared/model/genre.model';

type EntityResponseType = HttpResponse<IVideo>;
type EntityArrayResponseType = HttpResponse<IVideo[]>;

@Injectable({ providedIn: 'root' })
export class VideoService {
    private resourceUrl = SERVER_API_URL + 'api/videos';
    private resourceSearchUrl = SERVER_API_URL + 'api/_search/videos/range';
    private resourceGenresUrl = SERVER_API_URL + 'api/videos/genres';

    constructor(protected http: HttpClient) {}

    create(video: IVideo): Observable<EntityResponseType> {
        return this.http.post<IVideo>(this.resourceUrl, video, { observe: 'response' });
    }

    update(video: IVideo): Observable<EntityResponseType> {
        return this.http.put<IVideo>(this.resourceUrl, video, { observe: 'response' });
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<IVideo>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IVideo[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    search(req?: any, type?: string): Observable<EntityArrayResponseType> {
        let url = this.resourceSearchUrl;
        if (type) {
            url += '/' + type;
        }
        const options = createRequestOption(req);
        return this.http.get<IVideo[]>(url, { params: options, observe: 'response' });
    }

    genres(type?: string): Observable<IGenre[]> {
        let url = this.resourceGenresUrl;
        if (type) {
            url += '/' + type;
        }
        const result = this.http.get<IGenre[]>(url);
        return result;
    }
}
