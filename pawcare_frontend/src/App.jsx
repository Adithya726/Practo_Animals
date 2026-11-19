import { lazy, Suspense } from 'react';
import { Route, Routes } from 'react-router-dom';
import PublicLayout from './components/common/PublicLayout';
import ProtectedRoute from './components/common/ProtectedRoute';
import ScrollToTop from './components/common/ScrollToTop';
import Home from './main/Home';
import NotFound from './main/NotFound';
import SearchPage from './search/SearchPage';
import ProviderDetails from './search/ProviderDetails';
import ListBusiness from './business/ListBusiness';
import { PageSkeleton } from './components/ui/Skeleton';

// The academy, studio (with its PDF/PNG export libraries) and admin load on demand.
const Academy = lazy(() => import('./academy/Academy'));
const Studio = lazy(() => import('./studio/Studio'));
const AdminLogin = lazy(() => import('./admin/AdminLogin'));
const AdminDashboard = lazy(() => import('./admin/AdminDashboard'));

function App() {
  return (
    <>
      <ScrollToTop />
      <Suspense fallback={<div className="container page-pad"><PageSkeleton /></div>}>
      <Routes>
        {/* 1. Practo for Animals */}
        <Route element={<PublicLayout />}>
          <Route path="/" element={<Home />} />
          <Route path="/search" element={<SearchPage />} />
          <Route path="/provider/:id" element={<ProviderDetails />} />
          <Route path="/list-your-business" element={<ListBusiness />} />
          <Route path="*" element={<NotFound />} />
        </Route>

        {/* 2. AI education landing page */}
        <Route path="/academy" element={<Academy />} />

        {/* 3. AI content studio */}
        <Route path="/studio" element={<Studio />} />

        {/* Back office */}
        <Route path="/admin/login" element={<AdminLogin />} />
        <Route element={<ProtectedRoute />}>
          <Route path="/admin" element={<AdminDashboard />} />
        </Route>
      </Routes>
      </Suspense>
    </>
  );
}

export default App;
