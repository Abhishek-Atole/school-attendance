import { NavLink } from 'react-router-dom';
import { 
  LayoutDashboard, 
  Users, 
  UserCheck, 
  ClipboardList, 
  FileText,
  X 
} from 'lucide-react';

const Sidebar = ({ isOpen, onClose }) => {
  const navigation = [
    {
      name: 'Dashboard',
      href: '/dashboard',
      icon: LayoutDashboard,
    },
    {
      name: 'Students',
      href: '/students',
      icon: Users,
    },
    {
      name: 'Teachers',
      href: '/teachers',
      icon: UserCheck,
    },
    {
      name: 'Attendance',
      href: '/attendance',
      icon: ClipboardList,
    },
    {
      name: 'Reports',
      href: '/reports',
      icon: FileText,
    },
  ];

  const sidebarClasses = `
    fixed inset-y-0 left-0 z-30 w-64 bg-white shadow-lg transform transition-transform duration-300 ease-in-out
    ${isOpen ? 'translate-x-0' : '-translate-x-full'}
    lg:translate-x-0 lg:static lg:inset-0
  `;

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

      {/* Navigation */}
      <nav className="mt-4 lg:mt-8">
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
      </nav>

      {/* Footer info */}
      <div className="absolute bottom-0 left-0 right-0 p-4 border-t border-gray-200">
        <div className="text-xs text-gray-500 text-center">
          <p>School Attendance System</p>
          <p>Version 1.0.0</p>
        </div>
      </div>
    </div>
  );
};

export default Sidebar;
