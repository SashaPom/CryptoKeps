import React, { useState } from "react";
import type { TwProfile } from "../types/tw";

/* ---------- helpers ---------- */
function fmtMoney(n?: number | null) {
    const v = Number(n ?? 0);
    return Number.isFinite(v) ? v.toFixed(0) : "0";
}
function yesNo(b?: boolean | null) {
    return b ? "YES" : "NO";
}
function exTitle(ex?: string | number | null) {
    if (typeof ex === "number") {
        return ex === 1 ? "OKX" : "BINANCE";
    }
    const s = String(ex ?? "").trim().toUpperCase();
    return s === "OKX" ? "OKX" : "BINANCE";
}

function formatDate(s?: string | null) {
    if (!s) return "-";
    const d = new Date(s);
    if (Number.isNaN(d.getTime())) return s;
    const dd = String(d.getDate()).padStart(2, "0");
    const mm = String(d.getMonth() + 1).padStart(2, "0");
    const yyyy = d.getFullYear();
    return `${dd}.${mm}.${yyyy}`;
}

/* ---------- component ---------- */
export default function ProfileCard({
                                        p,
                                        onStart,
                                        onStop,
                                        onEdit,
                                        onDelete,
                                    }: {
    p: TwProfile;
    onStart: (id: number) => void;
    onStop: (id: number) => void;
    onEdit: (p: TwProfile) => void;
    onDelete: (p: TwProfile) => void;
}) {
    // только клик по INFO переключает режим
    const [showInfo, setShowInfo] = useState(false);

    return (
        <div className="rounded-2xl border bg-white shadow-sm p-4 lg:p-5">
            {/* Шапка: заголовок + кнопки справа */}
            <div className="mb-3 flex items-start justify-between">
                <div className="text-base md:text-lg font-semibold tracking-wide">
                    {exTitle(p.exchange)}
                </div>

                <div className="flex items-center gap-3">
                    {p.status === "running" ? (
                        <button
                            className="text-blue-700 underline underline-offset-4 text-sm"
                            onClick={() => onStop(p.id)}
                        >
                            STOP
                        </button>
                    ) : (
                        <button
                            className="text-blue-700 underline underline-offset-4 text-sm"
                            onClick={() => onStart(p.id)}
                        >
                            START
                        </button>
                    )}

                    <button
                        className="text-blue-700 underline underline-offset-4 text-sm"
                        onClick={() => setShowInfo((v) => !v)}
                        title={showInfo ? "Hide info" : "Show info"}
                    >
                        INFO
                    </button>
                    <button
                        className="text-blue-700 underline underline-offset-4 text-sm"
                        onClick={() => onEdit(p)}
                    >
                        EDIT
                    </button>
                    <button
                        className="text-red-700 underline underline-offset-4 text-sm"
                        onClick={() => onDelete(p)}
                    >
                        DELETE
                    </button>
                </div>
            </div>


            {/* Фиксированная по высоте область контента: карточка не прыгает */}
            <div className="relative min-h-[220px] sm:min-h-[200px]">
                {/* --- Витрина (основные показатели) --- */}
                <div className={showInfo ? "hidden" : "block"}>
                    <div className="grid grid-cols-[180px_1fr] gap-x-3 gap-y-1 text-sm">
                        <div className="text-gray-500">Name:</div>
                        <div className="font-medium">{p.name}</div>

                        <div className="text-gray-500">Available Balance:</div>
                        <div className="font-medium">{fmtMoney(p.available_balance)} USDT</div>

                        <div className="text-gray-500">Balance:</div>
                        <div className="font-medium">{fmtMoney(p.balance)} USDT</div>

                        <div className="text-gray-500">Max Balance:</div>
                        <div className="font-medium">{fmtMoney(p.max_balance)} USDT</div>

                        <div className="text-gray-500">Pnl:</div>
                        <div className="font-medium">{fmtMoney(p.pnl)}</div>

                        <div className="text-gray-500">Lose Streak:</div>
                        <div className="font-medium">{fmtMoney(p.lose_streak)}</div>

                        <div className="text-gray-500">Trade Now:</div>
                        <div
                            className={
                                p.trade_now ? "text-green-600 font-semibold" : "text-red-600 font-semibold"
                            }
                        >
                            {yesNo(p.trade_now)}
                        </div>

                        <div className="col-span-2 text-center text-sm font-bold mt-2">
                            {p.blocked ? "BLOCKED" : ""}
                        </div>
                    </div>
                </div>

                {/* --- Инфо (параметры вотчера) --- */}
                <div className={showInfo ? "block" : "hidden"}>
                    <div className="grid grid-cols-[180px_1fr] gap-x-3 gap-y-2 text-sm">
                        <div className="text-gray-500">Name:</div>
                        <div className="font-medium">{p.name}</div>

                        <div className="text-gray-500">Refresh Time:</div>
                        <div className="font-medium">{p.refresh_time || "-"}</div>

                        <div className="text-gray-500">Lose $ Limit:</div>
                        <div className="font-medium">
                            {p.lose_amount_limit != null ? `${p.lose_amount_limit} $` : "-"}
                        </div>

                        <div className="text-gray-500">Lose % Limit:</div>
                        <div className="font-medium">
                            {p.lose_percent_limit != null ? `${p.lose_percent_limit} %` : "-"}
                        </div>

                        <div className="text-gray-500">Lose Streak Limit:</div>
                        <div className="font-medium">
                            {p.lose_streak_limit != null ? `${p.lose_streak_limit}` : "-"}
                        </div>

                        <div className="text-gray-500">Created At:</div>
                        <div className="font-medium">{formatDate(p.created_at)}</div>
                    </div>
                </div>
            </div>
        </div>
    );
}
