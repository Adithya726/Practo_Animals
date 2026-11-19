import api from './api';

export const loginAdmin = (data) => api.post('/api/auth/login', data);
export const getSummary = () => api.get('/api/admin/summary');
export const getListings = () => api.get('/api/admin/listings');
export const setListingStatus = (id, status) => api.patch(`/api/admin/listings/${id}/status`, { status });
export const getCorrections = () => api.get('/api/admin/corrections');
export const setCorrectionStatus = (id, status) => api.patch(`/api/admin/corrections/${id}/status`, { status });
export const getLeads = () => api.get('/api/admin/leads');
export const setLeadStatus = (id, status) => api.patch(`/api/admin/leads/${id}/status`, { status });
export const deleteCarousel = (id) => api.delete(`/api/admin/carousels/${id}`);
