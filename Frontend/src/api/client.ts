// Универсальный HTTP-клиент. Все запросы идут через /api → gateway:8080

type GRequestInit = globalThis.RequestInit;
type GHeadersInit = globalThis.HeadersInit;
type GRequestInfo = globalThis.RequestInfo | URL;


// ===== BasicAuth storage =====
let _basicAuth: string | null = null;

export function setBasicAuth(username: string, password: string) {
    _basicAuth = `Basic ${btoa(`${username}:${password}`)}`;
    try { localStorage.setItem("basicAuth", _basicAuth); } catch {}
}

export function loadBasicAuthFromStorage() {
    try {
        const v = localStorage.getItem("basicAuth");
        if (v) _basicAuth = v;
    } catch {}
}

export function clearBasicAuth() {
    _basicAuth = null;
    try { localStorage.removeItem("basicAuth"); } catch {}
}

function buildInit(init?: GRequestInit): GRequestInit {
    const { headers: raw, ...rest } =
        (init ?? {}) as GRequestInit & { headers?: GHeadersInit };

    const headers = new Headers(raw);
    if (!headers.has("Content-Type")) {
        headers.set("Content-Type", "application/json");
    }

    if (_basicAuth && !headers.has("Authorization")) {
        headers.set("Authorization", _basicAuth);
    }

    return { ...rest, headers: headers as GHeadersInit, credentials: "include" };
}

export async function api<T>(input: GRequestInfo, init: GRequestInit = {}): Promise<T> {
    const res = await fetch(input as RequestInfo, buildInit(init) as RequestInit);

    if (!res.ok) {
        const text = await safeReadText(res);
        throw new Error(`HTTP ${res.status} ${res.statusText}${text ? `: ${text}` : ""}`);
    }

    if (isEmptyBody(res)) {
        return undefined as unknown as T;
    }
    return res.json() as Promise<T>;
}

/* ---------- helpers ---------- */
function isEmptyBody(res: Response) {
    if (res.status === 204 || res.status === 205) return true;
    const len = res.headers.get("content-length");
    return len === "0";
}
async function safeReadText(res: Response) {
    try { return await res.text(); } catch { return ""; }
}
