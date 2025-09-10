import { useState, useEffect } from 'react';
import { 
  Users, 
  UserCheck, 
  ClipboardList, 
  TrendingUp,
  Calendar,
  Award,
  AlertCircle,
  RefreshCw
} from 'lucide-react';
import { 
  LineChart, 
  Line, 
  AreaChart, 
  Area, 
  BarChart, 
  Bar, 
  PieChart, 
  Pie, 
  Cell,
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip, 
  Legend, 
  ResponsiveContainer 
} from 'recharts';

const Dashboard = () => {
  const [stats, setStats] = useState({
    totalStudents: 450,
    totalTeachers: 25,
    todayAttendance: 92.5,
    weeklyAverage: 89.2
  });

  const [attendanceTrend, setAttendanceTrend] = useState([
    { date: '2025-09-04', attendance: 88.5 },
    { date: '2025-09-05', attendance: 91.2 },
    { date: '2025-09-06', attendance: 87.8 },
    { date: '2025-09-07', attendance: 94.1 },
    { date: '2025-09-08', attendance: 89.6 },
    { date: '2025-09-09', attendance: 92.3 },
    { date: '2025-09-10', attendance: 92.5 }
  ]);

  const [classWiseData, setClassWiseData] = useState([
    { class: 'Class 1', present: 45, absent: 5, percentage: 90 },
    { class: 'Class 2', present: 42, absent: 3, percentage: 93.3 },
    { class: 'Class 3', present: 38, absent: 7, percentage: 84.4 },
    { class: 'Class 4', present: 41, absent: 4, percentage: 91.1 },
    { class: 'Class 5', present: 44, absent: 1, percentage: 97.8 },
  ]);

  const [genderDistribution, setGenderDistribution] = useState([
    { name: 'Male', value: 260, color: '#3B82F6' },
    { name: 'Female', value: 190, color: '#EC4899' }
  ]);

  const StatCard = ({ title, value, icon: Icon, change, changeType }) => (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
      <div className="flex items-center justify-between">
        <div>
          <p className="text-sm font-medium text-gray-600">{title}</p>
          <p className="text-3xl font-bold text-gray-900 mt-2">{value}</p>
          {change && (
            <p className={`text-sm mt-2 flex items-center ${
              changeType === 'positive' ? 'text-green-600' : 'text-red-600'
            }`}>
              <TrendingUp className="h-4 w-4 mr-1" />
              {change}
            </p>
          )}
        </div>
        <div className={`p-3 rounded-full ${
          Icon === Users ? 'bg-blue-50' :
          Icon === UserCheck ? 'bg-green-50' :
          Icon === ClipboardList ? 'bg-purple-50' :
          'bg-yellow-50'
        }`}>
          <Icon className={`h-6 w-6 ${
            Icon === Users ? 'text-blue-600' :
            Icon === UserCheck ? 'text-green-600' :
            Icon === ClipboardList ? 'text-purple-600' :
            'text-yellow-600'
          }`} />
        </div>
      </div>
    </div>
  );

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
          <p className="text-gray-600 mt-1">
            Welcome back! Here's what's happening at your school today.
          </p>
        </div>
        <div className="mt-4 sm:mt-0 flex items-center space-x-2">
          <button className="flex items-center px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50">
            <RefreshCw className="h-4 w-4 mr-2" />
            Refresh
          </button>
        </div>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatCard
          title="Total Students"
          value={stats.totalStudents}
          icon={Users}
          change="+12 this month"
          changeType="positive"
        />
        <StatCard
          title="Total Teachers"
          value={stats.totalTeachers}
          icon={UserCheck}
          change="+2 this month"
          changeType="positive"
        />
        <StatCard
          title="Today's Attendance"
          value={`${stats.todayAttendance}%`}
          icon={ClipboardList}
          change="+3.2% from yesterday"
          changeType="positive"
        />
        <StatCard
          title="Weekly Average"
          value={`${stats.weeklyAverage}%`}
          icon={Award}
          change="+1.5% from last week"
          changeType="positive"
        />
      </div>

      {/* Charts Row */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Attendance Trend */}
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Attendance Trend (Last 7 Days)</h3>
          <ResponsiveContainer width="100%" height={300}>
            <AreaChart data={attendanceTrend}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis 
                dataKey="date" 
                tickFormatter={(date) => new Date(date).toLocaleDateString('en-US', { month: 'short', day: 'numeric' })}
              />
              <YAxis domain={[80, 100]} />
              <Tooltip 
                labelFormatter={(date) => new Date(date).toLocaleDateString()}
                formatter={(value) => [`${value}%`, 'Attendance']}
              />
              <Area 
                type="monotone" 
                dataKey="attendance" 
                stroke="#3B82F6" 
                fill="#93C5FD" 
                strokeWidth={2}
              />
            </AreaChart>
          </ResponsiveContainer>
        </div>

        {/* Gender Distribution */}
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Student Gender Distribution</h3>
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie
                data={genderDistribution}
                cx="50%"
                cy="50%"
                innerRadius={60}
                outerRadius={100}
                paddingAngle={5}
                dataKey="value"
              >
                {genderDistribution.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={entry.color} />
                ))}
              </Pie>
              <Tooltip formatter={(value) => [value, 'Students']} />
              <Legend />
            </PieChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Class-wise Attendance */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">Class-wise Attendance Today</h3>
        <div className="overflow-x-auto">
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={classWiseData} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="class" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Bar dataKey="present" fill="#10B981" name="Present" />
              <Bar dataKey="absent" fill="#EF4444" name="Absent" />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Quick Actions */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">Quick Actions</h3>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <button className="flex items-center justify-center p-4 bg-blue-50 rounded-lg hover:bg-blue-100 transition-colors">
            <ClipboardList className="h-5 w-5 text-blue-600 mr-2" />
            <span className="text-sm font-medium text-blue-900">Mark Attendance</span>
          </button>
          <button className="flex items-center justify-center p-4 bg-green-50 rounded-lg hover:bg-green-100 transition-colors">
            <Users className="h-5 w-5 text-green-600 mr-2" />
            <span className="text-sm font-medium text-green-900">Add Student</span>
          </button>
          <button className="flex items-center justify-center p-4 bg-purple-50 rounded-lg hover:bg-purple-100 transition-colors">
            <UserCheck className="h-5 w-5 text-purple-600 mr-2" />
            <span className="text-sm font-medium text-purple-900">Add Teacher</span>
          </button>
          <button className="flex items-center justify-center p-4 bg-yellow-50 rounded-lg hover:bg-yellow-100 transition-colors">
            <Calendar className="h-5 w-5 text-yellow-600 mr-2" />
            <span className="text-sm font-medium text-yellow-900">View Reports</span>
          </button>
        </div>
      </div>

      {/* Recent Activity */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">Recent Activity</h3>
        <div className="space-y-4">
          <div className="flex items-center p-3 bg-green-50 rounded-lg">
            <div className="flex-shrink-0">
              <div className="w-2 h-2 bg-green-400 rounded-full"></div>
            </div>
            <div className="ml-3">
              <p className="text-sm text-gray-900">Attendance marked for Class 5A</p>
              <p className="text-xs text-gray-500">2 minutes ago</p>
            </div>
          </div>
          <div className="flex items-center p-3 bg-blue-50 rounded-lg">
            <div className="flex-shrink-0">
              <div className="w-2 h-2 bg-blue-400 rounded-full"></div>
            </div>
            <div className="ml-3">
              <p className="text-sm text-gray-900">New student John Doe added to Class 3B</p>
              <p className="text-xs text-gray-500">1 hour ago</p>
            </div>
          </div>
          <div className="flex items-center p-3 bg-yellow-50 rounded-lg">
            <div className="flex-shrink-0">
              <div className="w-2 h-2 bg-yellow-400 rounded-full"></div>
            </div>
            <div className="ml-3">
              <p className="text-sm text-gray-900">Monthly attendance report generated</p>
              <p className="text-xs text-gray-500">3 hours ago</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
