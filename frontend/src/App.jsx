import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

// Import contexts
import { AuthProvider } from './contexts/AuthContext';
import { I18nProvider } from './contexts/I18nContext';

// Import pages
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Students from './pages/Students';
import Teachers from './pages/Teachers';
import Attendance from './pages/Attendance';
import Reports from './pages/Reports';
import Analytics from './pages/Analytics';
import Notifications from './pages/Notifications';

// Import layout and auth components
import Layout from './components/Layout';
import PrivateRoute from './components/PrivateRoute';

// Create a client for React Query
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 1,
      staleTime: 5 * 60 * 1000, // 5 minutes
    },
  },
});

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <I18nProvider>
        <AuthProvider>
          <Router>
            <div className="min-h-screen bg-gray-50">
              <Routes>
              {/* Public route */}
              <Route path="/login" element={<Login />} />
              
              {/* Protected routes with role-based access */}
              <Route 
                path="/*" 
                element={
                  <PrivateRoute>
                    <Layout>
                      <Routes>
                        {/* Dashboard - accessible to all authenticated users */}
                        <Route path="/dashboard" element={<Dashboard />} />
                        
                        {/* Students - accessible to Admin and Teacher */}
                        <Route 
                          path="/students" 
                          element={
                            <PrivateRoute requiredRole={['ADMIN', 'TEACHER']}>
                              <Students />
                            </PrivateRoute>
                          } 
                        />
                        
                        {/* Teachers - Admin only */}
                        <Route 
                          path="/teachers" 
                          element={
                            <PrivateRoute requiredRole="ADMIN">
                              <Teachers />
                            </PrivateRoute>
                          } 
                        />
                        
                        {/* Attendance - Admin and Teacher */}
                        <Route 
                          path="/attendance" 
                          element={
                            <PrivateRoute requiredRole={['ADMIN', 'TEACHER']}>
                              <Attendance />
                            </PrivateRoute>
                          } 
                        />
                        
                        {/* Reports - Admin and Teacher */}
                        <Route 
                          path="/reports" 
                          element={
                            <PrivateRoute requiredRole={['ADMIN', 'TEACHER']}>
                              <Reports />
                            </PrivateRoute>
                          } 
                        />
                        
                        {/* Analytics - Admin and Teacher */}
                        <Route 
                          path="/analytics" 
                          element={
                            <PrivateRoute requiredRole={['ADMIN', 'TEACHER']}>
                              <Analytics />
                            </PrivateRoute>
                          } 
                        />
                        
                        {/* Notifications - Admin and Teacher */}
                        <Route 
                          path="/notifications" 
                          element={
                            <PrivateRoute requiredRole={['ADMIN', 'TEACHER']}>
                              <Notifications />
                            </PrivateRoute>
                          } 
                        />
                        
                        {/* Student Dashboard - Students only */}
                        <Route 
                          path="/my-attendance" 
                          element={
                            <PrivateRoute requiredRole="STUDENT">
                              <div className="p-6">
                                <h1 className="text-2xl font-bold text-gray-900 mb-6">My Attendance</h1>
                                <div className="bg-white rounded-lg shadow p-6">
                                  <p className="text-gray-600">Student attendance view coming soon...</p>
                                </div>
                              </div>
                            </PrivateRoute>
                          } 
                        />
                        
                        {/* Default redirect */}
                        <Route path="/" element={<Navigate to="/dashboard" replace />} />
                        
                        {/* 404 - Not Found */}
                        <Route path="*" element={
                          <div className="min-h-screen flex items-center justify-center">
                            <div className="text-center">
                              <h1 className="text-4xl font-bold text-gray-900 mb-4">404</h1>
                              <p className="text-gray-600 mb-4">Page not found</p>
                              <Navigate to="/dashboard" replace />
                            </div>
                          </div>
                        } />
                      </Routes>
                    </Layout>
                  </PrivateRoute>
                } 
              />
            </Routes>
          </div>
        </Router>
      </AuthProvider>
    </I18nProvider>
  </QueryClientProvider>
  );
}

export default App;
