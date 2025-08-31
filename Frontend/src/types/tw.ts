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
    exchange: "BINANCE" | "OKX" | string;
    status: "running" | "stopped";

    // витрина балансов/профита
    available_balance?: number | null;
    balance?: number | null;
    max_balance?: number | null;
    pnl?: number | null;
    lose_streak?: number | null;
    trade_now?: boolean | null;   // true/false -> YES/NO
    blocked?: boolean | null;     // true -> BLOCKED

    // параметры вотчера (для INFO)
    refresh_time?: string | null;         // "HH:mm"
    lose_percent_limit?: number | null;
    lose_amount_limit?: number | null;
    lose_streak_limit?: number | null;
    created_at?: string | null;           // "YYYY-MM-DD" или "MM.DD.YYYY"
};