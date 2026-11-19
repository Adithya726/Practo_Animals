import { useMemo, useState } from 'react';
import { AuthContext } from './authContextValue';
import { clearStoredAuth, getStoredAuth, saveStoredAuth } from '../services/api';
import { loginAdmin } from '../services/adminService';

export function AuthProvider({ children }) {
  const [auth, setAuth] = useState(() => getStoredAuth());

  const value = useMemo(() => {
    const login = async ({ email, password }) => {
      const { data } = await loginAdmin({ email, password });
      const nextAuth = { token: data.token, adminId: data.adminId, name: data.name, email: data.email };
      saveStoredAuth(nextAuth);
      setAuth(nextAuth);
      return nextAuth;
    };

    const logout = () => {
      clearStoredAuth();
      setAuth(null);
    };

    return { auth, isAuthenticated: Boolean(auth?.token), login, logout };
  }, [auth]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
