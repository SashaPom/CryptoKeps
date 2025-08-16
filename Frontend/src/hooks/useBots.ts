import { useEffect, useState } from "react";
import { listBots, recentTrades } from "../api/bots";
import type { BotCard, TradeRow } from "../types/bots";

export function useBots(filters: { q: string; status: string; period: "24h"|"7d"|"30d" }) {
    const [data, setData] = useState<BotCard[] | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        let aborted = false;
        setLoading(true);
        setError(null);
        listBots({ q: filters.q, status: filters.status, period: filters.period })
            .then(b => { if (!aborted) setData(b); })
            .catch(e => { if (!aborted) setError(e.message || "Failed to load bots"); })
            .finally(() => { if (!aborted) setLoading(false); });
        return () => { aborted = true; };
    }, [filters.q, filters.status, filters.period]);

    return { data, loading, error };
}

export function useTrades(botId: string | null, period: "24h"|"7d"|"30d") {
    const [data, setData] = useState<TradeRow[] | null>(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        if (!botId) return;
        let aborted = false;
        setLoading(true);
        setError(null);
        recentTrades(botId, period)
            .then(t => { if (!aborted) setData(t); })
            .catch(e => { if (!aborted) setError(e.message || "Failed to load trades"); })
            .finally(() => { if (!aborted) setLoading(false); });
        return () => { aborted = true; };
    }, [botId, period]);

    return { data, loading, error };
}
