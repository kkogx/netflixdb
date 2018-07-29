export interface IVideo {
    id?: number;
    title?: string;
    fwebTitle?: string;
    releaseYear?: number;
    genre?: string;
    genreId?: number;
    original?: boolean;
    type?: string;
    omdbAvailable?: boolean;
    fwebAvailable?: boolean;
    imdbRating?: number;
    fwebRating?: number;
    imdbVotes?: number;
    fwebVotes?: number;
    metascore?: number;
    tomatoRating?: number;
    tomatoUserRating?: number;
    imdbID?: string;
    fwebID?: number;
}

export class Video implements IVideo {
    constructor(
        public id?: number,
        public title?: string,
        public fwebTitle?: string,
        public releaseYear?: number,
        public genre?: string,
        public genreId?: number,
        public original?: boolean,
        public type?: string,
        public omdbAvailable?: boolean,
        public fwebAvailable?: boolean,
        public imdbRating?: number,
        public fwebRating?: number,
        public imdbVotes?: number,
        public fwebVotes?: number,
        public metascore?: number,
        public tomatoRating?: number,
        public tomatoUserRating?: number,
        public imdbID?: string,
        public fwebID?: number
    ) {
        this.original = false;
        this.omdbAvailable = false;
        this.fwebAvailable = false;
    }
}