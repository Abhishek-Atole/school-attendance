import { useState } from 'react';
import { 
  Download, 
  FileText, 
  Calendar, 
  Filter,
  Search,
  BarChart3,
  TrendingUp,
  Users,
  Clock
} from 'lucide-react';
import { reportsAPI } from '../services/api';

const Reports = () => {
  const [filters, setFilters] = useState({
    startDate: new Date(new Date().setMonth(new Date().getMonth() - 1)).toISOString().split('T')[0],
    endDate: new Date().toISOString().split('T')[0],
    class: '',
    section: '',
    reportType: 'COMPREHENSIVE'
  });
  
  const [isGenerating, setIsGenerating] = useState({
    csv: false,
    excel: false,
    pdf: false
  });

  const handleFilterChange = (key, value) => {
    setFilters(prev => ({ ...prev, [key]: value }));
  };

  const handleExport = async (format) => {
    setIsGenerating(prev => ({ ...prev, [format]: true }));
    
    try {
      const params = {
        schoolId: 1, // Default school ID
        startDate: filters.startDate,
        endDate: filters.endDate,
        reportType: filters.reportType
      };

      if (filters.class) params.class = filters.class;
      if (filters.section) params.section = filters.section;

      let response;
      let filename;
      let mimeType;

      switch (format) {
        case 'csv':
          // Simulate API call for now
          console.log('Exporting CSV with params:', params);
          filename = `attendance_report_${filters.startDate}_${filters.endDate}.csv`;
          
          // Create mock CSV content
          const csvContent = `Student Name,GR No,Roll No,Standard,Section,Total Days,Present Days,Absent Days,Attendance %
John Doe,GR001,1,10,A,30,28,2,93.33%
Jane Smith,GR002,2,10,A,30,29,1,96.67%
Alice Johnson,GR003,3,10,A,30,27,3,90.00%`;
          
          const csvBlob = new Blob([csvContent], { type: 'text/csv' });
          downloadFile(csvBlob, filename);
          break;

        case 'excel':
          console.log('Exporting Excel with params:', params);
          filename = `attendance_report_${filters.startDate}_${filters.endDate}.xlsx`;
          // For demo, we'll create a mock Excel file
          const excelContent = csvContent; // In real implementation, this would be from the API
          const excelBlob = new Blob([excelContent], { 
            type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' 
          });
          downloadFile(excelBlob, filename);
          break;

        case 'pdf':
          console.log('Exporting PDF with params:', params);
          filename = `attendance_report_${filters.startDate}_${filters.endDate}.pdf`;
          // For demo, we'll create a mock PDF file
          const pdfContent = `Attendance Report\n\nPeriod: ${filters.startDate} to ${filters.endDate}\n\n${csvContent}`;
          const pdfBlob = new Blob([pdfContent], { type: 'application/pdf' });
          downloadFile(pdfBlob, filename);
          break;
      }

      // Show success message
      alert(`${format.toUpperCase()} report generated successfully!`);
    } catch (error) {
      console.error(`Error generating ${format} report:`, error);
      alert(`Failed to generate ${format.toUpperCase()} report. Please try again.`);
    } finally {
      setIsGenerating(prev => ({ ...prev, [format]: false }));
    }
  };

  const downloadFile = (blob, filename) => {
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  };

  // Mock statistics data
  const reportStats = {
    totalStudents: 450,
    totalClasses: 12,
    averageAttendance: 89.5,
    reportsPeriod: `${filters.startDate} to ${filters.endDate}`
  };

  const quickReports = [
    {
      title: 'Daily Attendance',
      description: 'Generate attendance report for a specific day',
      icon: Calendar,
      color: 'blue',
      action: () => {
        const today = new Date().toISOString().split('T')[0];
        setFilters(prev => ({ ...prev, startDate: today, endDate: today }));
      }
    },
    {
      title: 'Weekly Summary',
      description: 'Get attendance summary for the current week',
      icon: BarChart3,
      color: 'green',
      action: () => {
        const today = new Date();
        const weekStart = new Date(today.setDate(today.getDate() - today.getDay()));
        const weekEnd = new Date(today.setDate(today.getDate() - today.getDay() + 6));
        setFilters(prev => ({
          ...prev,
          startDate: weekStart.toISOString().split('T')[0],
          endDate: weekEnd.toISOString().split('T')[0]
        }));
      }
    },
    {
      title: 'Monthly Report',
      description: 'Complete attendance report for the current month',
      icon: TrendingUp,
      color: 'purple',
      action: () => {
        const today = new Date();
        const monthStart = new Date(today.getFullYear(), today.getMonth(), 1);
        const monthEnd = new Date(today.getFullYear(), today.getMonth() + 1, 0);
        setFilters(prev => ({
          ...prev,
          startDate: monthStart.toISOString().split('T')[0],
          endDate: monthEnd.toISOString().split('T')[0]
        }));
      }
    },
    {
      title: 'Class-wise Report',
      description: 'Generate reports grouped by class and section',
      icon: Users,
      color: 'yellow',
      action: () => {
        // This would open a class selection modal in real implementation
        alert('Class-wise report generation coming soon!');
      }
    }
  ];

  const ExportButton = ({ format, title, description, icon: Icon, disabled }) => (
    <button
      onClick={() => handleExport(format)}
      disabled={disabled || isGenerating[format]}
      className="flex items-center justify-between w-full p-4 bg-white border border-gray-200 rounded-lg hover:bg-gray-50 hover:border-gray-300 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
    >
      <div className="flex items-center space-x-3">
        <div className="p-2 bg-primary-100 rounded-lg">
          <Icon className="h-5 w-5 text-primary-600" />
        </div>
        <div className="text-left">
          <h3 className="font-medium text-gray-900">{title}</h3>
          <p className="text-sm text-gray-500">{description}</p>
        </div>
      </div>
      <div className="flex items-center">
        {isGenerating[format] ? (
          <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-primary-600"></div>
        ) : (
          <Download className="h-5 w-5 text-gray-400" />
        )}
      </div>
    </button>
  );

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Reports</h1>
          <p className="text-gray-600 mt-1">Generate and export attendance reports</p>
        </div>
      </div>

      {/* Statistics Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div className="flex items-center">
            <Users className="h-8 w-8 text-blue-600" />
            <div className="ml-3">
              <p className="text-sm font-medium text-gray-600">Total Students</p>
              <p className="text-2xl font-bold text-gray-900">{reportStats.totalStudents}</p>
            </div>
          </div>
        </div>
        
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div className="flex items-center">
            <BarChart3 className="h-8 w-8 text-green-600" />
            <div className="ml-3">
              <p className="text-sm font-medium text-gray-600">Total Classes</p>
              <p className="text-2xl font-bold text-gray-900">{reportStats.totalClasses}</p>
            </div>
          </div>
        </div>
        
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div className="flex items-center">
            <TrendingUp className="h-8 w-8 text-purple-600" />
            <div className="ml-3">
              <p className="text-sm font-medium text-gray-600">Avg. Attendance</p>
              <p className="text-2xl font-bold text-gray-900">{reportStats.averageAttendance}%</p>
            </div>
          </div>
        </div>
        
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div className="flex items-center">
            <Clock className="h-8 w-8 text-yellow-600" />
            <div className="ml-3">
              <p className="text-sm font-medium text-gray-600">Report Period</p>
              <p className="text-sm font-bold text-gray-900">{reportStats.reportsPeriod}</p>
            </div>
          </div>
        </div>
      </div>

      {/* Quick Reports */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Quick Reports</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          {quickReports.map((report, index) => (
            <button
              key={index}
              onClick={report.action}
              className="flex flex-col items-center p-4 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors"
            >
              <div className={`p-3 rounded-full mb-3 ${
                report.color === 'blue' ? 'bg-blue-100' :
                report.color === 'green' ? 'bg-green-100' :
                report.color === 'purple' ? 'bg-purple-100' :
                'bg-yellow-100'
              }`}>
                <report.icon className={`h-6 w-6 ${
                  report.color === 'blue' ? 'text-blue-600' :
                  report.color === 'green' ? 'text-green-600' :
                  report.color === 'purple' ? 'text-purple-600' :
                  'text-yellow-600'
                }`} />
              </div>
              <h3 className="font-medium text-gray-900 text-center">{report.title}</h3>
              <p className="text-sm text-gray-500 text-center mt-1">{report.description}</p>
            </button>
          ))}
        </div>
      </div>

      {/* Filters */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Report Filters</h2>
        <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-5 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Start Date</label>
            <input
              type="date"
              value={filters.startDate}
              onChange={(e) => handleFilterChange('startDate', e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-primary-500 focus:border-primary-500"
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">End Date</label>
            <input
              type="date"
              value={filters.endDate}
              onChange={(e) => handleFilterChange('endDate', e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-primary-500 focus:border-primary-500"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Class</label>
            <select
              value={filters.class}
              onChange={(e) => handleFilterChange('class', e.target.value)}
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
              value={filters.section}
              onChange={(e) => handleFilterChange('section', e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-primary-500 focus:border-primary-500"
            >
              <option value="">All Sections</option>
              {['A', 'B', 'C', 'D'].map(section => (
                <option key={section} value={section}>{section}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Report Type</label>
            <select
              value={filters.reportType}
              onChange={(e) => handleFilterChange('reportType', e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-primary-500 focus:border-primary-500"
            >
              <option value="COMPREHENSIVE">Comprehensive</option>
              <option value="SUMMARY">Summary</option>
              <option value="DETAILED">Detailed</option>
            </select>
          </div>
        </div>
      </div>

      {/* Export Options */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Export Reports</h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <ExportButton
            format="csv"
            title="Export to CSV"
            description="Download as comma-separated values file"
            icon={FileText}
          />
          
          <ExportButton
            format="excel"
            title="Export to Excel"
            description="Download as Microsoft Excel spreadsheet"
            icon={BarChart3}
          />
          
          <ExportButton
            format="pdf"
            title="Export to PDF"
            description="Download as portable document format"
            icon={FileText}
          />
        </div>
      </div>

      {/* Recent Reports */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Recent Reports</h2>
        <div className="space-y-3">
          <div className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
            <div className="flex items-center space-x-3">
              <FileText className="h-5 w-5 text-gray-400" />
              <div>
                <p className="font-medium text-gray-900">Monthly Attendance Report - September 2025</p>
                <p className="text-sm text-gray-500">Generated on Sep 10, 2025 at 2:30 PM</p>
              </div>
            </div>
            <div className="flex items-center space-x-2">
              <span className="inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-green-100 text-green-800">
                Completed
              </span>
              <button className="p-1 text-gray-400 hover:text-gray-600">
                <Download className="h-4 w-4" />
              </button>
            </div>
          </div>

          <div className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
            <div className="flex items-center space-x-3">
              <BarChart3 className="h-5 w-5 text-gray-400" />
              <div>
                <p className="font-medium text-gray-900">Class 10-A Weekly Summary</p>
                <p className="text-sm text-gray-500">Generated on Sep 9, 2025 at 10:15 AM</p>
              </div>
            </div>
            <div className="flex items-center space-x-2">
              <span className="inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-green-100 text-green-800">
                Completed
              </span>
              <button className="p-1 text-gray-400 hover:text-gray-600">
                <Download className="h-4 w-4" />
              </button>
            </div>
          </div>

          <div className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
            <div className="flex items-center space-x-3">
              <TrendingUp className="h-5 w-5 text-gray-400" />
              <div>
                <p className="font-medium text-gray-900">Daily Attendance - Sep 8, 2025</p>
                <p className="text-sm text-gray-500">Generated on Sep 8, 2025 at 5:45 PM</p>
              </div>
            </div>
            <div className="flex items-center space-x-2">
              <span className="inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-green-100 text-green-800">
                Completed
              </span>
              <button className="p-1 text-gray-400 hover:text-gray-600">
                <Download className="h-4 w-4" />
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Reports;
