import { useEffect, useMemo, useState } from "react";
import { TwApi } from "../api/client";

type Profile = {
    id: number;
    name: string;
    status: "running" | "stopped" | string;
};

export default function RiskManager() {
    const [profiles, setProfiles] = useState<Profile[]>([]);
    const [error, setError] = useState<string | null>(null);

    const wsOkxUrl = useMemo(() => `${location.protocol === "https:" ? "wss" : "ws"}://${location.host}/ws/okx`, []);
    const wsBinUrl = useMemo(() => `${location.protocol === "https:" ? "wss" : "ws"}://${location.host}/ws/binance`, []);

    useEffect(() => {
        refresh();

        // Подписка на оба канала (если нужны сразу)
        const okx = new WebSocket(wsOkxUrl);
        okx.onmessage = (e) => {
            try {
                const data = JSON.parse(e.data);
                // если бек шлет структуру с профилями
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

        return () => { okx.close(); bin.close(); };
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    function refresh() {
        TwApi.listProfiles()
            .then(setProfiles)
            .catch(err => setError(err.message || String(err)));
    }

    function start(id: number) {
        TwApi.start(id).then(refresh);
    }

    function stop(id: number) {
        TwApi.stop(id).then(refresh);
    }

    return (
        <div className="p-6 space-y-4">
            <h1 className="text-2xl font-semibold">Risk Manager</h1>
            {error && <div className="text-red-500">{error}</div>}

            <div className="overflow-x-auto border rounded-xl">
                <table className="min-w-full text-sm">
                    <thead className="bg-gray-50">
                    <tr>
                        <th className="px-3 py-2 text-left">ID</th>
                        <th className="px-3 py-2 text-left">Name</th>
                        <th className="px-3 py-2 text-left">Status</th>
                        <th className="px-3 py-2 text-left">Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {profiles.map(p => (
                        <tr key={p.id} className="border-t">
                            <td className="px-3 py-2">{p.id}</td>
                            <td className="px-3 py-2">{p.name}</td>
                            <td className="px-3 py-2">
                  <span className={p.status === "running" ? "text-green-600" : "text-gray-500"}>
                    {p.status}
                  </span>
                            </td>
                            <td className="px-3 py-2 space-x-2">
                                {p.status === "running" ? (
                                    <button className="px-3 py-1 rounded bg-red-600 text-white" onClick={() => stop(p.id)}>
                                        Stop
                                    </button>
                                ) : (
                                    <button className="px-3 py-1 rounded bg-green-600 text-white" onClick={() => start(p.id)}>
                                        Start
                                    </button>
                                )}
                            </td>
                        </tr>
                    ))}
                    {profiles.length === 0 && (
                        <tr><td className="px-3 py-6 text-gray-500" colSpan={4}>Нет профилей</td></tr>
                    )}
                    </tbody>
                </table>
            </div>
        </div>
    );
}
