import { useForm } from "react-hook-form";
import type { FeedbackRequestDTO } from "../types";
import { useState } from "react";

export default function FeedbackForm({
    onSubmit,
}: {
    onSubmit: (p: FeedbackRequestDTO) => Promise<void>;
}) {
    const { register, handleSubmit, reset } = useForm<FeedbackRequestDTO>();
    const [loading, setLoading] = useState(false);

    const submit = async (data: FeedbackRequestDTO) => {
        setLoading(true);
        try {
            await onSubmit(data);
            reset();
        } finally {
            setLoading(false);
        }
    };

    return (
        <form
            onSubmit={handleSubmit(submit)}
            className="border rounded p-3 grid gap-2"
        >
            <h3 className="font-semibold">Leave Feedback</h3>
            <textarea
                {...register("message", { required: true, maxLength: 5000 })}
                className="border rounded p-2"
                placeholder="Your feedback…"
                rows={3}
            />
            <button
                type="submit"
                disabled={loading}
                className="bg-blue-600 text-white px-4 py-2 rounded"
            >
                {loading ? "Submitting…" : "Submit"}
            </button>
        </form>
    );
}
