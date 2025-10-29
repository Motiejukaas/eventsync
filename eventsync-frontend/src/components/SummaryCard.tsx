import type { EventSentimentBreakdownDTO } from "../types";

export default function SummaryCard({
    summary,
}: {
    summary: EventSentimentBreakdownDTO;
}) {
    return (
        <div className="border rounded p-3">
            <h3 className="font-semibold mb-2">Sentiment Summary</h3>
            <div className="grid grid-cols-2 gap-2 text-sm">
                <div>Total</div>
                <div>{summary.totalFeedbacks}</div>
                <div>Positive</div>
                <div>
                    {summary.positive} ({summary.positivePercent.toFixed(1)}%)
                </div>
                <div>Neutral</div>
                <div>
                    {summary.neutral} ({summary.neutralPercent.toFixed(1)}%)
                </div>
                <div>Negative</div>
                <div>
                    {summary.negative} ({summary.negativePercent.toFixed(1)}%)
                </div>
            </div>
        </div>
    );
}
