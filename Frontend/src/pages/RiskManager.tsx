import React, { useEffect, useMemo, useState } from "react";
import { TwApi } from "../api/tw";
import type { TwCreatePayload, TwProfile } from "../types/tw";
import ProfileCard from "../components/ProfileCard";

/** Включи DEMO_MODE=true если нужен локальный демо-рендер без бэка */
const DEMO_MODE = false;

const demoProfiles: TwProfile[] = [
    { id: 101, name: "OKX Alpha", exchange: "OKX", status: "running",
        available_balance: 0, balance: 0, max_balance: 0, pnl: 0,
        lose_streak: 0, trade_now: false, blocked: false,
        refresh_time: "09:00", lose_amount_limit: 10, lose_percent_limit: 5,
        lose_streak_limit: 5, created_at: "2025-08-28" },
    { id: 102, name: "Binance Demo", exchange: "BINANCE", status: "stopped",
        available_balance: 0, balance: 0, max_balance: 0, pnl: 0,
        lose_streak: 0, trade_now: false, blocked: true,
        refresh_time: "09:00", lose_amount_limit: 10, lose_percent_limit: 5,
        lose_streak_limit: 5, created_at: "2025-08-28" },
];

export default function RiskManager() {
    const [profiles, setProfiles] = useState<TwProfile[]>([]);
    const [error, setError] = useState<string | null>(null);
    const [creating, setCreating] = useState(false);

    const wsOkxUrl = useMemo(() => `ws://${location.hostname}:8080/ws/okx`, []);
    const wsBinUrl = useMemo(() => `ws://${location.hostname}:8080/ws/binance`, []);


    useEffect(() => {
        if (DEMO_MODE) {
            setProfiles(demoProfiles);
            return;
        }
        refresh();

        // const t = setInterval(refresh, 10000); // обновляем каждые 10 сек
        // return () => clearInterval(t);

        // подписки на вотчеры (если присылают обновлённые профили)
        const okx = new WebSocket(wsOkxUrl);
        okx.onmessage = (e) => {
            try {
                const data = JSON.parse(e.data);
                if (data?.profiles) setProfiles(data.profiles);
            } catch {}
        };

        const bin = new WebSocket(wsBinUrl);
        bin.onmessage = (e) => {
            try {
                const data = JSON.parse(e.data);
                if (data?.profiles) setProfiles(data.profiles);
            } catch {}
        };

        return () => {
            okx.close();
            bin.close();
        };
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    function refresh() {
        setError(null);
        TwApi.listProfiles()
            .then(setProfiles)
            .catch((err) => setError(err.message || String(err)));
    }

    function start(id: number) {
        TwApi.start(id).then(refresh).catch((e) => setError(String(e)));
    }

    function stop(id: number) {
        TwApi.stop(id).then(refresh).catch((e) => setError(String(e)));
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

            {profiles.length === 0 && !error && (
                <div className="text-gray-500">Нет профилей</div>
            )}

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

/* ---------- Create modal (без изменений) ---------- */
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
                        <input
                            className="input"
                            value={form.secret_key}
                            onChange={(e) => update("secret_key", e.target.value)}
                        />
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
