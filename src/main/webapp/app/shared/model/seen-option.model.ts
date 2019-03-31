export enum SeenOptionId {
    YES = 'YES',
    NO = 'NO',
    ANY = 'ANY'
}

export interface ISeenOption {
    id?: SeenOptionId;
    name?: string;
}

export class SeenOption implements ISeenOption {
    constructor(public id?: SeenOptionId, public name?: string) {
        this.id = id;
        this.name = name;
    }
}
