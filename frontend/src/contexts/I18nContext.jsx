import React, { createContext, useContext, useState, useEffect } from 'react';

const I18nContext = createContext();

// Supported languages
export const SUPPORTED_LANGUAGES = [
  { code: 'en', name: 'English', nativeName: 'English', flag: '🇺🇸' },
  { code: 'hi', name: 'Hindi', nativeName: 'हिंदी', flag: '🇮🇳' },
  { code: 'mr', name: 'Marathi', nativeName: 'मराठी', flag: '🇮🇳' }
];

// Default messages (fallback)
const defaultMessages = {
  'app.name': 'School Attendance Management System',
  'nav.dashboard': 'Dashboard',
  'nav.students': 'Students',
  'nav.teachers': 'Teachers',
  'nav.attendance': 'Attendance',
  'nav.reports': 'Reports',
  'nav.settings': 'Settings',
  'nav.logout': 'Logout',
  'auth.login': 'Login',
  'auth.logout': 'Logout',
  'auth.username': 'Username',
  'auth.password': 'Password',
  'action.save': 'Save',
  'action.cancel': 'Cancel',
  'action.edit': 'Edit',
  'action.delete': 'Delete',
  'action.view': 'View',
  'action.add': 'Add',
  'accessibility.language.selector': 'Language Selector',
  'language.select': 'Select Language'
};

export const I18nProvider = ({ children }) => {
  const [currentLanguage, setCurrentLanguage] = useState('en');
  const [messages, setMessages] = useState(defaultMessages);
  const [loading, setLoading] = useState(false);

  // Load messages for the selected language
  const loadMessages = async (languageCode) => {
    setLoading(true);
    try {
      // Try to fetch from backend API first
      const response = await fetch(`http://localhost:8080/api/messages?lang=${languageCode}`, {
        headers: {
          'Accept-Language': languageCode
        },
        credentials: 'include'
      });
      
      if (response.ok) {
        const data = await response.json();
        setMessages({ ...defaultMessages, ...data });
      } else {
        // Fallback to local translations
        let languageMessages = { ...defaultMessages };
        
        if (languageCode === 'hi') {
          languageMessages = {
            ...defaultMessages,
            'app.name': 'स्कूल उपस्थिति प्रबंधन प्रणाली',
            'nav.dashboard': 'डैशबोर्ड',
            'nav.students': 'छात्र',
            'nav.teachers': 'शिक्षक',
            'nav.attendance': 'उपस्थिति',
            'nav.reports': 'रिपोर्ट',
            'nav.settings': 'सेटिंग्स',
            'auth.login': 'लॉगिन',
            'auth.username': 'उपयोगकर्ता नाम',
            'auth.password': 'पासवर्ड'
          };
        } else if (languageCode === 'mr') {
          languageMessages = {
            ...defaultMessages,
            'app.name': 'शाळा उपस्थिती व्यवस्थापन प्रणाली',
            'nav.dashboard': 'डॅशबोर्ड',
            'nav.students': 'विद्यार्थी',
            'nav.teachers': 'शिक्षक',
            'nav.attendance': 'उपस्थिती',
            'nav.reports': 'अहवाल',
            'nav.settings': 'सेटिंग्ज',
            'auth.login': 'लॉगिन',
            'auth.username': 'वापरकर्ता नाव',
            'auth.password': 'पासवर्ड'
          };
        }
        
        setMessages(languageMessages);
      }
    } catch (error) {
      console.warn('Failed to load language messages:', error);
      // Use default messages as fallback
      setMessages(defaultMessages);
    } finally {
      setLoading(false);
    }
  };

  // Change language
  const changeLanguage = async (languageCode) => {
    if (languageCode !== currentLanguage) {
      setCurrentLanguage(languageCode);
      localStorage.setItem('preferred-language', languageCode);
      await loadMessages(languageCode);
      
      // Update document language and direction
      document.documentElement.lang = languageCode;
      document.documentElement.dir = ['ar', 'ur', 'fa'].includes(languageCode) ? 'rtl' : 'ltr';
    }
  };

  // Get translated message
  const t = (key, params = {}) => {
    let message = messages[key] || key;
    
    // Replace parameters in the message
    Object.keys(params).forEach(param => {
      message = message.replace(`{${param}}`, params[param]);
    });
    
    return message;
  };

  // Get current language info
  const getCurrentLanguage = () => {
    return SUPPORTED_LANGUAGES.find(lang => lang.code === currentLanguage) || SUPPORTED_LANGUAGES[0];
  };

  // Initialize language from localStorage or browser preference
  useEffect(() => {
    const savedLanguage = localStorage.getItem('preferred-language');
    const browserLanguage = navigator.language.split('-')[0];
    
    let initialLanguage = 'en';
    
    if (savedLanguage && SUPPORTED_LANGUAGES.some(lang => lang.code === savedLanguage)) {
      initialLanguage = savedLanguage;
    } else if (SUPPORTED_LANGUAGES.some(lang => lang.code === browserLanguage)) {
      initialLanguage = browserLanguage;
    }
    
    setCurrentLanguage(initialLanguage);
    loadMessages(initialLanguage);
    
    // Set initial document language
    document.documentElement.lang = initialLanguage;
  }, []);

  const value = {
    currentLanguage,
    changeLanguage,
    t,
    loading,
    getCurrentLanguage,
    supportedLanguages: SUPPORTED_LANGUAGES
  };

  return (
    <I18nContext.Provider value={value}>
      {children}
    </I18nContext.Provider>
  );
};

export const useI18n = () => {
  const context = useContext(I18nContext);
  if (!context) {
    throw new Error('useI18n must be used within an I18nProvider');
  }
  return context;
};

export default I18nContext;
