import api from './api';

/** Drops empty filters so the URL and the query stay clean. */
function clean(params) {
  return Object.fromEntries(
    Object.entries(params).filter(([, value]) => value !== '' && value !== null && value !== undefined && value !== false)
  );
}

export const searchProviders = (params) => api.get('/api/providers', { params: clean(params) });
export const getProvider = (id) => api.get(`/api/providers/${id}`);
export const getFeaturedProviders = () => api.get('/api/providers/featured');
export const getSearchMeta = () => api.get('/api/providers/meta');
export const submitListing = (data) => api.post('/api/listings', data);
export const submitCorrection = (data) => api.post('/api/corrections', data);
