import { useState } from 'react';
import { CalendarDays, CircleCheck, GraduationCap, MessageCircle, PhoneCall } from 'lucide-react';
import Modal from '../components/ui/Modal';
import Button from '../components/ui/Button';
import Field from '../components/ui/Field';
import { useToast } from '../components/ui/useToast';
import { submitLead } from '../services/courseService';
import { getErrorMessage, getFieldErrors } from '../services/api';
import { formatDate, rupees } from '../utils/format';

/**
 * The registration journey: choose a programme, leave details, see what
 * happens next. Seat availability is checked on the server.
 */
function RegisterModal({ open, courses, initialCourseId, source, onClose, onRegistered }) {
  const toast = useToast();
  // The parent mounts a fresh modal each time it opens, so state starts clean.
  const [step, setStep] = useState(initialCourseId ? 2 : 1);
  const [courseId, setCourseId] = useState(initialCourseId);
  const [form, setForm] = useState({ name: '', email: '', phone: '', background: 'STUDENT' });
  const [errors, setErrors] = useState({});
  const [saving, setSaving] = useState(false);

  const course = courses.find((c) => c.id === courseId);
  const update = (key) => (event) => setForm((current) => ({ ...current, [key]: event.target.value }));

  const submit = async (event) => {
    event.preventDefault();
    setSaving(true);
    setErrors({});
    try {
      await submitLead({ ...form, courseId, type: 'REGISTRATION', source });
      setStep(3);
      onRegistered?.();
    } catch (error) {
      setErrors(getFieldErrors(error));
      toast.error(getErrorMessage(error, 'Could not reserve your seat.'));
    } finally {
      setSaving(false);
    }
  };

  const title = step === 3 ? 'Your seat is reserved' : 'Reserve your seat';

  return (
    <Modal open={open} title={title} icon={step === 3 ? <CircleCheck size={22} /> : <GraduationCap size={22} />} onClose={onClose}>
      {step < 3 ? (
        <ol className="stepper" aria-label="Registration steps">
          <li className={step >= 1 ? 'done' : ''}>Programme</li>
          <li className={step >= 2 ? 'done' : ''}>Your details</li>
          <li>Confirmed</li>
        </ol>
      ) : null}

      {step === 1 ? (
        <div className="course-pick">
          {courses.map((c) => (
            <button
              key={c.id}
              type="button"
              className={`course-option ${courseId === c.id ? 'active' : ''}`}
              disabled={c.seatsLeft === 0}
              onClick={() => setCourseId(c.id)}
            >
              <strong>{c.title}</strong>
              <span>{c.durationWeeks} weeks · {c.mode} · {rupees(c.priceInr)}</span>
              <small>{c.seatsLeft === 0 ? 'Cohort full' : `Starts ${formatDate(c.startDate)} · ${c.seatsLeft} seats left`}</small>
            </button>
          ))}
          <div className="modal-actions">
            <Button variant="secondary" onClick={onClose}>Cancel</Button>
            <Button disabled={!courseId} onClick={() => setStep(2)}>Continue</Button>
          </div>
        </div>
      ) : null}

      {step === 2 && course ? (
        <form className="form-stack" onSubmit={submit}>
          <div className="picked-course">
            <span>{course.title}</span>
            <button type="button" className="link-btn" onClick={() => setStep(1)}>Change</button>
          </div>
          <Field label="Full name" error={errors.name}>
            <input value={form.name} onChange={update('name')} autoComplete="name" required />
          </Field>
          <div className="form-row">
            <Field label="Email" error={errors.email}>
              <input type="email" value={form.email} onChange={update('email')} autoComplete="email" required />
            </Field>
            <Field label="WhatsApp number" error={errors.phone}>
              <input type="tel" value={form.phone} onChange={update('phone')} autoComplete="tel" placeholder="+91" required />
            </Field>
          </div>
          <Field label="I am a" error={errors.background}>
            <div className="segmented wide">
              {[['STUDENT', 'Student'], ['PROFESSIONAL', 'Working professional']].map(([value, label]) => (
                <button
                  key={value}
                  type="button"
                  className={form.background === value ? 'active' : ''}
                  onClick={() => setForm((f) => ({ ...f, background: value }))}
                >
                  {label}
                </button>
              ))}
            </div>
          </Field>
          <div className="modal-actions">
            <Button variant="secondary" onClick={() => setStep(1)}>Back</Button>
            <Button type="submit" disabled={saving}>{saving ? 'Reserving...' : `Reserve seat · ${rupees(course.priceInr)}`}</Button>
          </div>
          <p className="fine-print">No payment now. You pay only after your counselling call.</p>
        </form>
      ) : null}

      {step === 3 && course ? (
        <div className="next-steps">
          <p className="modal-sub">
            You&apos;re on the list for <strong>{course.title}</strong>, starting {formatDate(course.startDate)}.
          </p>
          <ul>
            <li><MessageCircle size={18} /> A confirmation is on its way to {form.email} and WhatsApp.</li>
            <li><PhoneCall size={18} /> A mentor will call you within 24 hours.</li>
            <li><CalendarDays size={18} /> Your seat is held for 48 hours after the call.</li>
          </ul>
          <Button fullWidth onClick={onClose}>Done</Button>
        </div>
      ) : null}
    </Modal>
  );
}

export default RegisterModal;
