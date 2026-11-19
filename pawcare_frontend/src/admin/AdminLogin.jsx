import { useState } from 'react';
import { Navigate, useLocation, useNavigate } from 'react-router-dom';
import { LockKeyhole } from 'lucide-react';
import Brand from '../components/common/Brand';
import Button from '../components/ui/Button';
import Field from '../components/ui/Field';
import { useAuth } from '../contextapi/useAuth';
import { getErrorMessage } from '../services/api';

function AdminLogin() {
  const { login, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [form, setForm] = useState({ email: '', password: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  if (isAuthenticated) {
    return <Navigate to="/admin" replace />;
  }

  const submit = async (event) => {
    event.preventDefault();
    setLoading(true);
    setError('');
    try {
      await login(form);
      navigate(location.state?.from?.pathname || '/admin', { replace: true });
    } catch (err) {
      setError(getErrorMessage(err, 'Login failed'));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <form className="auth-card" onSubmit={submit}>
        <Brand />
        <h1><LockKeyhole size={20} /> Admin sign in</h1>
        <p className="muted">Review new listings, corrections and course leads.</p>
        {error ? <div className="form-error" role="alert">{error}</div> : null}
        <Field label="Email">
          <input type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} autoComplete="username" required />
        </Field>
        <Field label="Password">
          <input type="password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} autoComplete="current-password" required />
        </Field>
        <Button type="submit" size="lg" fullWidth disabled={loading}>{loading ? 'Signing in...' : 'Sign in'}</Button>
        <p className="fine-print">Demo login: admin@pawcare.in / Admin@123 (change it with ADMIN_EMAIL and ADMIN_PASSWORD)</p>
      </form>
    </div>
  );
}

export default AdminLogin;
