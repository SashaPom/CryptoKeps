import { api } from "./client";
import type { TwCreatePayload, TwProfile } from "../types/tw";


/** Django TW API (риск-менеджер) */

export const TwApi = {
    listProfiles: () => api<TwProfile[]>('/api/py/dj/tw/api/watcher/profiles/'),

    create: (payload: TwCreatePayload) =>
        api<{ id: number }>('/py/dj/tw/create/', {
            method: 'POST',
            body: JSON.stringify(payload),
        }),

    start: (id: number) =>
        api<{ ok: true }>('/py/dj/tw/start/', {
            method: 'POST',
            body: JSON.stringify({ id }),
        }),

    stop: (id: number) =>
        api<{ ok: true }>('/py/dj/tw/stop/', {
            method: 'POST',
            body: JSON.stringify({ id }),
        }),
};
