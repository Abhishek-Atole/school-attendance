import React, { useState } from 'react';
import { 
  BarChart3, 
  Users, 
  TrendingUp, 
  Calendar,
  PieChart,
  Activity
} from 'lucide-react';
import {
  LineChart,
  Line,
  BarChart,
  Bar,
  PieChart as RechartsPieChart,
  Cell,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer
} from 'recharts';

// Mock data for demonstration
const mockAttendanceTrends = [
  { date: '2024-01-01', presentCount: 95, absentCount: 15, total: 110 },
  { date: '2024-01-02', presentCount: 98, absentCount: 12, total: 110 },
  { date: '2024-01-03', presentCount: 92, absentCount: 18, total: 110 },
  { date: '2024-01-04', presentCount: 96, absentCount: 14, total: 110 },
  { date: '2024-01-05', presentCount: 89, absentCount: 21, total: 110 },
  { date: '2024-01-06', presentCount: 94, absentCount: 16, total: 110 },
  { date: '2024-01-07', presentCount: 97, absentCount: 13, total: 110 },
];

const mockGenderRatio = [
  { name: 'Boys Present', value: 52, color: '#3B82F6' },
  { name: 'Girls Present', value: 45, color: '#EC4899' },
  { name: 'Boys Absent', value: 8, color: '#6B7280' },
  { name: 'Girls Absent', value: 5, color: '#9CA3AF' },
];

const mockClassPerformance = [
  { class: 'Class 1', attendance: 95.2 },
  { class: 'Class 2', attendance: 87.4 },
  { class: 'Class 3', attendance: 91.8 },
  { class: 'Class 4', attendance: 89.6 },
  { class: 'Class 5', attendance: 93.1 },
  { class: 'Class 6', attendance: 88.9 },
];

const mockTopAbsentees = [
  { name: 'John Doe', class: 'Class 5', absentDays: 12 },
  { name: 'Jane Smith', class: 'Class 3', absentDays: 10 },
  { name: 'Mike Johnson', class: 'Class 4', absentDays: 9 },
  { name: 'Sarah Wilson', class: 'Class 2', absentDays: 8 },
  { name: 'David Brown', class: 'Class 6', absentDays: 7 },
];

const mockDashboardStats = {
  totalStudents: 110,
  presentToday: 97,
  absentToday: 13,
  attendanceRate: 88.2
};

const StatCard = ({ title, value, icon: Icon, color }) => (
  <div className="bg-white rounded-lg shadow-md p-6">
    <div className="flex items-center justify-between">
      <div>
        <p className="text-sm font-medium text-gray-600">{title}</p>
        <p className="text-2xl font-bold text-gray-900">{value}</p>
      </div>
      <div className={`p-3 rounded-full bg-${color}-100`}>
        <Icon className={`h-6 w-6 text-${color}-600`} />
      </div>
    </div>
  </div>
);

