import { useForm } from "react-hook-form";
import api from "../api";
import type { EventRequestDTO } from "../types";
import { useState } from "react";

export default function EventForm() {
    const { register, handleSubmit, reset } = useForm<EventRequestDTO>();
    const [loading, setLoading] = useState(false);

    const onSubmit = async (data: EventRequestDTO) => {
        setLoading(true);
        try {
            await api.post("/events", data);
            reset();
            // naive refresh of list below
            window.dispatchEvent(new CustomEvent("events:reload"));
        } finally {
            setLoading(false);
        }
    };

    return (
        <form
            onSubmit={handleSubmit(onSubmit)}
            className="border rounded p-4 grid gap-3"
        >
            <h2 className="text-lg font-semibold">Create Event</h2>
            <input
                {...register("title", { required: true })}
                placeholder="Title"
                className="border rounded p-2"
            />
            <textarea
                {...register("description", { required: true })}
                placeholder="Description"
                className="border rounded p-2"
                rows={3}
            />
            <button
                type="submit"
                disabled={loading}
                className="bg-blue-600 text-white px-4 py-2 rounded"
            >
                {loading ? "Creating..." : "Create"}
            </button>
        </form>
    );
}
