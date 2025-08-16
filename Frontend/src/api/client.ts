export async function fetchJson<T>(input: RequestInfo | URL, init: RequestInit = {}): Promise<T> {
    // Собираем заголовки из любых возможных форматов (Headers | массив | объект)
    const headers = new Headers(init.headers);

    // Проставим Content-Type по умолчанию, если его нет (для JSON-запросов)
    if (!headers.has("Content-Type")) {
        headers.set("Content-Type", "application/json");
    }

    const res = await fetch(input, {
        ...init,
        headers, // <= передаем уже нормализованный Headers
    });

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
