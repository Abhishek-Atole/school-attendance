import { Menu, Bell, User, LogOut, School, Settings, Shield, BookOpen } from 'lucide-react';
import { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useI18n } from '../contexts/I18nContext';
import LanguageSelector from './LanguageSelector';

const Navbar = ({ onToggleSidebar }) => {
  const [showUserMenu, setShowUserMenu] = useState(false);
  const { user, logout, isAdmin, isTeacher, isStudent } = useAuth();
  const { t } = useI18n();

  const handleLogout = () => {
    setShowUserMenu(false);
    logout();
  };

  const getRoleIcon = () => {
    if (isAdmin()) return <Shield className="h-4 w-4 text-red-600" />;
    if (isTeacher()) return <BookOpen className="h-4 w-4 text-blue-600" />;
    if (isStudent()) return <User className="h-4 w-4 text-green-600" />;
    return <User className="h-4 w-4 text-gray-600" />;
  };

  const getRoleColor = () => {
    if (isAdmin()) return 'bg-red-100 text-red-600';
    if (isTeacher()) return 'bg-blue-100 text-blue-600';
    if (isStudent()) return 'bg-green-100 text-green-600';
    return 'bg-gray-100 text-gray-600';
  };

  return (
    <nav className="bg-white shadow-sm border-b border-gray-200 px-4 py-3">
      <div className="flex items-center justify-between">
        {/* Left side */}
        <div className="flex items-center space-x-4">
          <button
            onClick={onToggleSidebar}
            className="p-2 rounded-md text-gray-600 hover:text-gray-900 hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-primary-500 lg:hidden"
          >
            <Menu className="h-6 w-6" />
          </button>
          
          <div className="flex items-center space-x-3">
            <div className="flex items-center justify-center w-8 h-8 bg-primary-600 rounded-lg">
              <School className="h-5 w-5 text-white" />
            </div>
            <div className="hidden sm:block">
              <h1 className="text-xl font-bold text-gray-900">{t('app.name')}</h1>
              <p className="text-sm text-gray-500">Management System</p>
            </div>
          </div>
        </div>

        {/* Right side */}
        <div className="flex items-center space-x-4">
          {/* Language Selector */}
          <LanguageSelector className="hidden sm:block" />
          
          {/* Role Badge */}
          <div className={`hidden md:flex items-center space-x-1 px-2 py-1 rounded-full text-xs font-medium ${getRoleColor()}`}>
            {getRoleIcon()}
            <span className="capitalize">{user?.role?.toLowerCase()}</span>
          </div>

          {/* Notifications - Only for Admin and Teachers */}
          {(isAdmin() || isTeacher()) && (
            <button className="p-2 text-gray-600 hover:text-gray-900 hover:bg-gray-100 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500">
              <Bell className="h-6 w-6" />
            </button>
          )}

          {/* User menu */}
          <div className="relative">
            <button
              onClick={() => setShowUserMenu(!showUserMenu)}
              className="flex items-center space-x-3 p-2 text-gray-600 hover:text-gray-900 hover:bg-gray-100 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500"
            >
              <div className={`flex items-center justify-center w-8 h-8 rounded-full ${getRoleColor()}`}>
                {getRoleIcon()}
              </div>
              <div className="hidden md:block text-left">
                <p className="text-sm font-medium text-gray-900">
                  {user?.fullName || user?.username || 'User'}
                </p>
                <p className="text-xs text-gray-500">
                  {user?.email || `${user?.role?.toLowerCase()}@school.edu`}
                </p>
              </div>
            </button>

            {/* User dropdown */}
            {showUserMenu && (
              <div className="absolute right-0 mt-2 w-56 bg-white rounded-md shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none z-50">
                <div className="py-1">
                  {/* User Info */}
                  <div className="px-4 py-3 border-b border-gray-100">
                    <p className="text-sm font-medium text-gray-900">
                      {user?.fullName || user?.username}
                    </p>
                    <p className="text-sm text-gray-500">{user?.email}</p>
                    <div className={`inline-flex items-center space-x-1 px-2 py-1 rounded-full text-xs font-medium mt-2 ${getRoleColor()}`}>
                      {getRoleIcon()}
                      <span className="capitalize">{user?.role?.toLowerCase()}</span>
                    </div>
                  </div>
                  
                  {/* Profile Link */}
                  <button
                    onClick={() => setShowUserMenu(false)}
                    className="flex items-center w-full px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                  >
                    <User className="h-4 w-4 mr-3" />
                    {t('profile.view')}
                  </button>
                  
                  {/* Settings - Admin only */}
                  {isAdmin() && (
                    <button
                      onClick={() => setShowUserMenu(false)}
                      className="flex items-center w-full px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                    >
                      <Settings className="h-4 w-4 mr-3" />
                      {t('nav.settings')}
                    </button>
                  )}
                  
                  {/* Language selector for mobile */}
                  <div className="sm:hidden px-4 py-2">
                    <LanguageSelector />
                  </div>
                  
                  {/* Divider */}
                  <div className="border-t border-gray-100 my-1"></div>
                  
                  {/* Logout */}
                  <button
                    onClick={handleLogout}
                    className="flex items-center w-full px-4 py-2 text-sm text-red-700 hover:bg-red-50"
                  >
                    <LogOut className="h-4 w-4 mr-3" />
                    {t('auth.logout')}
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
