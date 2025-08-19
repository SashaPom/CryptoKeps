export async function api(path: string, init: RequestInit = {}): Promise<any> {
    const headers = new Headers(init.headers);

    if (!headers.has("Content-Type")) {
        headers.set("Content-Type", "application/json");
    }

    // Собираем объект отдельно
    const requestInit: RequestInit = {
        ...init,
        headers: headers as HeadersInit, // явный каст
    };

    const res: Response = await fetch(`/api${path}`, requestInit);

    if (!res.ok) {
        throw new Error(`${res.status} ${await res.text()}`);
    }

    return res.json();
}
