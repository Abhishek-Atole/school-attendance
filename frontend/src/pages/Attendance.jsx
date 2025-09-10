import { useState, useEffect } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { 
  Calendar, 
  Check, 
  X, 
  Clock,
  Save,
  RefreshCw,
  Filter,
  Users,
  UserCheck,
  UserX,
  AlertCircle
} from 'lucide-react';
import { attendanceAPI, studentAPI } from '../services/api';

const Attendance = () => {
  const queryClient = useQueryClient();
  const [selectedDate, setSelectedDate] = useState(new Date().toISOString().split('T')[0]);
  const [selectedClass, setSelectedClass] = useState('');
  const [selectedSection, setSelectedSection] = useState('');
  const [attendanceData, setAttendanceData] = useState({});
  const [isHoliday, setIsHoliday] = useState(false);
  const [isSaving, setIsSaving] = useState(false);

  // Mock students data for attendance marking
  const mockStudents = [
    {
      id: 1,
      grNo: 'GR001',
      rollNo: '1',
      firstName: 'John',
      lastName: 'Doe',
      standard: '10',
      section: 'A',
      profilePhoto: null
    },
    {
      id: 2,
      grNo: 'GR002',
      rollNo: '2',
      firstName: 'Jane',
      lastName: 'Smith',
      standard: '10',
      section: 'A',
      profilePhoto: null
    },
    {
      id: 3,
      grNo: 'GR003',
      rollNo: '3',
      firstName: 'Alice',
      lastName: 'Johnson',
      standard: '10',
      section: 'A',
      profilePhoto: null
    },
    {
      id: 4,
      grNo: 'GR004',
      rollNo: '4',
      firstName: 'Bob',
      lastName: 'Wilson',
      standard: '10',
      section: 'A',
      profilePhoto: null
    },
    {
      id: 5,
      grNo: 'GR005',
      rollNo: '5',
      firstName: 'Charlie',
      lastName: 'Brown',
      standard: '10',
      section: 'A',
      profilePhoto: null
    }
  ];

  // Initialize attendance data when students or date changes
  useEffect(() => {
    if (mockStudents.length > 0) {
      const initialData = {};
      mockStudents.forEach(student => {
        // Default to present (true)
        initialData[student.id] = {
          status: 'PRESENT',
          remarks: ''
        };
      });
      setAttendanceData(initialData);
    }
  }, [selectedDate, selectedClass, selectedSection]);

  const handleAttendanceChange = (studentId, status) => {
    setAttendanceData(prev => ({
      ...prev,
      [studentId]: {
        ...prev[studentId],
        status: status
      }
    }));
  };

  const handleRemarksChange = (studentId, remarks) => {
    setAttendanceData(prev => ({
      ...prev,
      [studentId]: {
        ...prev[studentId],
        remarks: remarks
      }
    }));
  };

  const handleSaveAttendance = async () => {
    setIsSaving(true);
    try {
      // Prepare attendance data for API
      const attendanceRecords = Object.entries(attendanceData).map(([studentId, data]) => ({
        studentId: parseInt(studentId),
        status: data.status,
        remarks: data.remarks || null
      }));

      const payload = {
        schoolId: 1, // Default school ID
        date: selectedDate,
        isHoliday: isHoliday,
        attendanceRecords: attendanceRecords
      };

      console.log('Saving attendance:', payload);
      
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      alert('Attendance saved successfully!');
    } catch (error) {
      console.error('Error saving attendance:', error);
      alert('Failed to save attendance. Please try again.');
    } finally {
      setIsSaving(false);
    }
  };

  const handleMarkAllPresent = () => {
    const updatedData = {};
    mockStudents.forEach(student => {
      updatedData[student.id] = {
        status: 'PRESENT',
        remarks: ''
      };
    });
    setAttendanceData(updatedData);
  };

  const handleMarkAllAbsent = () => {
    const updatedData = {};
    mockStudents.forEach(student => {
      updatedData[student.id] = {
        status: 'ABSENT',
        remarks: ''
      };
    });
    setAttendanceData(updatedData);
  };

  // Calculate statistics
  const totalStudents = mockStudents.length;
  const presentCount = Object.values(attendanceData).filter(data => data.status === 'PRESENT').length;
  const absentCount = Object.values(attendanceData).filter(data => data.status === 'ABSENT').length;
  const lateCount = Object.values(attendanceData).filter(data => data.status === 'LATE').length;
  const attendancePercentage = totalStudents > 0 ? ((presentCount + lateCount) / totalStudents * 100).toFixed(1) : 0;

  const getStatusColor = (status) => {
    switch (status) {
      case 'PRESENT': return 'bg-green-100 text-green-800 border-green-200';
      case 'ABSENT': return 'bg-red-100 text-red-800 border-red-200';
      case 'LATE': return 'bg-yellow-100 text-yellow-800 border-yellow-200';
      case 'SICK_LEAVE': return 'bg-blue-100 text-blue-800 border-blue-200';
      default: return 'bg-gray-100 text-gray-800 border-gray-200';
    }
  };

  const AttendanceCard = ({ student }) => {
    const attendance = attendanceData[student.id] || { status: 'PRESENT', remarks: '' };
    
    return (
      <div className="bg-white rounded-lg border border-gray-200 p-4 hover:shadow-md transition-shadow">
        <div className="flex items-center justify-between mb-3">
          <div className="flex items-center space-x-3">
            <div className="w-12 h-12 bg-primary-100 rounded-full flex items-center justify-center">
              <span className="text-primary-600 font-semibold">
                {student.firstName.charAt(0)}{student.lastName.charAt(0)}
              </span>
            </div>
            <div>
              <h3 className="font-medium text-gray-900">{student.firstName} {student.lastName}</h3>
              <p className="text-sm text-gray-500">Roll: {student.rollNo} | GR: {student.grNo}</p>
            </div>
          </div>
          <div className="text-right">
            <span className="text-sm font-medium text-gray-900">Class {student.standard}-{student.section}</span>
          </div>
        </div>

        <div className="space-y-3">
          {/* Status buttons */}
          <div className="grid grid-cols-2 md:grid-cols-4 gap-2">
            <button
              onClick={() => handleAttendanceChange(student.id, 'PRESENT')}
              className={`flex items-center justify-center px-3 py-2 rounded-md text-sm font-medium border-2 transition-colors ${
                attendance.status === 'PRESENT' 
                  ? 'bg-green-100 text-green-800 border-green-300' 
                  : 'bg-white text-gray-700 border-gray-300 hover:bg-green-50'
              }`}
            >
              <Check className="h-4 w-4 mr-1" />
              Present
            </button>
            
            <button
              onClick={() => handleAttendanceChange(student.id, 'ABSENT')}
              className={`flex items-center justify-center px-3 py-2 rounded-md text-sm font-medium border-2 transition-colors ${
                attendance.status === 'ABSENT' 
                  ? 'bg-red-100 text-red-800 border-red-300' 
                  : 'bg-white text-gray-700 border-gray-300 hover:bg-red-50'
              }`}
            >
              <X className="h-4 w-4 mr-1" />
              Absent
            </button>
            
            <button
              onClick={() => handleAttendanceChange(student.id, 'LATE')}
              className={`flex items-center justify-center px-3 py-2 rounded-md text-sm font-medium border-2 transition-colors ${
                attendance.status === 'LATE' 
                  ? 'bg-yellow-100 text-yellow-800 border-yellow-300' 
                  : 'bg-white text-gray-700 border-gray-300 hover:bg-yellow-50'
              }`}
            >
              <Clock className="h-4 w-4 mr-1" />
              Late
            </button>
            
            <button
              onClick={() => handleAttendanceChange(student.id, 'SICK_LEAVE')}
              className={`flex items-center justify-center px-3 py-2 rounded-md text-sm font-medium border-2 transition-colors ${
                attendance.status === 'SICK_LEAVE' 
                  ? 'bg-blue-100 text-blue-800 border-blue-300' 
                  : 'bg-white text-gray-700 border-gray-300 hover:bg-blue-50'
              }`}
            >
              <AlertCircle className="h-4 w-4 mr-1" />
              Sick
            </button>
          </div>

          {/* Remarks */}
          {(attendance.status === 'ABSENT' || attendance.status === 'LATE' || attendance.status === 'SICK_LEAVE') && (
            <div>
              <input
                type="text"
                placeholder="Add remarks (optional)"
                value={attendance.remarks}
                onChange={(e) => handleRemarksChange(student.id, e.target.value)}
                className="w-full px-3 py-2 text-sm border border-gray-300 rounded-md focus:outline-none focus:ring-primary-500 focus:border-primary-500"
              />
            </div>
          )}
        </div>
      </div>
    );
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Mark Attendance</h1>
          <p className="text-gray-600 mt-1">Record daily attendance for students</p>
        </div>
        <div className="mt-4 sm:mt-0 flex items-center space-x-3">
          <button
            onClick={() => window.location.reload()}
            className="flex items-center px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50"
          >
            <RefreshCw className="h-4 w-4 mr-2" />
            Refresh
          </button>
        </div>
      </div>

      {/* Controls */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Date</label>
            <input
              type="date"
              value={selectedDate}
              onChange={(e) => setSelectedDate(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-primary-500 focus:border-primary-500"
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Class</label>
            <select
              value={selectedClass}
              onChange={(e) => setSelectedClass(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-primary-500 focus:border-primary-500"
            >
              <option value="">All Classes</option>
              {Array.from({ length: 12 }, (_, i) => (
                <option key={i + 1} value={i + 1}>Class {i + 1}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Section</label>
            <select
              value={selectedSection}
              onChange={(e) => setSelectedSection(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-primary-500 focus:border-primary-500"
            >
              <option value="">All Sections</option>
              {['A', 'B', 'C', 'D'].map(section => (
                <option key={section} value={section}>{section}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Holiday</label>
            <label className="flex items-center">
              <input
                type="checkbox"
                checked={isHoliday}
                onChange={(e) => setIsHoliday(e.target.checked)}
                className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
              />
              <span className="ml-2 text-sm text-gray-700">Mark as Holiday</span>
            </label>
          </div>
        </div>

        {/* Quick Actions */}
        <div className="flex flex-wrap gap-2">
          <button
            onClick={handleMarkAllPresent}
            className="flex items-center px-4 py-2 text-sm font-medium text-green-700 bg-green-100 rounded-lg hover:bg-green-200"
          >
            <UserCheck className="h-4 w-4 mr-2" />
            Mark All Present
          </button>
          <button
            onClick={handleMarkAllAbsent}
            className="flex items-center px-4 py-2 text-sm font-medium text-red-700 bg-red-100 rounded-lg hover:bg-red-200"
          >
            <UserX className="h-4 w-4 mr-2" />
            Mark All Absent
          </button>
        </div>
      </div>

      {/* Statistics */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
          <div className="flex items-center">
            <Users className="h-8 w-8 text-blue-600" />
            <div className="ml-3">
              <p className="text-sm font-medium text-gray-600">Total Students</p>
              <p className="text-2xl font-bold text-gray-900">{totalStudents}</p>
            </div>
          </div>
        </div>
        
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
          <div className="flex items-center">
            <UserCheck className="h-8 w-8 text-green-600" />
            <div className="ml-3">
              <p className="text-sm font-medium text-gray-600">Present</p>
              <p className="text-2xl font-bold text-gray-900">{presentCount}</p>
            </div>
          </div>
        </div>
        
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
          <div className="flex items-center">
            <UserX className="h-8 w-8 text-red-600" />
            <div className="ml-3">
              <p className="text-sm font-medium text-gray-600">Absent</p>
              <p className="text-2xl font-bold text-gray-900">{absentCount}</p>
            </div>
          </div>
        </div>
        
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
          <div className="flex items-center">
            <div className="w-8 h-8 bg-primary-600 rounded-full flex items-center justify-center">
              <span className="text-white font-bold text-sm">%</span>
            </div>
            <div className="ml-3">
              <p className="text-sm font-medium text-gray-600">Attendance</p>
              <p className="text-2xl font-bold text-gray-900">{attendancePercentage}%</p>
            </div>
          </div>
        </div>
      </div>

      {/* Students Grid */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-lg font-semibold text-gray-900">
            Students - Class 10-A ({mockStudents.length} students)
          </h2>
          <button
            onClick={handleSaveAttendance}
            disabled={isSaving}
            className="flex items-center px-6 py-2 text-sm font-medium text-white bg-primary-600 rounded-lg hover:bg-primary-700 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <Save className="h-4 w-4 mr-2" />
            {isSaving ? 'Saving...' : 'Save Attendance'}
          </button>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {mockStudents.map((student) => (
            <AttendanceCard key={student.id} student={student} />
          ))}
        </div>
      </div>
    </div>
  );
};

export default Attendance;
