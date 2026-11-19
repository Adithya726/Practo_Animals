import { useState } from 'react';
import { Flag } from 'lucide-react';
import Modal from '../components/ui/Modal';
import Button from '../components/ui/Button';
import Field from '../components/ui/Field';
import { useToast } from '../components/ui/useToast';
import { submitCorrection } from '../services/providerService';
import { getErrorMessage, getFieldErrors } from '../services/api';

const FIELDS = [
  { value: 'PHONE', label: 'Phone number' },
  { value: 'ADDRESS', label: 'Address or location' },
  { value: 'HOURS', label: 'Opening hours' },
  { value: 'FEE', label: 'Fees' },
  { value: 'SERVICES', label: 'Services offered' },
  { value: 'CLOSED', label: 'Permanently closed' },
  { value: 'OTHER', label: 'Something else' },
];

function CorrectionModal({ provider, onClose }) {
  const toast = useToast();
  const [form, setForm] = useState({ field: 'PHONE', details: '', reporterEmail: '' });
  const [errors, setErrors] = useState({});
  const [saving, setSaving] = useState(false);

  if (!provider) {
    return null;
  }

  const update = (key) => (event) => setForm((current) => ({ ...current, [key]: event.target.value }));

  const submit = async (event) => {
    event.preventDefault();
    setSaving(true);
    setErrors({});
    try {
      await submitCorrection({ ...form, providerId: provider.id });
      toast.success('Thanks! Our team will verify and update this listing.');
      onClose();
    } catch (error) {
      setErrors(getFieldErrors(error));
      toast.error(getErrorMessage(error));
    } finally {
      setSaving(false);
    }
  };

  return (
    <Modal open title="Submit a correction" icon={<Flag size={22} />} onClose={onClose}>
      <p className="modal-sub">
        Help other pet parents. What&apos;s wrong with <strong>{provider.name}</strong>?
      </p>
      <form className="form-stack" onSubmit={submit}>
        <Field label="What needs fixing?" error={errors.field}>
          <select value={form.field} onChange={update('field')}>
            {FIELDS.map((f) => (
              <option key={f.value} value={f.value}>{f.label}</option>
            ))}
          </select>
        </Field>
        <Field label="Correct details" error={errors.details}>
          <textarea
            rows={3}
            maxLength={500}
            value={form.details}
            onChange={update('details')}
            placeholder="For example: the new number is 040 1234 5678"
            required
          />
        </Field>
        <Field label="Your email" optional error={errors.reporterEmail} hint="Only used if we need to check something with you.">
          <input type="email" value={form.reporterEmail} onChange={update('reporterEmail')} placeholder="you@example.com" />
        </Field>
        <div className="modal-actions">
          <Button variant="secondary" onClick={onClose}>Cancel</Button>
          <Button type="submit" disabled={saving}>{saving ? 'Sending...' : 'Send correction'}</Button>
        </div>
      </form>
    </Modal>
  );
}

export default CorrectionModal;
