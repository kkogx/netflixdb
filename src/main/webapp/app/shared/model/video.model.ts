export interface IVideo {
    id?: number;
    title?: string;
    fwebTitle?: string;
    releaseYear?: number;
    genre?: string;
    genres?: string;
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
    boxart?: string;
    timestamp?: Date;
    fwebPlot?: string;
}

export class Video implements IVideo {
    constructor(
        public id?: number,
        public title?: string,
        public fwebTitle?: string,
        public releaseYear?: number,
        public genre?: string,
        public genres?: string,
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
        public fwebID?: number,
        public boxart?: string,
        public timestamp?: Date,
        public fwebPlot?: string
    ) {
        this.original = this.original || false;
        this.omdbAvailable = this.omdbAvailable || false;
        this.fwebAvailable = this.fwebAvailable || false;
    }
}
