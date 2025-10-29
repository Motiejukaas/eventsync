import { useEffect, useState } from "react";
import api from "../api";
import type { EventResponseDTO } from "../types";
import { Link } from "react-router-dom";

export default function EventList() {
    const [events, setEvents] = useState<EventResponseDTO[]>([]);
    const [loading, setLoading] = useState(true);

    const load = async () => {
        setLoading(true);
        const res = await api.get<EventResponseDTO[]>("/events");
        setEvents(res.data);
        setLoading(false);
    };

    useEffect(() => {
        load();
        const handler = () => load();
        window.addEventListener("events:reload", handler);
        return () =>
            window.removeEventListener("events:reload", handler);
    }, []);

    if (loading)
        return <div className="text-sm text-gray-500">Loading events…</div>;
    if (!events.length)
        return <div className="text-sm text-gray-500">No events yet.</div>;

    return (
        <div>
            <h2 className="text-lg font-semibold mb-2">Events</h2>
            <ul className="grid gap-3">
                {events.map((ev) => (
                    <li key={ev.id} className="border rounded p-3">
                        <div className="font-semibold">{ev.title}</div>
                        <div className="text-sm text-gray-700">
                            {ev.description}
                        </div>
                        <div className="text-sm text-gray-500 mt-1">
                            👍 {ev.positiveFeedbackSentimentCount} | 😐{" "}
                            {ev.neutralFeedbackSentimentCount} | 👎{" "}
                            {ev.negativeFeedbackSentimentCount}
                        </div>
                        <Link
                            to={`/events/${ev.id}`}
                            className="inline-block mt-2 bg-gray-900 text-white px-3 py-1 rounded"
                        >
                            View
                        </Link>
                    </li>
                ))}
            </ul>
        </div>
    );
}
