export interface EventRequestDTO {
    title: string;
    description: string;
}

export interface EventResponseDTO {
    id: string;
    title: string;
    description: string;
    positiveFeedbackSentimentCount: number;
    neutralFeedbackSentimentCount: number;
    negativeFeedbackSentimentCount: number;
    feedbackSentimentSummary: string;
}

export interface EventSentimentBreakdownDTO {
    eventId: string;
    totalFeedbacks: number;
    positive: number;
    neutral: number;
    negative: number;
    positivePercent: number;
    neutralPercent: number;
    negativePercent: number;
}

export interface FeedbackRequestDTO {
    message: string;
}

export type SentimentType = "POSITIVE" | "NEUTRAL" | "NEGATIVE";

export interface FeedbackResponseDTO {
    id: string;
    message: string;
    sentiment: SentimentType;
    createdAt: string;
    eventId: string;
}
