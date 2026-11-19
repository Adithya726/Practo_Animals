import api from './api';

export const generateCarousel = (data) => api.post('/api/carousels/generate', data);
export const getCarousel = (id) => api.get(`/api/carousels/${id}`);
export const updateCarousel = (id, data) => api.put(`/api/carousels/${id}`, data);
export const getRecentCarousels = () => api.get('/api/carousels');
export const getWriter = () => api.get('/api/carousels/engine');
