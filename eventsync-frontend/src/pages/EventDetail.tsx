import { useCallback, useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import api from "../api";
import type {
    EventSentimentBreakdownDTO,
    FeedbackResponseDTO,
    FeedbackRequestDTO,
} from "../types";
import FeedbackForm from "../components/FeedbackForm";
import FeedbackList from "../components/FeedbackList";
import SummaryCard from "../components/SummaryCard";

export default function EventDetail() {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();

    const [summary, setSummary] = useState<EventSentimentBreakdownDTO | null>(
        null
    );
    const [aiSummary, setAiSummary] = useState<string>("");
    const [feedbacks, setFeedbacks] = useState<FeedbackResponseDTO[]>([]);
    const [loading, setLoading] = useState(true);

    const load = useCallback(async () => {
        if (!id) return;
        setLoading(true);
        const [s, f] = await Promise.all([
            api.get<EventSentimentBreakdownDTO>(`/events/${id}/summary`),
            api.get<FeedbackResponseDTO[]>(`/feedbacks/${id}`),
        ]);
        setSummary(s.data);
        setFeedbacks(f.data);
        try {
            const ai = await api.get<string>(`/events/${id}/summary/ai`, {
                responseType: "text",
            });
            setAiSummary(
                typeof ai.data === "string" ? ai.data : String(ai.data)
            );
        } catch {
            setAiSummary("");
        }
        setLoading(false);
    }, [id]);

    useEffect(() => {
        load();
    }, [load]);

    const onSubmitFeedback = async (payload: FeedbackRequestDTO) => {
        await api.post(`/events/${id}/feedback`, payload);
        await load();
        window.dispatchEvent(new CustomEvent("events:reload"));
    };

    const onDelete = async () => {
        if (!id) return;
        await api.delete(`/events/${id}`);
        window.dispatchEvent(new CustomEvent("events:reload"));
        navigate("/");
    };

    if (loading) {
        return (
            <div className="max-w-3xl mx-auto p-4">
                <div className="text-sm text-gray-500">Loading…</div>
            </div>
        );
    }

    if (!summary) {
        return (
            <div className="max-w-3xl mx-auto p-4">
                <Link to="/" className="text-blue-700 underline">
                    &larr; Back
                </Link>
                <div className="mt-4 text-sm text-gray-500">
                    Event not found.
                </div>
            </div>
        );
    }

    return (
        <div className="max-w-3xl mx-auto p-4 grid gap-4">
            <div className="flex items-center justify-between">
                <Link to="/" className="text-blue-700 underline">
                    &larr; Back
                </Link>
                <button
                    onClick={onDelete}
                    className="bg-red-600 text-white px-3 py-1 rounded"
                >
                    Delete Event
                </button>
            </div>

            <SummaryCard summary={summary} />

            {aiSummary && (
                <div className="border rounded p-3">
                    <h3 className="font-semibold mb-1">AI Summary</h3>
                    <p className="text-sm whitespace-pre-wrap">{aiSummary}</p>
                </div>
            )}

            <FeedbackForm onSubmit={onSubmitFeedback} />

            <FeedbackList items={feedbacks} />
        </div>
    );
}
