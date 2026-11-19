import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import '@fontsource-variable/plus-jakarta-sans';
import '@fontsource-variable/inter';
import { AuthProvider } from './contextapi/AuthContext';
import { ToastProvider } from './components/ui/Toast';
import App from './App.jsx';
import './styles/global.css';
import './styles/pawcare.css';
import './styles/academy.css';
import './styles/studio.css';
import './styles/admin.css';

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <BrowserRouter>
      <AuthProvider>
        <ToastProvider>
          <App />
        </ToastProvider>
      </AuthProvider>
    </BrowserRouter>
  </StrictMode>
);
