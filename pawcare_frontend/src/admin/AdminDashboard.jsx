import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Flag, GraduationCap, Inbox, LogOut, PawPrint, RefreshCw, Store, Users, Wand2 } from 'lucide-react';
import Brand from '../components/common/Brand';
import EmptyState from '../components/common/EmptyState';
import { PageSkeleton } from '../components/ui/Skeleton';
import { useToast } from '../components/ui/useToast';
import { useAuth } from '../contextapi/useAuth';
import {
  getCorrections, getLeads, getListings, getSummary, setCorrectionStatus, setLeadStatus, setListingStatus,
} from '../services/adminService';
import { getErrorMessage } from '../services/api';
import { formatDateTime, rupees } from '../utils/format';

const TABS = [
  { key: 'listings', label: 'Business listings', icon: Store },
  { key: 'corrections', label: 'Corrections', icon: Flag },
  { key: 'leads', label: 'Course leads', icon: GraduationCap },
];

const fetchDashboard = () => Promise.all([getSummary(), getListings(), getCorrections(), getLeads()]);

const LEAD_STATUSES = ['NEW', 'CONTACTED', 'ENROLLED', 'LOST'];

function StatusPill({ status }) {
  return <span className={`status-pill s-${status.toLowerCase()}`}>{status.toLowerCase()}</span>;
}

