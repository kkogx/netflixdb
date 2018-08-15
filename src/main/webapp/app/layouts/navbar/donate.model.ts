export class Przelewy24 {
    host: string;
    crc: string;
    merchantId: string;
}

export class Przelewy24Trx {
    amount: number;
    currency: string;
    description: string;
    email: string;
    country: string;
    urlReturn: string;
    transferLabel: string;
}
