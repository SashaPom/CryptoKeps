import { fetchJson } from "./client";
import type { BotCard, TradeRow } from "../types/bots";

export async function listBots(params?: { q?: string; status?: string; period?: "24h"|"7d"|"30d" }): Promise<BotCard[]> {
    const qp = new URLSearchParams();
    if (params?.q) qp.set("q", params.q);
    if (params?.status && params.status !== "ALL") qp.set("status", params.status);
    if (params?.period) qp.set("period", params.period);
    return fetchJson<BotCard[]>(`/api/bots?${qp.toString()}`);
}

export async function recentTrades(botId: string, period: "24h"|"7d"|"30d"): Promise<TradeRow[]> {
    const qp = new URLSearchParams({ period });
    return fetchJson<TradeRow[]>(`/api/bots/${encodeURIComponent(botId)}/trades?${qp.toString()}`);
}

export async function sendTestSignal(botId: string, payload: {
    symbol: string;
    side: "BUY"|"SELL";
    orderType: "MARKET"|"LIMIT";
    quantity: number;
    price?: number;
    takeProfit?: number;
    stopLoss?: number;
    atr?: number;
}) {
    return fetchJson<{ ok: true }>(`/api/bots/${encodeURIComponent(botId)}/test-signal`, {
        method: "POST",
        body: JSON.stringify(payload),
    });
}
