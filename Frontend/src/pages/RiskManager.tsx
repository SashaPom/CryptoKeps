import React, { useEffect, useMemo, useState, useRef, useCallback } from "react";
import { TwApi } from "../api/tw";
import type { TwCreatePayload, TwProfile } from "../types/tw";
import ProfileCard from "../components/ProfileCard";


export default function RiskManager() {
    const [profiles, setProfiles] = useState<TwProfile[]>([]);
    const [error, setError] = useState<string | null>(null);
    const [creating, setCreating] = useState(false);

    const okxRef = useRef<WebSocket | null>(null);
    const binRef = useRef<WebSocket | null>(null);

    function safeSend(ws: WebSocket | null | undefined, msg: string) {
        if (ws && ws.readyState === WebSocket.OPEN) ws.send(msg);
    }

    const handleWsMessage = useCallback((ev: MessageEvent) => {
        try {
            console.log(ev.data)
            const data = JSON.parse(ev.data as string);
            if (Array.isArray(data)) {
                setProfiles((prev) =>
                    prev.map((p) => {
                        const upd = (data as any[]).find((row: any[]) => row && row[0] === p.id);
                        if (!upd) return p;
                        const [_id, balance, max_balance, available_balance, pnl, is_blocked, is_trade_now, lose_streak] = upd;
                        return {
                            ...p,
                            balance: Number(balance ?? p.balance ?? 0),
                            max_balance: Number(max_balance ?? p.max_balance ?? 0),
                            available_balance: Number(available_balance ?? p.available_balance ?? 0),
                            pnl: Number(pnl ?? p.pnl ?? 0),
                            blocked: Boolean(is_blocked),
                            trade_now: Boolean(is_trade_now),
                            lose_streak: Number(lose_streak ?? p.lose_streak ?? 0),
                        };
                    })
                );
            } else if (data && (data as any).profiles) {
                setProfiles((data as any).profiles);
            }
        } catch {}
    }, []);

    useEffect(() => {
        refresh();

        const wsProto = location.protocol === "https:" ? "wss" : "ws";
        const okxUrl  = `${wsProto}://${location.host}/ws/okx/`;
        const binUrl  = `${wsProto}://${location.host}/ws/binance/`;

        const okx = new WebSocket(okxUrl) as WebSocket;
        const bin = new WebSocket(binUrl) as WebSocket;

        okxRef.current = okx;
        binRef.current = bin;

        const subscribeAll = (sock: WebSocket, list: TwProfile[]) => {
            const okxIds = list
                .filter((p) => (p.exchange === "OKX" || p.exchange === 1) && p.status === "running")
                .map((p) => p.id);
            const binIds = list
                .filter((p) => (p.exchange === "BINANCE" || p.exchange === 0) && p.status === "running")
                .map((p) => p.id);
            if (sock === okx && okxIds.length) safeSend(okx, okxIds.join("."));
            if (sock === bin && binIds.length) safeSend(bin, binIds.join("."));
        };

        const onOkxOpen = () => subscribeAll(okx, profiles);
        const onBinOpen = () => subscribeAll(bin, profiles);

        okx.addEventListener("open", onOkxOpen);
        bin.addEventListener("open", onBinOpen);

        okx.addEventListener("message", handleWsMessage as unknown as EventListener);
        bin.addEventListener("message", handleWsMessage as unknown as EventListener);

        return () => {
            try {
                okx.removeEventListener("open", onOkxOpen);
                okx.removeEventListener("message", handleWsMessage as unknown as EventListener);
                okx.close();
            } catch {}
            try {
                bin.removeEventListener("open", onBinOpen);
                bin.removeEventListener("message", handleWsMessage as unknown as EventListener);
                bin.close();
            } catch {}
            okxRef.current = null;
            binRef.current = null;
        };
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

// 2. Отдельный useEffect, который реагирует на обновление profiles
    useEffect(() => {
        if (binRef.current?.readyState === WebSocket.OPEN) {
            const binIds = profiles
                .filter((p) => (p.exchange === "BINANCE" || p.exchange === 0) && p.status === "running")
                .map((p) => p.id);
            if (binIds.length) safeSend(binRef.current, binIds.join("."));
        }
        if (okxRef.current?.readyState === WebSocket.OPEN) {
            const okxIds = profiles
                .filter((p) => (p.exchange === "OKX" || p.exchange === 1) && p.status === "running")
                .map((p) => p.id);
            if (okxIds.length) safeSend(okxRef.current, okxIds.join("."));
        }
    }, [profiles]);

    function refresh() {
        setError(null);
        TwApi.listProfiles().then(setProfiles).catch((err) => setError(err.message || String(err)));
    }

    function start(id: number) {
        TwApi.start(id)
            .then(() => {
                refresh();
                safeSend(okxRef.current, "update");
                safeSend(binRef.current, "update");
            })
            .catch((e) => setError(String(e)));
    }

    function stop(id: number) {
        TwApi.stop(id)
            .then(() => {
                refresh();
                safeSend(okxRef.current, "update");
                safeSend(binRef.current, "update");
            })
            .catch((e) => setError(String(e)));
    }

    return (
        <div className="p-6 space-y-4">
            <div className="flex items-center justify-between">
                <h1 className="text-2xl font-semibold">Risk Manager</h1>
                <button
                    onClick={() => setCreating(true)}
                    className="px-3 py-2 rounded-xl bg-black text-white hover:opacity-90"
                >
                    + Create profile
                </button>
            </div>

            {error && <div className="text-red-500">{error}</div>}

            <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
                {profiles.map((p) => (
                    <ProfileCard key={p.id} p={p} onStart={start} onStop={stop} />
                ))}
            </div>

            {profiles.length === 0 && !error && <div className="text-gray-500">Нет профилей</div>}

            {creating && (
                <CreateProfileModal
                    onClose={() => setCreating(false)}
                    onCreated={() => {
                        setCreating(false);
                        refresh();
                    }}
                    onError={(m) => setError(m)}
                />
            )}
        </div>
    );
}

function CreateProfileModal({
                                onClose,
                                onCreated,
                                onError,
                            }: {
    onClose: () => void;
    onCreated: () => void;
    onError: (msg: string) => void;
}) {
    const [form, setForm] = useState<TwCreatePayload>({
        name: "",
        exchange: "OKX",
        api_key: "",
        secret_key: "",
        passphrase: "",
        refresh_time: "00:30",
        lose_percent_limit: null,
        lose_streak_limit: null,
        lose_amount_limit: null,
    });
    const [submitting, setSubmitting] = useState(false);

    function update<K extends keyof TwCreatePayload>(k: K, v: TwCreatePayload[K]) {
        setForm((s) => ({ ...s, [k]: v }));
    }

    async function submit() {
        if (!form.name?.trim()) return onError("Укажите Name");
        if (!form.api_key?.trim()) return onError("Укажите API key");
        if (!form.secret_key?.trim()) return onError("Укажите Secret key");

        setSubmitting(true);
        try {
            await TwApi.create(form);
            onCreated();
        } catch (e: any) {
            onError(e?.message ?? String(e));
        } finally {
            setSubmitting(false);
        }
    }

    return (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
            <div className="w-full max-w-xl rounded-2xl bg-white p-5 space-y-4">
                <div className="flex items-center justify-between">
                    <h2 className="text-lg font-semibold">Create TW profile</h2>
                    <button onClick={onClose} className="px-2 py-1 rounded hover:bg-gray-100">
                        ✕
                    </button>
                </div>

                <div className="grid grid-cols-2 gap-4">
                    <L label="Name">
                        <input
                            className="input"
                            value={form.name}
                            onChange={(e) => update("name", e.target.value)}
                            placeholder="например profile-1"
                        />
                    </L>

                    <L label="Exchange">
                        <select
                            className="input"
                            value={form.exchange}
                            onChange={(e) => update("exchange", e.target.value as "OKX" | "BINANCE")}
                        >
                            <option value="OKX">OKX</option>
                            <option value="BINANCE">Binance</option>
                        </select>
                    </L>

                    <L label="API key">
                        <input
                            className="input"
                            value={form.api_key}
                            onChange={(e) => update("api_key", e.target.value)}
                            placeholder="например my-api-key"
                        />
                    </L>

                    <L label="Secret key">
                        <input className="input" value={form.secret_key} onChange={(e) => update("secret_key", e.target.value)} />
                    </L>

                    <L label="Passphrase (если требуется)">
                        <input
                            className="input"
                            value={form.passphrase ?? ""}
                            onChange={(e) => update("passphrase", e.target.value)}
                            placeholder="для OKX"
                        />
                    </L>

                    <L label="Refresh time (HH:mm)">
                        <input
                            className="input"
                            value={form.refresh_time ?? ""}
                            onChange={(e) => update("refresh_time", e.target.value)}
                            placeholder="00:30"
                        />
                    </L>

                    <L label="Lose % limit">
                        <input
                            type="number"
                            className="input"
                            value={form.lose_percent_limit ?? ""}
                            onChange={(e) =>
                                update("lose_percent_limit", e.target.value === "" ? null : Number(e.target.value))
                            }
                            placeholder="например 5"
                        />
                    </L>

                    <L label="Lose streak limit">
                        <input
                            type="number"
                            className="input"
                            value={form.lose_streak_limit ?? ""}
                            onChange={(e) =>
                                update("lose_streak_limit", e.target.value === "" ? null : Number(e.target.value))
                            }
                            placeholder="например 3"
                        />
                    </L>

                    <L label="Lose amount limit">
                        <input
                            type="number"
                            className="input"
                            value={form.lose_amount_limit ?? ""}
                            onChange={(e) =>
                                update("lose_amount_limit", e.target.value === "" ? null : Number(e.target.value))
                            }
                            placeholder="например 50"
                        />
                    </L>
                </div>

                <div className="flex justify-end gap-2">
                    <button className="px-3 py-2 rounded-xl border" onClick={onClose} disabled={submitting}>
                        Cancel
                    </button>
                    <button
                        className="px-3 py-2 rounded-xl bg-black text-white disabled:opacity-60"
                        onClick={submit}
                        disabled={submitting}
                    >
                        {submitting ? "Creating..." : "Create"}
                    </button>
                </div>
            </div>

            <style>{`.input{ @apply w-full px-3 py-2 rounded-xl border outline-none focus:ring focus:border-black/50; }`}</style>
        </div>
    );
}

function L({ label, children }: { label: string; children: React.ReactNode }) {
    return (
        <label className="space-y-1">
            <div className="text-xs text-gray-600">{label}</div>
            {children}
        </label>
    );
}
