import React, {useEffect, useMemo, useState, useRef, useCallback} from "react";
import {TwApi} from "../api/tw";
import type {TwCreatePayload, TwProfile} from "../types/tw";
import ProfileCard from "../components/ProfileCard";

export default function RiskManager() {
    const [profiles, setProfiles] = useState<TwProfile[]>([]);
    const [error, setError] = useState<string | null>(null);
    const [creating, setCreating] = useState(false);
    const [editing, setEditing] = useState<TwProfile | null>(null);
    const [deleting, setDeleting] = useState<TwProfile | null>(null);

    const okxRef = useRef<WebSocket | null>(null);
    const binRef = useRef<WebSocket | null>(null);

    const wsBase = useMemo(() => {
        const proto = location.protocol === "https:" ? "wss" : "ws";
        return `${proto}://${location.host}`;
    }, []);

    const okxUrl = `${wsBase}/ws/okx/`;
    const binUrl = `${wsBase}/ws/binance/`;

    function safeSend(ws: WebSocket | null | undefined, msg: string) {
        if (ws && ws.readyState === WebSocket.OPEN) ws.send(msg);
    }

    const okxSig = useMemo(
        () =>
            profiles
                .filter(p => (p.exchange === "OKX" || p.exchange === 1) && p.status === "running")
                .map(p => p.id)
                .sort((a, b) => a - b)
                .join("."),
        [profiles]
    );
    const binSig = useMemo(
        () =>
            profiles
                .filter(p => (p.exchange === "BINANCE" || p.exchange === 0) && p.status === "running")
                .map(p => p.id)
                .sort((a, b) => a - b)
                .join("."),
        [profiles]
    );

    const pending = useRef<Map<number, any[]>>(new Map());
    const flushTimer = useRef<number | null>(null);

    const handleWsMessage = useCallback((ev: MessageEvent) => {
        try {
            const data = JSON.parse(ev.data as string);
            if (!Array.isArray(data)) return;

            for (const row of data as any[]) pending.current.set(row[0], row);

            if (flushTimer.current == null) {
                flushTimer.current = window.setTimeout(() => {
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
                    pending.current.clear();
                    flushTimer.current = null;
                }, 300);
            }
        } catch {
        }
    }, []);

    useEffect(() => {
        refresh();

        const okx = new WebSocket(okxUrl) as WebSocket;
        const bin = new WebSocket(binUrl) as WebSocket;

        okxRef.current = okx;
        binRef.current = bin;

        okx.addEventListener("message", handleWsMessage as unknown as EventListener);
        bin.addEventListener("message", handleWsMessage as unknown as EventListener);

        return () => {
            try {
                okx.removeEventListener("message", handleWsMessage as unknown as EventListener);
                okx.close();
            } catch {}
            try {
                bin.removeEventListener("message", handleWsMessage as unknown as EventListener);
                bin.close();
            } catch {}
            okxRef.current = null;
            binRef.current = null;
        };
    }, [okxUrl, binUrl, handleWsMessage]);

    useEffect(() => {
        if (okxRef.current?.readyState === WebSocket.OPEN && okxSig) safeSend(okxRef.current, okxSig);
    }, [okxSig]);
    useEffect(() => {
        if (binRef.current?.readyState === WebSocket.OPEN && binSig) safeSend(binRef.current, binSig);
    }, [binSig]);

    function refresh() {
        setError(null);
        TwApi.listProfiles().then(setProfiles).catch((err) => setError(err.message || String(err)));
    }

    function update(id: number, payload: Partial<TwCreatePayload>) {
        TwApi.update(id, payload)
            .then(() => refresh())
            .catch((e) => setError(String(e)));
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
                    <ProfileCard
                        key={p.id}
                        p={p}
                        onStart={start}
                        onStop={stop}
                        onEdit={(prof) => setEditing(prof)}
                        onDelete={(prof) => setDeleting(prof)}
                    />
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

            {editing && (
                <EditProfileModal
                    profile={editing}
                    onClose={() => setEditing(null)}
                    onSaved={() => {
                        setEditing(null);
                        refresh();
                    }}
                    onError={(m) => setError(m)}
                />
            )}

            {deleting && (
                <DeleteProfileModal
                    profile={deleting}
                    onClose={() => setDeleting(null)}
                    onDeleted={() => {
                        setDeleting(null);
                        refresh();
                    }}
                    onError={(m) => setError(m)}
                />
            )}
        </div>
    );
}

/* ---------- CREATE ---------- */
function CreateProfileModal({ onClose, onCreated, onError }: {
    onClose: () => void; onCreated: () => void; onError: (msg: string) => void;
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
        <Modal title="Create TW profile" onClose={onClose}>
            <ProfileForm form={form} update={update} />
            <div className="flex justify-end gap-2">
                <button className="px-3 py-2 rounded-xl border" onClick={onClose} disabled={submitting}>Cancel</button>
                <button
                    className="px-3 py-2 rounded-xl bg-black text-white disabled:opacity-60"
                    onClick={submit}
                    disabled={submitting}
                >
                    {submitting ? "Creating..." : "Create"}
                </button>
            </div>
        </Modal>
    );
}

