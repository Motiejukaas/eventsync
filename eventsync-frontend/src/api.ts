import axios from "axios";

// In production (nginx), use /api so nginx can proxy to backend; in dev, use local backend.
const isProd = import.meta.env.PROD;
const baseURL = import.meta.env.VITE_API_URL || (isProd ? "/api" : "http://localhost:8080");

const api = axios.create({ baseURL });

export default api;
