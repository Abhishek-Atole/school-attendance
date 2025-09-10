import { NavLink } from 'react-router-dom';
import { 
  LayoutDashboard, 
  Users, 
  UserCheck, 
  ClipboardList, 
  FileText,
  X,
  Calendar,
  BarChart3,
  Settings,
  Shield,
  Bell
} from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';

const Sidebar = ({ isOpen, onClose }) => {
  const { user, isAdmin, isTeacher, isStudent } = useAuth();

  // Define navigation items based on roles
  const getNavigationItems = () => {
    const commonItems = [
      {
        name: 'Dashboard',
        href: '/dashboard',
        icon: LayoutDashboard,
        roles: ['ADMIN', 'TEACHER', 'STUDENT']
      }
    ];

    const adminItems = [
      {
        name: 'Students',
        href: '/students',
        icon: Users,
        roles: ['ADMIN', 'TEACHER']
      },
      {
        name: 'Teachers',
        href: '/teachers',
        icon: UserCheck,
        roles: ['ADMIN']
      },
      {
        name: 'Attendance',
        href: '/attendance',
        icon: ClipboardList,
        roles: ['ADMIN', 'TEACHER']
      },
      {
        name: 'Reports',
        href: '/reports',
        icon: FileText,
        roles: ['ADMIN', 'TEACHER']
      },
      {
        name: 'Analytics',
        href: '/analytics',
        icon: BarChart3,
        roles: ['ADMIN', 'TEACHER']
      },
      {
        name: 'Notifications',
        href: '/notifications',
        icon: Bell,
        roles: ['ADMIN', 'TEACHER']
      }
    ];

    const studentItems = [
      {
        name: 'My Attendance',
        href: '/my-attendance',
        icon: Calendar,
        roles: ['STUDENT']
      }
    ];

    // Combine all items
    const allItems = [...commonItems, ...adminItems, ...studentItems];

    // Filter items based on user role
    return allItems.filter(item => item.roles.includes(user?.role));
  };

  const navigation = getNavigationItems();

  const sidebarClasses = `
    fixed inset-y-0 left-0 z-30 w-64 bg-white shadow-lg transform transition-transform duration-300 ease-in-out
    ${isOpen ? 'translate-x-0' : '-translate-x-full'}
    lg:translate-x-0 lg:static lg:inset-0
  `;

  const getRoleInfo = () => {
    if (isAdmin()) return { name: 'Administrator', icon: Shield, color: 'text-red-600 bg-red-50' };
    if (isTeacher()) return { name: 'Teacher', icon: UserCheck, color: 'text-blue-600 bg-blue-50' };
    if (isStudent()) return { name: 'Student', icon: Users, color: 'text-green-600 bg-green-50' };
    return { name: 'User', icon: Users, color: 'text-gray-600 bg-gray-50' };
  };

  const roleInfo = getRoleInfo();

  return (
    <div className={sidebarClasses}>
      {/* Sidebar header */}
      <div className="flex items-center justify-between p-4 border-b border-gray-200 lg:hidden">
        <h2 className="text-lg font-semibold text-gray-900">Menu</h2>
        <button
          onClick={onClose}
          className="p-2 rounded-md text-gray-600 hover:text-gray-900 hover:bg-gray-100"
        >
          <X className="h-5 w-5" />
        </button>
      </div>

      {/* User info section */}
      <div className="p-4 border-b border-gray-200">
        <div className="flex items-center space-x-3">
          <div className={`flex items-center justify-center w-10 h-10 rounded-full ${roleInfo.color}`}>
            <roleInfo.icon className="h-5 w-5" />
          </div>
          <div className="flex-1 min-w-0">
            <p className="text-sm font-medium text-gray-900 truncate">
              {user?.fullName || user?.username}
            </p>
            <p className="text-xs text-gray-500 truncate">
              {roleInfo.name}
            </p>
          </div>
        </div>
      </div>

      {/* Navigation */}
      <nav className="mt-4 flex-1">
        <div className="px-4 space-y-1">
          {navigation.map((item) => (
            <NavLink
              key={item.name}
              to={item.href}
              onClick={() => {
                // Close mobile sidebar when navigating
                if (window.innerWidth < 1024) {
                  onClose();
                }
              }}
              className={({ isActive }) =>
                `flex items-center px-4 py-3 text-sm font-medium rounded-lg transition-colors duration-150 ${
                  isActive
                    ? 'bg-primary-50 text-primary-700 border-r-2 border-primary-700'
                    : 'text-gray-700 hover:bg-gray-50 hover:text-gray-900'
                }`
              }
            >
              <item.icon className="mr-3 h-5 w-5" />
              {item.name}
            </NavLink>
          ))}
        </div>

        {/* Role-specific additional sections */}
        {isAdmin() && (
          <div className="mt-8 px-4">
            <h3 className="px-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">
              Administration
            </h3>
            <div className="mt-2 space-y-1">
              <button className="flex items-center w-full px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 rounded-lg">
                <Settings className="mr-3 h-4 w-4" />
                System Settings
              </button>
              <button className="flex items-center w-full px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 rounded-lg">
                <BarChart3 className="mr-3 h-4 w-4" />
                Analytics
              </button>
            </div>
          </div>
        )}
      </nav>

      {/* Footer info */}
      <div className="p-4 border-t border-gray-200">
        <div className="text-xs text-gray-500 text-center">
          <p>School Attendance System</p>
          <p>Version 1.0.0</p>
        </div>
      </div>

      {/* Mobile overlay */}
      {isOpen && (
        <div 
          className="fixed inset-0 bg-black bg-opacity-50 z-20 lg:hidden"
          onClick={onClose}
        />
      )}
    </div>
  );
};

export default Sidebar;