/* ---------- EDIT ---------- */
function EditProfileModal({ profile, onClose, onSaved, onError }: {
    profile: TwProfile; onClose: () => void; onSaved: () => void; onError: (msg: string) => void;
}) {
    const [form, setForm] = useState<Partial<TwCreatePayload>>({ ...profile } as Partial<TwCreatePayload>);
    const [submitting, setSubmitting] = useState(false);

    function update<K extends keyof TwCreatePayload>(k: K, v: TwCreatePayload[K]) {
        setForm((s) => ({ ...s, [k]: v }));
    }

    async function submit() {
        setSubmitting(true);
        try {
            await TwApi.update(profile.id, form);
            onSaved();
        } catch (e: any) {
            onError(e?.message ?? String(e));
        } finally {
            setSubmitting(false);
        }
    }

    return (
        <Modal title={`Edit ${profile.name}`} onClose={onClose}>
            <ProfileForm form={form} update={update} />
            <div className="flex justify-end gap-2">
                <button className="px-3 py-2 rounded-xl border" onClick={onClose} disabled={submitting}>Cancel</button>
                <button
                    className="px-3 py-2 rounded-xl bg-black text-white disabled:opacity-60"
                    onClick={submit}
                    disabled={submitting}
                >
                    {submitting ? "Saving..." : "Save"}
                </button>
            </div>
        </Modal>
    );
}

/* ---------- DELETE ---------- */
function DeleteProfileModal({ profile, onClose, onDeleted, onError }: {
    profile: TwProfile; onClose: () => void; onDeleted: () => void; onError: (msg: string) => void;
}) {
    const [submitting, setSubmitting] = useState(false);

    async function submit() {
        setSubmitting(true);
        try {
            await TwApi.delete(profile.id);
            onDeleted();
        } catch (e: any) {
            onError(e?.message ?? String(e));
        } finally {
            setSubmitting(false);
        }
    }

    return (
        <Modal title="Delete profile" onClose={onClose}>
            <p>Вы уверены, что хотите удалить профиль <b>{profile.name}</b>?</p>
            <div className="flex justify-end gap-2 mt-4">
                <button className="px-3 py-2 rounded-xl border" onClick={onClose} disabled={submitting}>Cancel</button>
                <button
                    className="px-3 py-2 rounded-xl bg-red-600 text-white disabled:opacity-60"
                    onClick={submit}
                    disabled={submitting}
                >
                    {submitting ? "Deleting..." : "Delete"}
                </button>
            </div>
        </Modal>
    );
}

/* ---------- SHARED FORM ---------- */
function ProfileForm({ form, update }: { form: any; update: (k: keyof TwCreatePayload, v: any) => void }) {
    return (
        <div className="grid grid-cols-2 gap-4">
            <L label="Name"><input className="input" value={form.name ?? ""} onChange={(e) => update("name", e.target.value)} /></L>
            <L label="Exchange">
                <select className="input" value={form.exchange ?? "OKX"} onChange={(e) => update("exchange", e.target.value as any)}>
                    <option value="OKX">OKX</option>
                    <option value="BINANCE">Binance</option>
                </select>
            </L>
            <L label="API key"><input className="input" value={form.api_key ?? ""} onChange={(e) => update("api_key", e.target.value)} /></L>
            <L label="Secret key"><input className="input" value={form.secret_key ?? ""} onChange={(e) => update("secret_key", e.target.value)} /></L>
            <L label="Passphrase"><input className="input" value={form.passphrase ?? ""} onChange={(e) => update("passphrase", e.target.value)} /></L>
            <L label="Refresh time"><input className="input" value={form.refresh_time ?? ""} onChange={(e) => update("refresh_time", e.target.value)} /></L>
            <L label="Lose % limit"><input type="number" className="input" value={form.lose_percent_limit ?? ""} onChange={(e) => update("lose_percent_limit", e.target.value === "" ? null : Number(e.target.value))} /></L>
            <L label="Lose streak limit"><input type="number" className="input" value={form.lose_streak_limit ?? ""} onChange={(e) => update("lose_streak_limit", e.target.value === "" ? null : Number(e.target.value))} /></L>
            <L label="Lose amount limit"><input type="number" className="input" value={form.lose_amount_limit ?? ""} onChange={(e) => update("lose_amount_limit", e.target.value === "" ? null : Number(e.target.value))} /></L>
        </div>
    );
}

/* ---------- REUSABLE MODAL ---------- */
function Modal({ title, onClose, children }: { title: string; onClose: () => void; children: React.ReactNode }) {
    return (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
            <div className="w-full max-w-xl rounded-2xl bg-white p-5 space-y-4">
                <div className="flex items-center justify-between">
                    <h2 className="text-lg font-semibold">{title}</h2>
                    <button onClick={onClose} className="px-2 py-1 rounded hover:bg-gray-100">✕</button>
                </div>
                {children}
            </div>
            <style>{`.input{ @apply w-full px-3 py-2 rounded-xl border outline-none focus:ring focus:border-black/50; }`}</style>
        </div>
    );
}

/* ---------- LABEL ---------- */
function L({label, children}: { label: string; children: React.ReactNode }) {
    return (
        <label className="space-y-1">
            <div className="text-xs text-gray-600">{label}</div>
            {children}
        </label>
    );
}