function AdminDashboard() {
  const { auth, logout } = useAuth();
  const toast = useToast();
  const [tab, setTab] = useState('listings');
  const [summary, setSummary] = useState(null);
  const [rows, setRows] = useState({ listings: [], corrections: [], leads: [] });
  const [loading, setLoading] = useState(true);
  const [busyId, setBusyId] = useState(null);

  const apply = useCallback(([s, l, c, d]) => {
    setSummary(s.data);
    setRows({ listings: l.data, corrections: c.data, leads: d.data });
    setLoading(false);
  }, []);

  const fail = useCallback((error) => {
    toast.error(getErrorMessage(error, 'Could not load the dashboard.'));
    setLoading(false);
  }, [toast]);

  useEffect(() => {
    fetchDashboard().then(apply).catch(fail);
  }, [apply, fail]);

  const load = () => {
    setLoading(true);
    return fetchDashboard().then(apply).catch(fail);
  };

  const act = async (fn, id, status, message) => {
    setBusyId(id);
    try {
      await fn(id, status);
      toast.success(message);
      await load();
    } catch (error) {
      toast.error(getErrorMessage(error));
    } finally {
      setBusyId(null);
    }
  };

  const stats = summary
    ? [
        { label: 'Live providers', value: summary.providers, icon: PawPrint },
        { label: 'Listings to review', value: summary.pendingListings, icon: Store },
        { label: 'Corrections to check', value: summary.pendingCorrections, icon: Flag },
        { label: 'New leads', value: summary.newLeads, icon: Users },
        { label: 'Seat reservations', value: summary.registrations, icon: GraduationCap },
        { label: 'Carousels made', value: summary.carousels, icon: Wand2 },
      ]
    : [];

  return (
    <div className="admin">
      <header className="admin-head">
        <div className="container admin-head-inner">
          <Brand to="/admin" />
          <span className="muted">Signed in as {auth?.name}</span>
          <div className="admin-head-actions">
            <Link to="/" className="ui-btn ui-btn-ghost ui-btn-sm">View site</Link>
            <button type="button" className="ui-btn ui-btn-secondary ui-btn-sm" onClick={logout}><LogOut size={15} /> Sign out</button>
          </div>
        </div>
      </header>

      <main className="container admin-main">
        <div className="admin-title">
          <h1>Operations</h1>
          <button type="button" className="ui-btn ui-btn-secondary ui-btn-sm" onClick={load} disabled={loading}>
            <RefreshCw size={15} /> Refresh
          </button>
        </div>

        {loading && !summary ? <PageSkeleton /> : (
          <>
            <div className="stat-grid">
              {stats.map(({ label, value, icon: Icon }) => (
                <div key={label} className="stat">
                  <Icon size={18} />
                  <strong>{value}</strong>
                  <span>{label}</span>
                </div>
              ))}
            </div>

            <div className="admin-tabs" role="tablist">
              {TABS.map(({ key, label, icon: Icon }) => (
                <button key={key} type="button" role="tab" aria-selected={tab === key} className={tab === key ? 'active' : ''} onClick={() => setTab(key)}>
                  <Icon size={16} /> {label} <span>{rows[key].length}</span>
                </button>
              ))}
            </div>

            <div className="panel table-panel">
              {rows[tab].length === 0 ? (
                <EmptyState icon={<Inbox size={26} />} title="Nothing here yet" text="New submissions from the site will appear here." />
              ) : tab === 'listings' ? (
                <table className="data-table">
                  <thead><tr><th>Business</th><th>Contact</th><th>Details</th><th>Submitted</th><th>Status</th><th /></tr></thead>
                  <tbody>
                    {rows.listings.map((l) => (
                      <tr key={l.id}>
                        <td><strong>{l.businessName}</strong><small>{l.categoryLabel} · {l.area}, {l.city}</small></td>
                        <td>{l.contactName}<small>{l.phone} · {l.email}</small></td>
                        <td><small>From {rupees(l.feeFrom)}{l.open24x7 ? ' · 24x7' : ''}{l.homeVisit ? ' · Home visit' : ''}</small></td>
                        <td><small>{formatDateTime(l.createdAt)}</small></td>
                        <td><StatusPill status={l.status} /></td>
                        <td className="row-actions">
                          {l.status === 'PENDING' ? (
                            <>
                              <button type="button" className="ui-btn ui-btn-primary ui-btn-sm" disabled={busyId === l.id} onClick={() => act(setListingStatus, l.id, 'APPROVED', 'Approved and published')}>Approve</button>
                              <button type="button" className="ui-btn ui-btn-secondary ui-btn-sm" disabled={busyId === l.id} onClick={() => act(setListingStatus, l.id, 'REJECTED', 'Listing rejected')}>Reject</button>
                            </>
                          ) : l.providerId ? <Link to={`/provider/${l.providerId}`} className="link-btn">View live</Link> : null}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              ) : tab === 'corrections' ? (
                <table className="data-table">
                  <thead><tr><th>Provider</th><th>Issue</th><th>Reported by</th><th>Submitted</th><th>Status</th><th /></tr></thead>
                  <tbody>
                    {rows.corrections.map((c) => (
                      <tr key={c.id}>
                        <td><Link to={`/provider/${c.providerId}`}><strong>{c.providerName}</strong></Link></td>
                        <td><span className="field-tag">{c.field}</span><small>{c.details}</small></td>
                        <td><small>{c.reporterEmail || 'Anonymous'}</small></td>
                        <td><small>{formatDateTime(c.createdAt)}</small></td>
                        <td><StatusPill status={c.status} /></td>
                        <td className="row-actions">
                          {c.status === 'PENDING' ? (
                            <>
                              <button type="button" className="ui-btn ui-btn-primary ui-btn-sm" disabled={busyId === c.id} onClick={() => act(setCorrectionStatus, c.id, 'APPROVED', 'Marked as fixed')}>Mark fixed</button>
                              <button type="button" className="ui-btn ui-btn-secondary ui-btn-sm" disabled={busyId === c.id} onClick={() => act(setCorrectionStatus, c.id, 'REJECTED', 'Correction dismissed')}>Dismiss</button>
                            </>
                          ) : null}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              ) : (
                <table className="data-table">
                  <thead><tr><th>Lead</th><th>Interest</th><th>Source</th><th>Received</th><th>Status</th></tr></thead>
                  <tbody>
                    {rows.leads.map((d) => (
                      <tr key={d.id}>
                        <td><strong>{d.name}</strong><small>{d.phone} · {d.email}</small></td>
                        <td>
                          <span className={`field-tag ${d.type === 'REGISTRATION' ? 'strong' : ''}`}>{d.type === 'REGISTRATION' ? 'Seat reserved' : 'Enquiry'}</span>
                          <small>{d.courseTitle || 'Not sure yet'} · {d.background.toLowerCase()}</small>
                          {d.message ? <small className="quote">&ldquo;{d.message}&rdquo;</small> : null}
                        </td>
                        <td><small>{d.source}</small></td>
                        <td><small>{formatDateTime(d.createdAt)}</small></td>
                        <td>
                          <select value={d.status} disabled={busyId === d.id} onChange={(e) => act(setLeadStatus, d.id, e.target.value, 'Lead updated')} aria-label="Lead status">
                            {LEAD_STATUSES.map((s) => <option key={s} value={s}>{s.toLowerCase()}</option>)}
                          </select>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </div>
          </>
        )}
      </main>
    </div>
  );
}

export default AdminDashboard;
