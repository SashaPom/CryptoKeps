// src/api/tw.ts
import { api } from "./client";
import type { TwCreatePayload, TwProfile } from "../types/tw";

function normNum(x: any, d = 0) {
    const n = Number(x);
    return Number.isFinite(n) ? n : d;
}
function normBool(x: any, d = false) {
    return typeof x === "boolean" ? x : d;
}
function normStr(x: any, d = "") {
    return typeof x === "string" ? x : d;
}
function mapProfile(raw: any): TwProfile {
    // exchange может прийти как 0/1 — быстренько мапнем
    const ex = raw.exchange;
    const exchange =
        typeof ex === "number"
            ? ex === 0
                ? "BINANCE"
                : "OKX"
            : normStr(ex, "BINANCE");

    const status: "running" | "stopped" =
        raw.server || raw.is_started ? "running" : "stopped";

    return {
        id: raw.id,
        name: normStr(raw.name, ""),
        exchange,
        status,

        available_balance: normNum(raw.available_balance),
        balance: normNum(raw.balance),
        max_balance: normNum(raw.max_balance),
        pnl: normNum(raw.pnl),
        lose_streak: normNum(raw.lose_streak),

        trade_now: normBool(raw.trade_now),
        blocked: normBool(raw.blocked),

        refresh_time: normStr(raw.refresh_time, ""),
        lose_percent_limit: raw.lose_percent_limit ?? null,
        lose_amount_limit: raw.lose_amount_limit ?? null,
        lose_streak_limit: raw.lose_streak_limit ?? null,
        created_at: normStr(raw.created_at ?? raw.createdAt, null as any),
    };
}


export const TwApi = {
    listProfiles: () => api<TwProfile[]>("/api/py/dj/tw/api/watcher/profiles/?all=1"),

    create: (payload: TwCreatePayload) =>
        api<{ id: number }>("/py/dj/tw/create/", { method: "POST", body: JSON.stringify(payload) }),

    start: (id: number) =>
        api<{ ok: true }>("/py/dj/tw/start/", { method: "POST", body: JSON.stringify({ id }) }),

    stop: (id: number) =>
        api<{ ok: true }>("/py/dj/tw/stop/", { method: "POST", body: JSON.stringify({ id }) }),
};
