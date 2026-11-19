import api from './api';

export const getCourses = () => api.get('/api/courses');
export const submitLead = (data) => api.post('/api/leads', data);
