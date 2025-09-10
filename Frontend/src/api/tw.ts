import { api } from "./client";
import type { TwCreatePayload, TwProfile } from "../types/tw";

function normNum(x: any, d = 0) { const n = Number(x); return Number.isFinite(n) ? n : d; }
function normBool(x: any, d = false) { return typeof x === "boolean" ? x : d; }
function normStr(x: any, d = "") { return typeof x === "string" ? x : d; }

function mapProfile(raw: any): TwProfile {
    const ex = raw.exchange;
    const exchange = typeof ex === "number" ? (ex === 1 ? "OKX" : "BINANCE") : (String(ex).toUpperCase() === "OKX" ? "OKX" : "BINANCE");
    const status: "running" | "stopped" = (raw.server || raw.server_id) ? "running" : "stopped";
    return {
        id: Number(raw.id),
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
        created_at: raw.created_at ?? null,
    };
}

export const TwApi = {
    listProfiles: () =>
        api<any[]>("/api/py/dj/tw/api/watcher/profiles/?all=1").then((rows) => rows.map(mapProfile)),

    create: (payload: TwCreatePayload) =>
        api<{ id: number }>("/api/py/dj/tw/api/profile/create", {
            method: "POST",
            body: JSON.stringify(payload),
        }),

    start: (id: number) =>
        api<{ ok: true }>("/api/py/dj/tw/api/profile/start", {
            method: "POST",
            body: JSON.stringify({ id }),
        }),

    stop: (id: number) =>
        api<{ ok: true }>("/api/py/dj/tw/api/profile/stop", {
            method: "POST",
            body: JSON.stringify({ id }),
        }),
};
