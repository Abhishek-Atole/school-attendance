import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useState } from 'react';

// Import pages
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Students from './pages/Students';
import Teachers from './pages/Teachers';
import Attendance from './pages/Attendance';
import Reports from './pages/Reports';

// Import layout components
import Layout from './components/Layout';

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
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  // Mock authentication - in real app, this would check JWT token
  const mockAuth = () => {
    setIsAuthenticated(true);
    localStorage.setItem('token', 'mock-jwt-token');
  };

  const logout = () => {
    setIsAuthenticated(false);
    localStorage.removeItem('token');
  };

  return (
    <QueryClientProvider client={queryClient}>
      <Router>
        <div className="min-h-screen bg-gray-50">
          <Routes>
            <Route 
              path="/login" 
              element={
                isAuthenticated ? 
                <Navigate to="/dashboard" replace /> : 
                <Login onLogin={mockAuth} />
              } 
            />
            
            {/* Protected routes */}
            <Route 
              path="/*" 
              element={
                isAuthenticated ? 
                <Layout onLogout={logout}>
                  <Routes>
                    <Route path="/dashboard" element={<Dashboard />} />
                    <Route path="/students" element={<Students />} />
                    <Route path="/teachers" element={<Teachers />} />
                    <Route path="/attendance" element={<Attendance />} />
                    <Route path="/reports" element={<Reports />} />
                    <Route path="/" element={<Navigate to="/dashboard" replace />} />
                  </Routes>
                </Layout> :
                <Navigate to="/login" replace />
              } 
            />
          </Routes>
        </div>
      </Router>
    </QueryClientProvider>
  );
}

export default App;
