import React, { createContext, useContext, useState, useEffect } from 'react';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [loading, setLoading] = useState(true);

  // Load user data from localStorage on app start
  useEffect(() => {
    const savedToken = localStorage.getItem('token');
    const savedUser = localStorage.getItem('user');
    
    if (savedToken && savedUser) {
      setToken(savedToken);
      setUser(JSON.parse(savedUser));
      
      // Set default authorization header
      if (window.axios?.defaults?.headers?.common) {
        window.axios.defaults.headers.common['Authorization'] = `Bearer ${savedToken}`;
      }
    }
    
    setLoading(false);
  }, []);

  const login = async (credentials) => {
    try {
      const response = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        credentials: 'include', // Include cookies for session management
        body: JSON.stringify(credentials),
      });

      const data = await response.json();
      
      if (!response.ok) {
        throw new Error(data.error || data.message || 'Login failed');
      }

      if (!data.success) {
        throw new Error(data.error || data.message || 'Login failed');
      }
      
      // Store token and user data based on our backend response structure
      localStorage.setItem('token', data.token);
      localStorage.setItem('user', JSON.stringify({
        id: data.user.id,
        username: data.user.username,
        role: data.user.role
      }));

      setToken(data.token);
      setUser({
        id: data.user.id,
        username: data.user.username,
        role: data.user.role
      });

      // Set default authorization header
      if (window.axios?.defaults?.headers?.common) {
        window.axios.defaults.headers.common['Authorization'] = `Bearer ${data.token}`;
      }

      return { success: true, data };
    } catch (error) {
      console.error('Login error:', error);
      return { success: false, error: error.message };
    }
  };

  const logout = () => {
    // Clear localStorage
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    
    // Clear state
    setToken(null);
    setUser(null);
    
    // Clear authorization header
    if (window.axios?.defaults?.headers?.common) {
      delete window.axios.defaults.headers.common['Authorization'];
    }
  };

  const register = async (userData) => {
    try {
      const response = await fetch('http://localhost:8080/api/auth/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': token ? `Bearer ${token}` : '',
        },
        body: JSON.stringify(userData),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.error || 'Registration failed');
      }

      const data = await response.json();
      return { success: true, data };
    } catch (error) {
      console.error('Registration error:', error);
      return { success: false, error: error.message };
    }
  };

  const validateToken = async () => {
    if (!token) return false;

    try {
      const response = await fetch('http://localhost:8080/api/auth/validate', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        logout(); // Token is invalid, logout user
        return false;
      }

      const data = await response.json();
      return data.valid;
    } catch (error) {
      console.error('Token validation error:', error);
      logout();
      return false;
    }
  };

  const getCurrentUser = async () => {
    if (!token) return null;

    try {
      const response = await fetch('http://localhost:8080/api/auth/me', {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        return null;
      }

      const data = await response.json();
      return data;
    } catch (error) {
      console.error('Get current user error:', error);
      return null;
    }
  };

  // Helper functions to check user roles
  const isAdmin = () => user?.role === 'ADMIN';
  const isTeacher = () => user?.role === 'TEACHER';
  const isStudent = () => user?.role === 'STUDENT';
  
  const hasRole = (requiredRole) => {
    if (Array.isArray(requiredRole)) {
      return requiredRole.includes(user?.role);
    }
    return user?.role === requiredRole;
  };

  const hasPermission = (permission) => {
    if (!user) return false;
    
    const rolePermissions = {
      ADMIN: [
        'MANAGE_USERS',
        'MANAGE_STUDENTS', 
        'MANAGE_TEACHERS',
        'MANAGE_ATTENDANCE',
        'VIEW_REPORTS',
        'EXPORT_REPORTS'
      ],
      TEACHER: [
        'MANAGE_ATTENDANCE',
        'VIEW_REPORTS',
        'VIEW_STUDENTS'
      ],
      STUDENT: [
        'VIEW_OWN_ATTENDANCE'
      ]
    };

    return rolePermissions[user.role]?.includes(permission) || false;
  };

  const value = {
    user,
    token,
    loading,
    login,
    logout,
    register,
    validateToken,
    getCurrentUser,
    isAdmin,
    isTeacher,
    isStudent,
    hasRole,
    hasPermission,
    isAuthenticated: !!token && !!user,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};