const Analytics = () => {
  const [dateRange, setDateRange] = useState('7days');
  const [selectedClass, setSelectedClass] = useState('all');

  return (
    <div className="p-6 bg-gray-50 min-h-screen">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">Analytics Dashboard</h1>
          <p className="text-gray-600">Monitor attendance patterns and performance insights</p>
        </div>

        {/* Filters */}
        <div className="mb-6 flex flex-wrap gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Date Range</label>
            <select 
              value={dateRange} 
              onChange={(e) => setDateRange(e.target.value)}
              className="border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="7days">Last 7 Days</option>
              <option value="30days">Last 30 Days</option>
              <option value="90days">Last 3 Months</option>
              <option value="1year">Last Year</option>
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Class</label>
            <select 
              value={selectedClass} 
              onChange={(e) => setSelectedClass(e.target.value)}
              className="border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="all">All Classes</option>
              <option value="class1">Class 1</option>
              <option value="class2">Class 2</option>
              <option value="class3">Class 3</option>
              <option value="class4">Class 4</option>
              <option value="class5">Class 5</option>
              <option value="class6">Class 6</option>
            </select>
          </div>
        </div>

        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          <StatCard
            title="Total Students"
            value={mockDashboardStats.totalStudents}
            icon={Users}
            color="blue"
          />
          <StatCard
            title="Present Today"
            value={mockDashboardStats.presentToday}
            icon={Activity}
            color="green"
          />
          <StatCard
            title="Absent Today"
            value={mockDashboardStats.absentToday}
            icon={Calendar}
            color="red"
          />
          <StatCard
            title="Attendance Rate"
            value={`${mockDashboardStats.attendanceRate}%`}
            icon={TrendingUp}
            color="purple"
          />
        </div>

        {/* Charts Grid */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-8">
          {/* Attendance Trends */}
          <div className="bg-white rounded-lg shadow-md p-6">
            <h3 className="text-lg font-semibold mb-4 flex items-center">
              <TrendingUp className="mr-2 h-5 w-5 text-blue-600" />
              Attendance Trends
            </h3>
            <ResponsiveContainer width="100%" height={300}>
              <LineChart data={mockAttendanceTrends}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis 
                  dataKey="date" 
                  tickFormatter={(date) => new Date(date).toLocaleDateString()}
                />
                <YAxis />
                <Tooltip 
                  labelFormatter={(date) => new Date(date).toLocaleDateString()}
                />
                <Legend />
                <Line 
                  type="monotone" 
                  dataKey="presentCount" 
                  stroke="#10B981" 
                  strokeWidth={2}
                  name="Present"
                />
                <Line 
                  type="monotone" 
                  dataKey="absentCount" 
                  stroke="#EF4444" 
                  strokeWidth={2}
                  name="Absent"
                />
              </LineChart>
            </ResponsiveContainer>
          </div>

          {/* Gender Ratio */}
          <div className="bg-white rounded-lg shadow-md p-6">
            <h3 className="text-lg font-semibold mb-4 flex items-center">
              <PieChart className="mr-2 h-5 w-5 text-purple-600" />
              Gender Distribution
            </h3>
            <ResponsiveContainer width="100%" height={300}>
              <RechartsPieChart>
                <Pie
                  data={mockGenderRatio}
                  cx="50%"
                  cy="50%"
                  outerRadius={80}
                  fill="#8884d8"
                  dataKey="value"
                  label={({ name, value }) => `${name}: ${value}`}
                >
                  {mockGenderRatio.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={entry.color} />
                  ))}
                </Pie>
                <Tooltip />
              </RechartsPieChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Bottom Section */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {/* Class Performance */}
          <div className="bg-white rounded-lg shadow-md p-6">
            <h3 className="text-lg font-semibold mb-4 flex items-center">
              <BarChart3 className="mr-2 h-5 w-5 text-green-600" />
              Class Performance
            </h3>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={mockClassPerformance}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="class" />
                <YAxis domain={[0, 100]} />
                <Tooltip formatter={(value) => [`${value}%`, 'Attendance Rate']} />
                <Bar dataKey="attendance" fill="#3B82F6" />
              </BarChart>
            </ResponsiveContainer>
          </div>

          {/* Top Absentees */}
          <div className="bg-white rounded-lg shadow-md p-6">
            <h3 className="text-lg font-semibold mb-4 flex items-center">
              <Users className="mr-2 h-5 w-5 text-red-600" />
              Top Absentees
            </h3>
            <div className="space-y-3">
              {mockTopAbsentees.map((student, index) => (
                <div key={index} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                  <div>
                    <p className="font-medium text-gray-900">{student.name}</p>
                    <p className="text-sm text-gray-600">{student.class}</p>
                  </div>
                  <div className="text-right">
                    <p className="font-semibold text-red-600">{student.absentDays} days</p>
                    <p className="text-xs text-gray-500">absent</p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Analytics;
