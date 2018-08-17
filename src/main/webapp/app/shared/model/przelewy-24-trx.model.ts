import { Moment } from 'moment';

export interface IPrzelewy24Trx {
    id?: number;
    createdDate?: Moment;
    confirmedDate?: Moment;
    email?: string;
    sessionId?: string;
    description?: string;
    currency?: string;
    country?: string;
    amount?: number;
}

export class Przelewy24Trx implements IPrzelewy24Trx {
    constructor(
        public id?: number,
        public createdDate?: Moment,
        public confirmedDate?: Moment,
        public email?: string,
        public sessionId?: string,
        public description?: string,
        public currency?: string,
        public country?: string,
        public amount?: number
    ) {}
}
