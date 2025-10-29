import EventForm from "./components/EventForm";
import EventList from "./components/EventList";

export default function App() {
    return (
        <div className="max-w-3xl mx-auto p-4">
            <h1 className="text-3xl font-bold mb-6">EventSync</h1>
            <div className="grid gap-6">
                <EventForm />
                <EventList />
            </div>
        </div>
    );
}
