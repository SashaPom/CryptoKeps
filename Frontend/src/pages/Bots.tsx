import React, { useMemo, useState, useEffect } from "react";
import {
    Activity,
    Pause,
    Play,
    Settings,
    TrendingDown,
    TrendingUp,
    Send,
    Database
} from "lucide-react";

import type { BotStatus } from "../types/bots";
import { useBots, useTrades } from "../hooks/useBots";
import { sendTestSignal } from "../api/bots";

// --- Helpers for alerts & smoke tests ---
function formatAlert(payload: any) {
    return `Stub send to backend:\n${JSON.stringify(payload, null, 2)}`;
}

// --- Page ---
export default function Bots() {
    const [period, setPeriod] = useState<"24h" | "7d" | "30d">("24h");
    const [openBotId, setOpenBotId] = useState<string | null>(null);
    const [showTest, setShowTest] = useState(false);
    const [search, setSearch] = useState("");
    const [statusFilter, setStatusFilter] = useState<BotStatus | "ALL">("ALL");
    const [testBotId, setTestBotId] = useState<string | null>(null);

    const { data: bots, loading, error } = useBots({
        q: search,
        status: statusFilter,
        period
    });

    const filtered = bots ?? [];
    const openBot = filtered.find(b => b.id === openBotId) || null;
    const trades = useTrades(openBot?.id ?? null, period);

    useEffect(() => {
        console.assert(["24h", "7d", "30d"].includes(period), "Unknown period");
    }, [period]);

    return (
        <div className="min-h-screen bg-gray-50 dark:bg-black">
            {/* Toolbar */}
            <div className="max-w-6xl mx-auto px-4 py-4 flex items-center justify-between gap-3 flex-wrap">
                <div className="flex items-center gap-2">
                    <span className="text-sm text-gray-700 dark:text-gray-200">Period:</span>
                    <div className="inline-flex rounded-xl border dark:border-white/10 bg-white dark:bg-neutral-900 overflow-hidden">
                        {(["24h","7d","30d"] as const).map(p => (
                            <button key={p} onClick={() => setPeriod(p)} className={`px-3 py-1.5 text-sm ${period===p ? "bg-gray-900 text-white dark:bg-neon dark:text-black"
                                : "text-gray-700 hover:bg-gray-100 dark:text-gray-200 dark:hover:bg-white/10"?"bg-gray-900 text-white":"text-gray-700 hover:bg-gray-100"}`}>{p}</button>
                        ))}
                    </div>
                </div>
                <div className="flex items-center gap-2">
                    <input placeholder="Search strategy or key" value={search} onChange={e=>setSearch(e.target.value)} className="w-56 rounded-xl border dark:border-white/10 px-3 py-2 text-sm bg-white dark:bg-neutral-900 text-gray-900 dark:text-gray-100"/>
                    <select value={statusFilter} onChange={e=>setStatusFilter(e.target.value as any)} className="rounded-xl border dark:border-white/10 px-3 py-2 text-sm bg-white dark:bg-neutral-900 text-gray-900 dark:text-gray-100">
                        <option value="ALL">All statuses</option>
                        <option value="RUNNING">Running</option>
                        <option value="PAUSED">Paused</option>
                        <option value="ERROR">Error</option>
                    </select>
                </div>
                <button className="inline-flex items-center gap-2 px-3 py-2 rounded-xl bg-gray-900 text-white text-sm" onClick={() => setShowTest(true)}>
                    <Send className="w-4 h-4"/> Send Test Signal
                </button>
            </div>

            {/* =========================
     BEGIN: Bots grid section
   ========================= */}
            <main className="max-w-6xl mx-auto px-4 pb-12">
                {/* loading / error / empty */}
                {loading && (
                    <div className="text-sm text-gray-500 dark:text-gray-400 py-6">
                        Loading bots…
                    </div>
                )}
                {error && (
                    <div className="text-sm text-red-600 py-6">
                        {error}
                    </div>
                )}
                {!loading && !error && filtered.length === 0 && (
                    <div className="text-sm text-gray-500 dark:text-gray-400 py-6">
                        No bots found.
                    </div>
                )}

                {/* cards */}
                {!loading && !error && filtered.length > 0 && (
                    <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
                        {filtered.map(bot => (
                            <div key={bot.id} className="rounded-2xl bg-white dark:bg-neutral-900 shadow-sm border dark:border-white/10 p-4 flex flex-col gap-3">
                                <div className="flex items-start justify-between">
                                    <div>
                                        <div className="text-sm text-gray-500 dark:text-gray-400">{bot.exchange}</div>
                                        <h3 className="text-lg font-semibold leading-tight text-gray-900 dark:text-white">{bot.strategyName}</h3>
                                        <div className="text-xs text-gray-500 dark:text-gray-400 mt-0.5">
                                            Key: <span className="font-medium text-gray-900 dark:text-white">{bot.accountKeyName}</span>
                                        </div>
                                    </div>
                                    <div className="flex flex-col items-end gap-1">
                                        <StatusBadge status={bot.status}/>
                                        <div className="text-[11px] text-gray-400">
                                            last sync {new Date(bot.lastSync).toLocaleTimeString()}
                                        </div>
                                    </div>
                                </div>

                                <div className="flex items-center justify-between">
                                    <div className="flex gap-6">
                                        <Stat label="PnL" value={<PnLValue abs={bot.kpis.pnlAbs} pct={bot.kpis.pnlPct}/>} />
                                        <Stat label="Trades" value={bot.kpis.trades} />
                                        <Stat label="WinRate" value={`${bot.kpis.winRate.toFixed(1)}%`} />
                                    </div>
                                    <div className="flex gap-2">
                                        <IconButton title="Details" onClick={() => setOpenBotId(bot.id)}>
                                            <Activity className="w-4 h-4"/>
                                        </IconButton>
                                        <IconButton title="Settings" onClick={() => setOpenBotId(bot.id)}>
                                            <Settings className="w-4 h-4"/>
                                        </IconButton>

                                    </div>
                                </div>

                                <div className="flex items-center justify-between">
                                    <div className="flex gap-6">
                                        <Stat label="Max DD" value={`${bot.kpis.maxDrawdownPct.toFixed(1)}%`} />
                                        <Stat label="Avg R:R" value={bot.kpis.avgRR.toFixed(2)} />
                                    </div>
                                    {/* Если на бэке уже будет series — отрисуем спарклайн. Если нет — можно временно скрыть */}
                                    {bot.equitySeries && bot.equitySeries.length > 0 ? (
                                        <Sparkline data={bot.equitySeries.map(p=>p.equity)} />
                                    ) : (
                                        <div className="text-xs text-gray-400">no equity</div>
                                    )}
                                </div>

                                <div className="flex gap-2 pt-1">
                                    <button
                                        className="flex-1 px-3 py-2 rounded-xl border dark:border-white/10 text-sm hover:bg-gray-50 dark:hover:bg-white/10"
                                        onClick={() => setOpenBotId(bot.id)}
                                    >
                                        Details
                                    </button>
                                    <button
                                        className="px-3 py-2 rounded-xl bg-gray-900 text-white dark:bg-neon dark:text-black text-sm"
                                        onClick={() => { setTestBotId(bot.id); setShowTest(true); }}
                                    >
                                        <Send className="inline-block w-4 h-4 mr-1"/> Test Signal
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </main>
            {/* =======================
    END: Bots grid section
   ======================= */}


            {openBot && (
                <Modal onClose={()=>setOpenBotId(null)} title={`${openBot.strategyName} • ${openBot.accountKeyName}`}>
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                        <div className="md:col-span-2">
                            <section className="rounded-2xl border dark:border-white/10 bg-white dark:bg-neutral-900 p-4">
                                <h4 className="font-semibold mb-3">Overview</h4>
                                <div className="flex flex-wrap gap-6">
                                    <Stat label="PnL" value={<PnLValue abs={openBot.kpis.pnlAbs} pct={openBot.kpis.pnlPct}/>} />
                                    <Stat label="Trades" value={openBot.kpis.trades} />
                                    <Stat label="WinRate" value={`${openBot.kpis.winRate.toFixed(1)}%`} />
                                    <Stat label="Max DD" value={`${openBot.kpis.maxDrawdownPct.toFixed(1)}%`} />
                                    <Stat label="Avg R:R" value={openBot.kpis.avgRR.toFixed(2)} />
                                </div>
                                <div className="mt-4">
                                    <Sparkline data={openBot.equitySeries.map(p=>p.equity)} width={520} height={70} />
                                </div>
                            </section>

                            <section className="rounded-2xl border dark:border-white/10 bg-white dark:bg-neutral-900 p-4 mt-4">
                                <h4 className="font-semibold mb-3 text-gray-900 dark:text-white">Recent Trades</h4>

                                {/* states */}
                                {trades.loading && (
                                    <div className="text-sm text-gray-500 dark:text-gray-400">Loading trades…</div>
                                )}
                                {trades.error && (
                                    <div className="text-sm text-red-600">{trades.error}</div>
                                )}
                                {!trades.loading && !trades.error && (!trades.data || trades.data.length === 0) && (
                                    <div className="text-sm text-gray-500 dark:text-gray-400">No trades for the selected period.</div>
                                )}

                                {/* table */}
                                {trades.data && trades.data.length > 0 && (
                                    <div className="overflow-x-auto">
                                        <table className="min-w-full text-sm text-gray-900 dark:text-gray-100">
                                            <thead className="text-xs text-gray-500 dark:text-gray-400">
                                            <tr>
                                                <th className="text-left p-2">Time</th>
                                                <th className="text-left p-2">Symbol</th>
                                                <th className="text-left p-2">Side</th>
                                                <th className="text-right p-2">Qty</th>
                                                <th className="text-right p-2">Entry</th>
                                                <th className="text-right p-2">Exit</th>
                                                <th className="text-right p-2">PnL</th>
                                                <th className="text-right p-2">R:R</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            {trades.data.map(t => (
                                                <tr key={t.id} className="border-t dark:border-white/10">
                                                    <td className="p-2">{new Date(t.time).toLocaleString()}</td>
                                                    <td className="p-2">{t.symbol}</td>
                                                    <td className="p-2">
                                                        <span className={t.side === "BUY" ? "text-green-600" : "text-red-600"}>{t.side}</span>
                                                    </td>
                                                    <td className="p-2 text-right">{t.qty.toFixed(4)}</td>
                                                    <td className="p-2 text-right">{t.entry.toFixed(2)}</td>
                                                    <td className="p-2 text-right">{t.exit?.toFixed(2) ?? "-"}</td>
                                                    <td className={`p-2 text-right ${t.pnl! >= 0 ? "text-green-600" : "text-red-600"}`}>{t.pnl?.toFixed(2) ?? "-"}</td>
                                                    <td className="p-2 text-right">{t.rr?.toFixed(2) ?? "-"}</td>
                                                </tr>
                                            ))}
                                            </tbody>
                                        </table>
                                    </div>
                                )}
                            </section>
                        </div>

                        <div className="space-y-4">
                            <section className="rounded-2xl border bg-white p-4">
                                <h4 className="font-semibold mb-3">Status</h4>
                                <div className="flex items-center gap-2"><StatusBadge status={openBot.status}/><span className="text-xs text-gray-500">last sync {new Date(openBot.lastSync).toLocaleTimeString()}</span></div>
                                <div className="mt-3 flex gap-2">
                                    <button className="flex-1 px-3 py-2 rounded-xl border text-sm"><Pause className="inline w-4 h-4 mr-1"/> Pause</button>
                                    <button className="flex-1 px-3 py-2 rounded-xl border text-sm"><Play className="inline w-4 h-4 mr-1"/> Start</button>
                                </div>
                            </section>

                            <section className="rounded-2xl border dark:border-white/10 bg-white dark:bg-neutral-900 p-4">
                                <h4 className="font-semibold mb-3 text-gray-900 dark:text-white">Test Signal</h4>
                                <TestSignalForm
                                    onSubmit={async (payload) => {
                                        if (!openBot) return;
                                        try {
                                            await sendTestSignal(openBot.id, payload);
                                            alert("Test signal sent ✔");
                                        } catch (e: any) {
                                            alert(`Failed to send: ${e.message || e}`);
                                        }
                                    }}
                                    defaultSymbol="BTCUSDT"
                                />
                            </section>
                        </div>
                    </div>
                </Modal>
            )}

            {showTest && (
                <Modal onClose={() => setShowTest(false)} title="Send Test Signal">
                    {/* выбор бота перед отправкой */}
                    <div className="mb-3">
                        <label className="text-sm">
                            <div className="text-xs text-gray-500 dark:text-gray-400 mb-1">Bot</div>
                            <select
                                value={testBotId ?? ""}
                                onChange={(e) => setTestBotId(e.target.value || null)}
                                className="w-full rounded-xl border dark:border-white/10 px-3 py-2 text-sm bg-white dark:bg-neutral-900 text-gray-900 dark:text-gray-100"
                            >
                                <option value="">— select a bot —</option>
                                {(bots ?? []).map(b => (
                                    <option key={b.id} value={b.id}>
                                        {b.strategyName} • {b.accountKeyName}
                                    </option>
                                ))}
                            </select>
                        </label>
                    </div>

                    <TestSignalForm
                        onSubmit={async (payload) => {
                            try {
                                const target = testBotId ?? openBotId ?? (bots && bots[0]?.id);
                                if (!target) {
                                    alert("Select a bot first.");
                                    return;
                                }
                                await sendTestSignal(target, payload);
                                alert("Test signal sent ✔");
                            } catch (e: any) {
                                alert(`Failed to send: ${e.message || e}`);
                            }
                        }}
                        defaultSymbol="BTCUSDT"
                    />
                </Modal>
            )}
        </div>
    );
}

// --- Reusable UI parts ---
const IconButton: React.FC<{ title?: string; onClick?: () => void; children: React.ReactNode }> = ({ title, onClick, children }) => (
    <button title={title} onClick={onClick} className="inline-flex items-center justify-center rounded-lg border w-9 h-9 hover:bg-gray-50">
        {children}
    </button>
);

const StatusBadge: React.FC<{ status: BotStatus }> = ({ status }) => {
    const map: Record<BotStatus, { text: string; cls: string }> = {
        RUNNING: { text: "Running", cls: "bg-green-100 text-green-800" },
        PAUSED: { text: "Paused", cls: "bg-yellow-100 text-yellow-800" },
        ERROR: { text: "Error", cls: "bg-red-100 text-red-800" },
    };
    const s = map[status];
    return <span className={`px-2 py-0.5 rounded-full text-xs font-medium ${s.cls}`}>{s.text}</span>;
};

const Stat: React.FC<{ label: string; value: React.ReactNode }> = ({ label, value }) => (
    <div className="flex flex-col"><span className="text-xs text-gray-500">{label}</span><span className="text-sm font-semibold">{value}</span></div>
);

const PnLValue: React.FC<{ abs: number; pct: number }> = ({ abs, pct }) => {
    const up = abs >= 0;
    const Icon = up ? TrendingUp : TrendingDown;
    return (
        <span className={`inline-flex items-center gap-1 ${up ? "text-green-600" : "text-red-600"}`}>
      <Icon className="w-4 h-4"/>
            {abs.toFixed(2)} ({pct.toFixed(1)}%)
    </span>
    );
};

const Sparkline: React.FC<{ data: number[]; width?: number; height?: number }> = ({ data, width = 160, height = 40 }) => {
    const min = Math.min(...data);
    const max = Math.max(...data);
    const points = data.map((v, i) => {
        const x = (i / (data.length - 1)) * width;
        const y = height - ((v - min) / (max - min || 1)) * height;
        return `${x},${y}`;
    }).join(" ");
    const up = data[data.length - 1] >= data[0];
    return (
        <svg width={width} height={height} className="overflow-visible">
             <polyline points={points} fill="none" stroke={up ? "#16a34a" : "#dc2626"} strokeWidth={2} />
        </svg>
    );
};

const Modal: React.FC<{ title: string; onClose: () => void; children: React.ReactNode }> = ({ title, onClose, children }) => (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
        <div className="absolute inset-0 bg-black/30" onClick={onClose} />
        <div className="relative bg-white dark:bg-neutral-900 rounded-2xl shadow-xl ... p-5 border dark:border-white/10">
            <div className="flex items-center justify-between mb-4">
                <h3 className="text-lg font-semibold">{title}</h3>
                <button onClick={onClose} className="w-9 h-9 rounded-lg border hover:bg-gray-50">✕</button>
            </div>
            {children}
        </div>
    </div>
);

const TestSignalForm: React.FC<{ defaultSymbol?: string; onSubmit: (payload: any)=>void }> = ({ defaultSymbol = "BTCUSDT", onSubmit }) => {
    const [symbol, setSymbol] = useState(defaultSymbol);
    const [side, setSide] = useState<"BUY"|"SELL">("BUY");
    const [orderType, setOrderType] = useState<"MARKET"|"LIMIT">("MARKET");
    const [quantity, setQuantity] = useState("0.005");
    const [price, setPrice] = useState("");
    const [tp, setTp] = useState("");
    const [sl, setSl] = useState("");
    const [atr, setAtr] = useState("");

    return (
        <form className="space-y-3" onSubmit={(e)=>{e.preventDefault(); onSubmit({ symbol, side, orderType, quantity: parseFloat(quantity), price: price?parseFloat(price):undefined, takeProfit: tp?parseFloat(tp):undefined, stopLoss: sl?parseFloat(sl):undefined, atr: atr?parseFloat(atr):undefined });}}>
            <div className="grid grid-cols-2 gap-3">
                <Field label="Symbol"><input value={symbol} onChange={e=>setSymbol(e.target.value)} className="input"/></Field>
                <Field label="Side">
                    <select value={side} onChange={e=>setSide(e.target.value as any)} className="input">
                        <option>BUY</option>
                        <option>SELL</option>
                    </select>
                </Field>
                <Field label="Order Type">
                    <select value={orderType} onChange={e=>setOrderType(e.target.value as any)} className="input">
                        <option>MARKET</option>
                        <option>LIMIT</option>
                    </select>
                </Field>
                <Field label="Quantity"><input value={quantity} onChange={e=>setQuantity(e.target.value)} className="input"/></Field>
                <Field label="Price (for LIMIT)"><input value={price} onChange={e=>setPrice(e.target.value)} className="input"/></Field>
                <Field label="ATR"><input value={atr} onChange={e=>setAtr(e.target.value)} className="input"/></Field>
                <Field label="Take Profit"><input value={tp} onChange={e=>setTp(e.target.value)} className="input"/></Field>
                <Field label="Stop Loss"><input value={sl} onChange={e=>setSl(e.target.value)} className="input"/></Field>
            </div>
            <div className="pt-2 flex justify-end">
                <button type="submit" className="px-3 py-2 rounded-xl bg-gray-900 text-white text-sm"><Send className="inline w-4 h-4 mr-1"/> Send</button>
            </div>
            <style>{`
        .input { @apply w-full rounded-xl border px-3 py-2 text-sm bg-white focus:outline-none focus:ring-2 focus:ring-gray-900/20; }
      `}</style>
        </form>
    );
};

const Field: React.FC<{ label: string; children: React.ReactNode }> = ({ label, children }) => (
    <label className="text-sm">
        <div className="text-xs text-gray-500 mb-1">{label}</div>
        {children}
    </label>
);
1