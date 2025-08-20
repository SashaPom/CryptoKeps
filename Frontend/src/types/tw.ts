export type TwCreatePayload = {
    name: string;
    exchange: "OKX" | "BINANCE";
    api_key: string;
    secret_key: string;
    passphrase?: string;
    refresh_time?: string;            // "HH:mm"
    lose_percent_limit?: number | null;
    lose_streak_limit?: number | null;
    lose_amount_limit?: number | null;
};

export type TwProfile = {
    id: number;
    name: string;
    status: "running" | "stopped" | string;
    exchange?: "OKX" | "BINANCE" | string;
};