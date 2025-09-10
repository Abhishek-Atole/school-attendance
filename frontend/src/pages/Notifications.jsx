import React, { useState, useEffect } from 'react';
import { Bell, Settings, Mail, Phone, Calendar, TrendingUp, AlertTriangle } from 'lucide-react';

const Notifications = () => {
  const [activeTab, setActiveTab] = useState('logs');
  const [notificationLogs, setNotificationLogs] = useState([]);
  const [notificationSettings, setNotificationSettings] = useState({
    emailEnabled: true,
    smsEnabled: true,
    dailyAbsenteeAlerts: true,
    lowAttendanceAlerts: true,
    holidayNotifications: true,
    attendanceThreshold: 75.0,
    notificationTime: '20:00'
  });
  const [stats, setStats] = useState({
    totalNotifications: 0,
    sentNotifications: 0,
    failedNotifications: 0,
    emailNotifications: 0,
    smsNotifications: 0,
    successRate: 0
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [testEmail, setTestEmail] = useState('');
  const [testPhone, setTestPhone] = useState('');
  const [testMessage, setTestMessage] = useState('');

  const API_BASE_URL = 'http://localhost:8080/api';

  useEffect(() => {
    fetchNotificationStats();
    fetchNotificationLogs();
    fetchNotificationSettings();
  }, []);

  const fetchNotificationStats = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/notifications/stats`);
      if (response.ok) {
        const data = await response.json();
        setStats(data);
      }
    } catch (err) {
      console.error('Error fetching notification stats:', err);
    }
  };

  const fetchNotificationLogs = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/notifications/logs?page=0&size=20`);
      if (response.ok) {
        const data = await response.json();
        setNotificationLogs(data.content || []);
      }
    } catch (err) {
      console.error('Error fetching notification logs:', err);
      setError('Failed to fetch notification logs');
    } finally {
      setLoading(false);
    }
  };

  const fetchNotificationSettings = async () => {
    try {
      // Using schoolId = 1 as default for demo
      const response = await fetch(`${API_BASE_URL}/notifications/settings/1`);
      if (response.ok) {
        const data = await response.json();
        setNotificationSettings(data);
      }
    } catch (err) {
      console.error('Error fetching notification settings:', err);
    }
  };

  const updateNotificationSettings = async (updatedSettings) => {
    try {
      const response = await fetch(`${API_BASE_URL}/notifications/settings/1`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(updatedSettings),
      });
      if (response.ok) {
        const data = await response.json();
        setNotificationSettings(data);
        alert('Settings updated successfully!');
      }
    } catch (err) {
      console.error('Error updating notification settings:', err);
      alert('Failed to update settings');
    }
  };

  const sendTestEmail = async () => {
    if (!testEmail || !testMessage) {
      alert('Please enter email and message');
      return;
    }

    try {
      const response = await fetch(`${API_BASE_URL}/notifications/test/email`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: new URLSearchParams({
          to: testEmail,
          subject: 'Test Email from School Attendance System',
          message: testMessage
        })
      });

      if (response.ok) {
        const data = await response.json();
        alert(data.message);
        setTestEmail('');
        setTestMessage('');
        fetchNotificationLogs(); // Refresh logs
      }
    } catch (err) {
      console.error('Error sending test email:', err);
      alert('Failed to send test email');
    }
  };

  const sendTestSMS = async () => {
    if (!testPhone || !testMessage) {
      alert('Please enter phone number and message');
      return;
    }

    try {
      const response = await fetch(`${API_BASE_URL}/notifications/test/sms`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: new URLSearchParams({
          to: testPhone,
          message: testMessage
        })
      });

      if (response.ok) {
        const data = await response.json();
        alert(data.message);
        setTestPhone('');
        setTestMessage('');
        fetchNotificationLogs(); // Refresh logs
      }
    } catch (err) {
      console.error('Error sending test SMS:', err);
      alert('Failed to send test SMS');
    }
  };

  const triggerDailyAlerts = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/notifications/trigger/daily-alerts`, {
        method: 'POST',
      });

      if (response.ok) {
        const data = await response.json();
        alert(data.message);
        fetchNotificationLogs(); // Refresh logs
        fetchNotificationStats(); // Refresh stats
      }
    } catch (err) {
      console.error('Error triggering daily alerts:', err);
      alert('Failed to trigger daily alerts');
    }
  };

  const triggerLowAttendanceAlerts = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/notifications/trigger/low-attendance-alerts`, {
        method: 'POST',
      });

      if (response.ok) {
        const data = await response.json();
        alert(data.message);
        fetchNotificationLogs(); // Refresh logs
        fetchNotificationStats(); // Refresh stats
      }
    } catch (err) {
      console.error('Error triggering low attendance alerts:', err);
      alert('Failed to trigger low attendance alerts');
    }
  };

  const formatDateTime = (dateString) => {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleString();
  };

  const StatCard = ({ title, value, icon: Icon, color = "blue" }) => (
    <div className={`bg-white p-6 rounded-lg shadow-md border-l-4 border-${color}-500`}>
      <div className="flex items-center justify-between">
        <div>
          <p className="text-sm font-medium text-gray-600">{title}</p>
          <p className="text-2xl font-semibold text-gray-900">{value}</p>
        </div>
        <Icon className={`h-8 w-8 text-${color}-500`} />
      </div>
    </div>
  );

  return (
    <div className="min-h-screen bg-gray-50 p-6">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 flex items-center gap-2">
            <Bell className="h-8 w-8 text-blue-600" />
            Notifications & Alerts
          </h1>
          <p className="text-gray-600 mt-2">Manage email and SMS notifications for attendance alerts</p>
        </div>

        {/* Stats Overview */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 xl:grid-cols-6 gap-6 mb-8">
          <StatCard 
            title="Total Notifications" 
            value={stats.totalNotifications} 
            icon={Bell} 
            color="blue" 
          />
          <StatCard 
            title="Sent Successfully" 
            value={stats.sentNotifications} 
            icon={TrendingUp} 
            color="green" 
          />
          <StatCard 
            title="Failed" 
            value={stats.failedNotifications} 
            icon={AlertTriangle} 
            color="red" 
          />
          <StatCard 
            title="Email Notifications" 
            value={stats.emailNotifications} 
            icon={Mail} 
            color="purple" 
          />
          <StatCard 
            title="SMS Notifications" 
            value={stats.smsNotifications} 
            icon={Phone} 
            color="orange" 
          />
          <StatCard 
            title="Success Rate" 
            value={`${stats.successRate.toFixed(1)}%`} 
            icon={TrendingUp} 
            color="emerald" 
          />
        </div>

        {/* Tabs */}
        <div className="bg-white rounded-lg shadow-md mb-6">
          <div className="border-b border-gray-200">
            <nav className="-mb-px flex space-x-8" aria-label="Tabs">
              {[
                { id: 'logs', name: 'Notification Logs', icon: Bell },
                { id: 'settings', name: 'Settings', icon: Settings },
                { id: 'test', name: 'Test Notifications', icon: Mail },
                { id: 'triggers', name: 'Manual Triggers', icon: Calendar },
              ].map((tab) => (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id)}
                  className={`${
                    activeTab === tab.id
                      ? 'border-blue-500 text-blue-600'
                      : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                  } whitespace-nowrap flex items-center py-4 px-1 border-b-2 font-medium text-sm`}
                >
                  <tab.icon className="h-5 w-5 mr-2" />
                  {tab.name}
                </button>
              ))}
            </nav>
          </div>

          <div className="p-6">
            {/* Notification Logs Tab */}
            {activeTab === 'logs' && (
              <div>
                <h2 className="text-lg font-semibold text-gray-900 mb-4">Recent Notifications</h2>
                {loading ? (
                  <div className="flex justify-center items-center h-32">
                    <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
                  </div>
                ) : error ? (
                  <div className="text-red-600 bg-red-50 p-4 rounded-lg">{error}</div>
                ) : notificationLogs.length === 0 ? (
                  <div className="text-gray-500 text-center py-8">
                    No notifications found. Try triggering some test notifications.
                  </div>
                ) : (
                  <div className="overflow-x-auto">
                    <table className="min-w-full divide-y divide-gray-200">
                      <thead className="bg-gray-50">
                        <tr>
                          <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Type</th>
                          <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Recipient</th>
                          <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Subject/Message</th>
                          <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                          <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Sent At</th>
                        </tr>
                      </thead>
                      <tbody className="bg-white divide-y divide-gray-200">
                        {notificationLogs.map((log, index) => (
                          <tr key={index}>
                            <td className="px-6 py-4 whitespace-nowrap">
                              <div className="flex items-center">
                                {log.type === 'EMAIL' ? (
                                  <Mail className="h-5 w-5 text-purple-500 mr-2" />
                                ) : (
                                  <Phone className="h-5 w-5 text-orange-500 mr-2" />
                                )}
                                <span className="text-sm font-medium text-gray-900">{log.type}</span>
                              </div>
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                              {log.recipient}
                            </td>
                            <td className="px-6 py-4 text-sm text-gray-900 max-w-xs truncate">
                              {log.subject || log.message}
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap">
                              <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
                                log.status === 'SENT' 
                                  ? 'bg-green-100 text-green-800' 
                                  : 'bg-red-100 text-red-800'
                              }`}>
                                {log.status}
                              </span>
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                              {formatDateTime(log.sentAt)}
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>
            )}

            {/* Settings Tab */}
            {activeTab === 'settings' && (
              <div>
                <h2 className="text-lg font-semibold text-gray-900 mb-4">Notification Settings</h2>
                <div className="space-y-6">
                  {/* Communication Channels */}
                  <div>
                    <h3 className="text-md font-medium text-gray-900 mb-3">Communication Channels</h3>
                    <div className="space-y-3">
                      <label className="flex items-center">
                        <input
                          type="checkbox"
                          checked={notificationSettings.emailEnabled}
                          onChange={(e) => setNotificationSettings(prev => ({
                            ...prev,
                            emailEnabled: e.target.checked
                          }))}
                          className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                        />
                        <span className="ml-2 text-sm text-gray-900">Enable Email Notifications</span>
                      </label>
                      <label className="flex items-center">
                        <input
                          type="checkbox"
                          checked={notificationSettings.smsEnabled}
                          onChange={(e) => setNotificationSettings(prev => ({
                            ...prev,
                            smsEnabled: e.target.checked
                          }))}
                          className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                        />
                        <span className="ml-2 text-sm text-gray-900">Enable SMS Notifications</span>
                      </label>
                    </div>
                  </div>

                  {/* Alert Types */}
                  <div>
                    <h3 className="text-md font-medium text-gray-900 mb-3">Alert Types</h3>
                    <div className="space-y-3">
                      <label className="flex items-center">
                        <input
                          type="checkbox"
                          checked={notificationSettings.dailyAbsenteeAlerts}
                          onChange={(e) => setNotificationSettings(prev => ({
                            ...prev,
                            dailyAbsenteeAlerts: e.target.checked
                          }))}
                          className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                        />
                        <span className="ml-2 text-sm text-gray-900">Daily Absentee Alerts</span>
                      </label>
                      <label className="flex items-center">
                        <input
                          type="checkbox"
                          checked={notificationSettings.lowAttendanceAlerts}
                          onChange={(e) => setNotificationSettings(prev => ({
                            ...prev,
                            lowAttendanceAlerts: e.target.checked
                          }))}
                          className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                        />
                        <span className="ml-2 text-sm text-gray-900">Low Attendance Alerts</span>
                      </label>
                      <label className="flex items-center">
                        <input
                          type="checkbox"
                          checked={notificationSettings.holidayNotifications}
                          onChange={(e) => setNotificationSettings(prev => ({
                            ...prev,
                            holidayNotifications: e.target.checked
                          }))}
                          className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                        />
                        <span className="ml-2 text-sm text-gray-900">Holiday Notifications</span>
                      </label>
                    </div>
                  </div>

                  {/* Attendance Threshold */}
                  <div>
                    <h3 className="text-md font-medium text-gray-900 mb-3">Attendance Threshold</h3>
                    <div className="flex items-center space-x-3">
                      <label className="text-sm text-gray-700">Minimum attendance percentage:</label>
                      <input
                        type="number"
                        min="0"
                        max="100"
                        step="0.1"
                        value={notificationSettings.attendanceThreshold}
                        onChange={(e) => setNotificationSettings(prev => ({
                          ...prev,
                          attendanceThreshold: parseFloat(e.target.value)
                        }))}
                        className="w-20 px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                      />
                      <span className="text-sm text-gray-500">%</span>
                    </div>
                  </div>

                  {/* Notification Time */}
                  <div>
                    <h3 className="text-md font-medium text-gray-900 mb-3">Notification Schedule</h3>
                    <div className="flex items-center space-x-3">
                      <label className="text-sm text-gray-700">Daily notification time:</label>
                      <input
                        type="time"
                        value={notificationSettings.notificationTime}
                        onChange={(e) => setNotificationSettings(prev => ({
                          ...prev,
                          notificationTime: e.target.value
                        }))}
                        className="px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                      />
                    </div>
                  </div>

                  {/* Save Button */}
                  <div className="pt-4">
                    <button
                      onClick={() => updateNotificationSettings(notificationSettings)}
                      className="bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded-md transition duration-150 ease-in-out"
                    >
                      Save Settings
                    </button>
                  </div>
                </div>
              </div>
            )}

            {/* Test Notifications Tab */}
            {activeTab === 'test' && (
              <div>
                <h2 className="text-lg font-semibold text-gray-900 mb-4">Test Notifications</h2>
                <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
                  {/* Test Email */}
                  <div className="bg-gray-50 p-6 rounded-lg">
                    <h3 className="text-md font-medium text-gray-900 mb-4 flex items-center">
                      <Mail className="h-5 w-5 mr-2 text-purple-500" />
                      Test Email
                    </h3>
                    <div className="space-y-4">
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Email Address</label>
                        <input
                          type="email"
                          value={testEmail}
                          onChange={(e) => setTestEmail(e.target.value)}
                          placeholder="Enter email address"
                          className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Message</label>
                        <textarea
                          value={testMessage}
                          onChange={(e) => setTestMessage(e.target.value)}
                          placeholder="Enter test message"
                          rows={4}
                          className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                        />
                      </div>
                      <button
                        onClick={sendTestEmail}
                        className="w-full bg-purple-600 hover:bg-purple-700 text-white font-medium py-2 px-4 rounded-md transition duration-150 ease-in-out"
                      >
                        Send Test Email
                      </button>
                    </div>
                  </div>

                  {/* Test SMS */}
                  <div className="bg-gray-50 p-6 rounded-lg">
                    <h3 className="text-md font-medium text-gray-900 mb-4 flex items-center">
                      <Phone className="h-5 w-5 mr-2 text-orange-500" />
                      Test SMS
                    </h3>
                    <div className="space-y-4">
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Phone Number</label>
                        <input
                          type="tel"
                          value={testPhone}
                          onChange={(e) => setTestPhone(e.target.value)}
                          placeholder="Enter phone number (+91-9876543210)"
                          className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Message</label>
                        <textarea
                          value={testMessage}
                          onChange={(e) => setTestMessage(e.target.value)}
                          placeholder="Enter test message"
                          rows={4}
                          className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                        />
                      </div>
                      <button
                        onClick={sendTestSMS}
                        className="w-full bg-orange-600 hover:bg-orange-700 text-white font-medium py-2 px-4 rounded-md transition duration-150 ease-in-out"
                      >
                        Send Test SMS
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            )}

            {/* Manual Triggers Tab */}
            {activeTab === 'triggers' && (
              <div>
                <h2 className="text-lg font-semibold text-gray-900 mb-4">Manual Triggers</h2>
                <p className="text-gray-600 mb-6">
                  Use these buttons to manually trigger notification alerts for testing purposes.
                </p>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div className="bg-blue-50 p-6 rounded-lg">
                    <h3 className="text-md font-medium text-gray-900 mb-2">Daily Absentee Alerts</h3>
                    <p className="text-sm text-gray-600 mb-4">
                      Send notifications to parents of students marked absent today.
                    </p>
                    <button
                      onClick={triggerDailyAlerts}
                      className="bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded-md transition duration-150 ease-in-out"
                    >
                      Trigger Daily Alerts
                    </button>
                  </div>

                  <div className="bg-yellow-50 p-6 rounded-lg">
                    <h3 className="text-md font-medium text-gray-900 mb-2">Low Attendance Alerts</h3>
                    <p className="text-sm text-gray-600 mb-4">
                      Send notifications to parents of students with low attendance.
                    </p>
                    <button
                      onClick={triggerLowAttendanceAlerts}
                      className="bg-yellow-600 hover:bg-yellow-700 text-white font-medium py-2 px-4 rounded-md transition duration-150 ease-in-out"
                    >
                      Trigger Low Attendance Alerts
                    </button>
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Notifications;
