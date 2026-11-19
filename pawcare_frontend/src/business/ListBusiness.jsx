import { useState } from 'react';
import { Link } from 'react-router-dom';
import { BadgeCheck, CircleCheck, Megaphone, PhoneCall, Store, Users } from 'lucide-react';
import Button from '../components/ui/Button';
import Field from '../components/ui/Field';
import { useToast } from '../components/ui/useToast';
import { CATEGORY_META } from '../components/common/categoryMeta';
import { submitListing } from '../services/providerService';
import { getErrorMessage, getFieldErrors } from '../services/api';

const CATEGORIES = [
  { value: 'VET_CLINIC', label: 'Vet Clinic' },
  { value: 'HOSPITAL', label: 'Pet Hospital' },
  { value: 'BOARDING', label: 'Boarding & Daycare' },
  { value: 'DIAGNOSTICS', label: 'Diagnostics & Labs' },
  { value: 'AMBULANCE', label: 'Pet Ambulance' },
  { value: 'NGO', label: 'Rescue & NGO' },
];

const EMPTY = {
  businessName: '',
  category: 'VET_CLINIC',
  city: '',
  area: '',
  address: '',
  contactName: '',
  phone: '',
  email: '',
  about: '',
  feeFrom: '',
  open24x7: false,
  homeVisit: false,
};

const BENEFITS = [
  { icon: Users, title: 'Reach pet parents nearby', text: 'Show up when people in your city search for your kind of care.' },
  { icon: PhoneCall, title: 'Calls, not clicks', text: 'Every listing has one-tap call and directions buttons.' },
  { icon: BadgeCheck, title: 'Verified badge', text: 'We check your details once, then show a verified badge.' },
  { icon: Megaphone, title: 'Free to list', text: 'No fees to be listed. Featured spots come later, if you want them.' },
];

function ListBusiness() {
  const toast = useToast();
  const [form, setForm] = useState(EMPTY);
  const [errors, setErrors] = useState({});
  const [saving, setSaving] = useState(false);
  const [done, setDone] = useState(null);

  const update = (key) => (event) => {
    const value = event.target.type === 'checkbox' ? event.target.checked : event.target.value;
    setForm((current) => ({ ...current, [key]: value }));
  };

  const submit = async (event) => {
    event.preventDefault();
    setSaving(true);
    setErrors({});
    try {
      const { data } = await submitListing({ ...form, feeFrom: Number(form.feeFrom || 0) });
      setDone(data);
      window.scrollTo({ top: 0, behavior: 'smooth' });
    } catch (error) {
      setErrors(getFieldErrors(error));
      toast.error(getErrorMessage(error, 'Could not submit your listing.'));
    } finally {
      setSaving(false);
    }
  };

  if (done) {
    return (
      <div className="container page-pad">
        <div className="success-card">
          <CircleCheck size={44} />
          <h1>Thanks, {done.contactName.split(' ')[0]}!</h1>
          <p>
            <strong>{done.businessName}</strong> is in our review queue. We usually verify listings within 24 hours and
            will email <strong>{done.email}</strong> once it&apos;s live.
          </p>
          <div className="success-actions">
            <Link to="/search" className="ui-btn ui-btn-primary">Browse PawCare</Link>
            <button type="button" className="ui-btn ui-btn-secondary" onClick={() => { setDone(null); setForm(EMPTY); }}>
              Add another location
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="container page-pad business-page">
      <aside className="business-pitch">
        <span className="eyebrow-pill"><Store size={14} /> For clinics, shelters &amp; pet businesses</span>
        <h1>Get found by pet parents who need you today.</h1>
        <div className="benefits">
          {BENEFITS.map(({ icon: Icon, title, text }) => (
            <div key={title} className="benefit">
              <Icon size={20} />
              <div><strong>{title}</strong><span>{text}</span></div>
            </div>
          ))}
        </div>
      </aside>

      <form className="panel business-form" onSubmit={submit} noValidate>
        <h2>List Your Business</h2>
        <p className="muted">Takes about 2 minutes. Fields marked optional can be added later.</p>

        <fieldset>
          <legend>What kind of business?</legend>
          <div className="category-picker">
            {CATEGORIES.map((c) => {
              const Icon = CATEGORY_META[c.value].icon;
              return (
                <label key={c.value} className={`pick ${form.category === c.value ? 'active' : ''}`}>
                  <input type="radio" name="category" value={c.value} checked={form.category === c.value} onChange={update('category')} />
                  <Icon size={18} /> {c.label}
                </label>
              );
            })}
          </div>
        </fieldset>

        <fieldset>
          <legend>Business details</legend>
          <Field label="Business name" error={errors.businessName}>
            <input value={form.businessName} onChange={update('businessName')} placeholder="e.g. Happy Paws Clinic" />
          </Field>
          <div className="form-row">
            <Field label="City" error={errors.city}>
              <input value={form.city} onChange={update('city')} placeholder="e.g. Vijayawada" />
            </Field>
            <Field label="Area / locality" error={errors.area}>
              <input value={form.area} onChange={update('area')} placeholder="e.g. Benz Circle" />
            </Field>
          </div>
          <Field label="Full address" error={errors.address}>
            <input value={form.address} onChange={update('address')} placeholder="Building, street, landmark, PIN" />
          </Field>
          <div className="form-row">
            <Field label="Starting fee (₹)" error={errors.feeFrom} hint="Use 0 if your services are free.">
              <input type="number" min="0" value={form.feeFrom} onChange={update('feeFrom')} placeholder="500" />
            </Field>
            <div className="toggle-stack">
              <label className="check-row"><input type="checkbox" checked={form.open24x7} onChange={update('open24x7')} /> Open 24x7</label>
              <label className="check-row"><input type="checkbox" checked={form.homeVisit} onChange={update('homeVisit')} /> Offers home visits</label>
            </div>
          </div>
          <Field label="About your business" optional error={errors.about}>
            <textarea rows={3} maxLength={1000} value={form.about} onChange={update('about')} placeholder="Specialities, facilities, languages spoken..." />
          </Field>
        </fieldset>

        <fieldset>
          <legend>Contact person</legend>
          <Field label="Your name" error={errors.contactName}>
            <input value={form.contactName} onChange={update('contactName')} placeholder="Full name" />
          </Field>
          <div className="form-row">
            <Field label="Phone" error={errors.phone}>
              <input type="tel" value={form.phone} onChange={update('phone')} placeholder="+91 98765 43210" />
            </Field>
            <Field label="Email" error={errors.email}>
              <input type="email" value={form.email} onChange={update('email')} placeholder="you@clinic.com" />
            </Field>
          </div>
        </fieldset>

        <Button type="submit" size="lg" fullWidth disabled={saving}>
          {saving ? 'Submitting...' : 'Submit for verification'}
        </Button>
        <p className="fine-print">By submitting you confirm you can represent this business.</p>
      </form>
    </div>
  );
}

export default ListBusiness;
