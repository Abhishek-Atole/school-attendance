import axios from 'axios';

// Base API configuration
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Student API
export const studentAPI = {
  getAll: (params = {}) => api.get('/students', { params }),
  getById: (id) => api.get(`/students/${id}`),
  create: (data) => api.post('/students', data),
  update: (id, data) => api.put(`/students/${id}`, data),
  delete: (id) => api.delete(`/students/${id}`),
  activate: (id) => api.put(`/students/${id}/activate`),
  deactivate: (id) => api.put(`/students/${id}/deactivate`),
  search: (params) => api.get('/students/search', { params }),
  getBySchool: (schoolId, params = {}) => api.get(`/students/school/${schoolId}`, { params }),
};

// Teacher API
export const teacherAPI = {
  getAll: (params = {}) => api.get('/teachers', { params }),
  getById: (id) => api.get(`/teachers/${id}`),
  create: (data) => api.post('/teachers', data),
  update: (id, data) => api.put(`/teachers/${id}`, data),
  delete: (id) => api.delete(`/teachers/${id}`),
  search: (params) => api.get('/teachers/search', { params }),
  getBySchool: (schoolId, params = {}) => api.get(`/teachers/school/${schoolId}`, { params }),
  assignSubjects: (id, subjects) => api.put(`/teachers/${id}/subjects`, { subjects }),
  assignClasses: (id, classes) => api.put(`/teachers/${id}/classes`, { classes }),
};

// Attendance API
export const attendanceAPI = {
  getAll: (params = {}) => api.get('/attendance', { params }),
  getById: (id) => api.get(`/attendance/${id}`),
  create: (data) => api.post('/attendance', data),
  update: (id, data) => api.put(`/attendance/${id}`, data),
  delete: (id) => api.delete(`/attendance/${id}`),
  markDaily: (data) => api.post('/attendance/mark-daily', data),
  getDailySummary: (schoolId, date) => api.get(`/attendance/daily-summary/${schoolId}`, { params: { date } }),
  getStudentSummary: (studentId, startDate, endDate) => 
    api.get(`/attendance/student/${studentId}/summary`, { params: { startDate, endDate } }),
  getTeacherSummary: (teacherId, startDate, endDate) => 
    api.get(`/attendance/teacher/${teacherId}/summary`, { params: { startDate, endDate } }),
  checkExists: (studentId, date) => api.get(`/attendance/exists`, { params: { studentId, date } }),
};

// Reports API
export const reportsAPI = {
  exportCSV: (params) => api.get('/attendance/export/csv', { 
    params, 
    responseType: 'blob',
    headers: { 'Accept': 'text/csv' }
  }),
  exportExcel: (params) => api.get('/attendance/export/excel', { 
    params, 
    responseType: 'blob',
    headers: { 'Accept': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' }
  }),
  exportPDF: (params) => api.get('/attendance/export/pdf', { 
    params, 
    responseType: 'blob',
    headers: { 'Accept': 'application/pdf' }
  }),
};

// Stats API for dashboard
export const statsAPI = {
  getDashboardStats: (schoolId, dateRange) => api.get('/attendance/stats/dashboard', { 
    params: { schoolId, ...dateRange } 
  }),
  getAttendanceTrends: (schoolId, period) => api.get('/attendance/stats/trends', { 
    params: { schoolId, period } 
  }),
  getClassWiseStats: (schoolId, date) => api.get('/attendance/stats/class-wise', { 
    params: { schoolId, date } 
  }),
};

export default api;
