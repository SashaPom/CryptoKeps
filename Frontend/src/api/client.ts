// Универсальный HTTP-клиент. Все запросы идут через /api → gateway:8080

type GRequestInit = globalThis.RequestInit;
type GHeadersInit = globalThis.HeadersInit;

function buildInit(init?: GRequestInit): GRequestInit {
    const { headers: raw, ...rest } =
        (init ?? {}) as GRequestInit & { headers?: GHeadersInit };

    const headers = new Headers(raw);
    if (!headers.has("Content-Type")) headers.set("Content-Type", "application/json");

    return { ...rest, headers: headers as GHeadersInit };
}

export async function api<T>(input: RequestInfo | URL, init: RequestInit = {}): Promise<T> {
    // Собираем заголовки из любых возможных форматов (Headers | массив | объект)
    const headers = new Headers(init.headers);

    // Проставим Content-Type по умолчанию, если его нет (для JSON-запросов)
    if (!headers.has("Content-Type")) {
        headers.set("Content-Type", "application/json");
    }

    const res = await fetch(input, buildInit(init));

    if (!res.ok) {
        const text = await res.text().catch(() => "");
        throw new Error(`HTTP ${res.status} ${res.statusText}${text ? `: ${text}` : ""}`);
    }

    // Если тело пустое (например 204), защитимся от ошибки парсинга
    const contentLength = res.headers.get("content-length");
    if (res.status === 204 || contentLength === "0") {
        return undefined as unknown as T;
    }

    return res.json() as Promise<T>;
}

/** Django TW API (риск-менеджер) */
export const TwApi = {
    listProfiles: () => api('/api/py/dj/tw/api/watcher/profiles/'),
    create: (payload: any) => api('/api/py/dj/tw/create/', { method: 'POST', body: JSON.stringify(payload) }),
    start: (id: number) => api('/api/py/dj/tw/start/', { method: 'POST', body: JSON.stringify({ id }) }),
    stop:  (id: number) => api('/api/py/dj/tw/stop/',  { method: 'POST', body: JSON.stringify({ id }) }),
};

/* ---------- helpers ---------- */
function isEmptyBody(res: Response) {
    const len = res.headers.get("content-length");
    return len === "0" || len === null;
}
async function safeReadText(res: Response) {
    try { return await res.text(); } catch { return ""; }
}
