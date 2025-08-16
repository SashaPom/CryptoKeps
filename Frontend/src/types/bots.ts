export type BotStatus = "RUNNING" | "PAUSED" | "ERROR";

export type BotCard = {
    id: string;
    strategyName: string;
    exchange: "Bybit";
    accountKeyName: string;
    status: BotStatus;
    lastSync: string; // ISO
    kpis: {
        pnlAbs: number;
        pnlPct: number;
        trades: number;
        winRate: number;
        maxDrawdownPct: number;
        avgRR: number;
    };
    equitySeries: { t: string; equity: number }[];
};

export type TradeRow = {
    id: string;
    time: string;
    symbol: string;
    side: "BUY" | "SELL";
    qty: number;
    entry: number;
    exit?: number;
    pnl?: number;
    rr?: number;
    durationSec?: number;
};
