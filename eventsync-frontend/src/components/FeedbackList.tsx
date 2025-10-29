import type { FeedbackResponseDTO } from "../types";

export default function FeedbackList({
    items,
}: {
    items: FeedbackResponseDTO[];
}) {
    if (!items.length)
        return <div className="text-sm text-gray-500">No feedback yet.</div>;

    return (
        <div>
            <h3 className="font-semibold mb-2">Feedback</h3>
            <ul className="grid gap-2">
                {items.map((f) => (
                    <li key={f.id} className="border rounded p-3">
                        <div className="font-medium">{f.message}</div>
                        <div className="text-xs text-gray-600 mt-1">
                            {f.sentiment} ·{" "}
                            {new Date(f.createdAt).toLocaleString()}
                        </div>
                    </li>
                ))}
            </ul>
        </div>
    );
}
