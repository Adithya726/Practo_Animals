import { useCallback, useEffect, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import {
  ArrowRight, Bot, CalendarDays, Check, ChevronDown, CircleCheck, Clock3, Laptop, Menu, Sparkles, X, Zap,
} from 'lucide-react';
import PrototypeBar from '../components/common/PrototypeBar';
import Button from '../components/ui/Button';
import Field from '../components/ui/Field';
import { Skeleton } from '../components/ui/Skeleton';
import { useToast } from '../components/ui/useToast';
import RegisterModal from './RegisterModal';
import { AUDIENCES, FAQS, JOURNEY, PROJECTS, VALUE_PROPS } from './academyData';
import { getCourses, submitLead } from '../services/courseService';
import { getErrorMessage, getFieldErrors } from '../services/api';
import { daysUntil, formatDate, rupees } from '../utils/format';

const EMPTY_ENQUIRY = { name: '', email: '', phone: '', background: 'STUDENT', courseId: '', message: '' };

function Academy() {
  const toast = useToast();
  const [params] = useSearchParams();
  // utm_source lets the founder see which post or ad a lead came from.
  const source = params.get('utm_source') || 'website';

  const [courses, setCourses] = useState(null);
  const [loadError, setLoadError] = useState('');
  const [audience, setAudience] = useState('STUDENT');
  const [openFaq, setOpenFaq] = useState(0);
  const [register, setRegister] = useState({ open: false, courseId: null });
  const [menuOpen, setMenuOpen] = useState(false);

  const [enquiry, setEnquiry] = useState(EMPTY_ENQUIRY);
  const [errors, setErrors] = useState({});
  const [sending, setSending] = useState(false);
  const [sent, setSent] = useState(false);

  const loadCourses = useCallback(() => {
    getCourses()
      .then(({ data }) => setCourses(data))
      .catch((error) => setLoadError(getErrorMessage(error, 'Could not load programmes.')));
  }, []);

  useEffect(() => {
    loadCourses();
  }, [loadCourses]);

  const next = courses?.find((c) => c.seatsLeft > 0);
  const openRegister = (courseId = null) => setRegister({ open: true, courseId });

  const updateEnquiry = (key) => (event) => setEnquiry((current) => ({ ...current, [key]: event.target.value }));

  const sendEnquiry = async (event) => {
    event.preventDefault();
    setSending(true);
    setErrors({});
    try {
      await submitLead({
        ...enquiry,
        courseId: enquiry.courseId ? Number(enquiry.courseId) : null,
        type: 'ENQUIRY',
        source,
      });
      setSent(true);
    } catch (error) {
      setErrors(getFieldErrors(error));
      toast.error(getErrorMessage(error, 'Could not send your enquiry.'));
    } finally {
      setSending(false);
    }
  };

  const AudienceIcon = AUDIENCES[audience].icon;

  return (
    <div className="academy">
      <PrototypeBar />
      <header className="ac-nav">
        <div className="container ac-nav-inner">
          <Link to="/academy" className="ac-brand"><span><Sparkles size={16} /></span>Forge<b>AI</b> Academy</Link>
          <nav className={`ac-links ${menuOpen ? 'open' : ''}`} onClick={() => setMenuOpen(false)}>
            <a href="#programmes">Programmes</a>
            <a href="#journey">How it works</a>
            <a href="#faq">FAQ</a>
            <a href="#enquire" className="ac-link-strong">Talk to a counsellor</a>
          </nav>
          <Button size="sm" className="ac-nav-cta" onClick={() => openRegister()}>Reserve a seat</Button>
          <button type="button" className="nav-toggle light" aria-label="Menu" onClick={() => setMenuOpen((v) => !v)}>
            {menuOpen ? <X size={22} /> : <Menu size={22} />}
          </button>
        </div>
      </header>

      <section className="ac-hero">
        <div className="container ac-hero-grid">
          <div>
            {next ? (
              <span className="ac-badge">
                <CalendarDays size={14} /> Next cohort starts {formatDate(next.startDate)} · {next.seatsLeft} seats left
              </span>
            ) : null}
            <h1>Learn AI by <span>building real things.</span></h1>
            <p className="ac-sub">
              Practical, live programmes in AI and digital skills for students and young professionals.
              Ship real projects every week and leave with a portfolio that gets you noticed.
            </p>
            <ul className="ac-ticks">
              <li><Check size={16} /> No coding needed to start</li>
              <li><Check size={16} /> Live mentors, small cohorts</li>
              <li><Check size={16} /> Portfolio + certificate</li>
            </ul>
            <div className="ac-cta-row">
              <a href="#programmes" className="ui-btn ui-btn-accent ui-btn-lg">Explore programmes <ArrowRight size={18} /></a>
              <a href="#enquire" className="ui-btn ui-btn-ghost-light ui-btn-lg">Book a free counselling call</a>
            </div>
          </div>

          <div className="ac-visual" aria-hidden="true">
            <div className="ac-window">
              <div className="ac-window-bar"><i /><i /><i /><span>week-3-project.ai</span></div>
              <div className="ac-chat user">Turn my class notes into a 10-question quiz with answers</div>
              <div className="ac-chat bot"><Bot size={16} /> Done. 10 questions, 3 levels of difficulty, answers at the end. Want flashcards too?</div>
              <div className="ac-flow">
                <span>Notes</span><Zap size={14} /><span>Claude</span><Zap size={14} /><span>Quiz</span><Zap size={14} /><span>Sheets</span>
              </div>
            </div>
            <div className="ac-float"><CircleCheck size={18} /> Project shipped</div>
          </div>
        </div>
      </section>

      <section className="ac-tools">
        <div className="container">
          <span>Tools you&apos;ll master</span>
          <div className="ac-tool-row">
            {['ChatGPT', 'Claude', 'Gemini', 'Perplexity', 'n8n', 'Make', 'Canva', 'Figma', 'Lovable', 'Notion AI'].map((t) => (
              <b key={t}>{t}</b>
            ))}
          </div>
        </div>
      </section>

      <section className="container ac-section">
        <div className="ac-props">
          {VALUE_PROPS.map(({ icon: Icon, title, text }) => (
            <div key={title} className="ac-prop">
              <Icon size={22} />
              <h3>{title}</h3>
              <p>{text}</p>
            </div>
          ))}
        </div>
      </section>

      <section className="container ac-section" id="programmes">
        <div className="section-head center">
          <div>
            <span className="eyebrow violet">Programmes</span>
            <h2>Pick the track that fits where you are</h2>
          </div>
        </div>
        {loadError ? <p className="error-text center">{loadError}</p> : null}
        <div className="course-grid">
          {courses === null && !loadError
            ? [0, 1, 2].map((i) => <Skeleton key={i} height={460} style={{ borderRadius: 22 }} />)
            : (courses ?? []).map((course, index) => {
                const taken = course.seatsTotal - course.seatsLeft;
                const startsIn = daysUntil(course.startDate);
                return (
                  <article key={course.id} className={`course-card ${index === 1 ? 'featured' : ''}`}>
                    {index === 1 ? <span className="course-flag">Most popular</span> : null}
                    <div className="course-meta">
                      <span>{course.level}</span>
                      <span>{course.audience === 'BOTH' ? 'Students & professionals' : course.audience === 'STUDENT' ? 'For students' : 'For professionals'}</span>
                    </div>
                    <h3>{course.title}</h3>
                    <p className="course-tagline">{course.tagline}</p>
                    <div className="course-facts">
                      <span><Clock3 size={15} /> {course.durationWeeks} weeks · {course.hoursPerWeek} hrs/week</span>
                      <span><Laptop size={15} /> {course.mode}</span>
                      <span><CalendarDays size={15} /> Starts {formatDate(course.startDate)}{startsIn > 0 ? ` (in ${startsIn} days)` : ''}</span>
                    </div>
                    <ul className="course-outcomes">
                      {course.outcomes.map((o) => <li key={o}><Check size={15} /> {o}</li>)}
                    </ul>
                    <div className="seat-bar" aria-label={`${course.seatsLeft} of ${course.seatsTotal} seats left`}>
                      <div style={{ width: `${Math.min(100, (taken / course.seatsTotal) * 100)}%` }} />
                    </div>
                    <p className="seat-note">{course.seatsLeft === 0 ? 'This cohort is full' : `${course.seatsLeft} of ${course.seatsTotal} seats left`}</p>
                    <div className="course-price">
                      <strong>{rupees(course.priceInr)}</strong>
                      <span>or no-cost EMI</span>
                    </div>
                    <Button fullWidth variant={index === 1 ? 'accent' : 'primary-violet'} disabled={course.seatsLeft === 0} onClick={() => openRegister(course.id)}>
                      Reserve your seat
                    </Button>
                    <a href="#enquire" className="course-ask" onClick={() => setEnquiry((e) => ({ ...e, courseId: String(course.id) }))}>
                      Have a question? Ask a mentor
                    </a>
                  </article>
                );
              })}
        </div>
      </section>

      <section className="ac-band">
        <div className="container ac-split">
          <div>
            <span className="eyebrow violet">Who it&apos;s for</span>
            <h2>Built for where you are right now</h2>
            <div className="segmented wide light">
              {Object.entries(AUDIENCES).map(([key, a]) => (
                <button key={key} type="button" className={audience === key ? 'active' : ''} onClick={() => setAudience(key)}>
                  {a.title}
                </button>
              ))}
            </div>
            <div className="audience-card">
              <AudienceIcon size={26} />
              <ul>
                {AUDIENCES[audience].points.map((p) => <li key={p}><Check size={16} /> {p}</li>)}
              </ul>
            </div>
          </div>
          <div>
            <span className="eyebrow violet">What you&apos;ll build</span>
            <div className="project-grid">
              {PROJECTS.map((p, i) => (
                <div key={p.title} className="project">
                  <span>0{i + 1}</span>
                  <h4>{p.title}</h4>
                  <p>{p.text}</p>
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>

      <section className="container ac-section" id="journey">
        <div className="section-head center">
          <div>
            <span className="eyebrow violet">How it works</span>
            <h2>From enquiry to demo day</h2>
          </div>
        </div>
        <ol className="journey">
          {JOURNEY.map((j, i) => (
            <li key={j.title}>
              <span>{i + 1}</span>
              <h4>{j.title}</h4>
              <p>{j.text}</p>
            </li>
          ))}
        </ol>
      </section>

      <section className="container ac-section ac-split faq-split" id="faq">
        <div>
          <span className="eyebrow violet">FAQ</span>
          <h2>Questions, answered</h2>
          <p className="muted">Still unsure? Leave your number and a mentor will call you back.</p>
        </div>
        <div className="faq">
          {FAQS.map((f, i) => (
            <div key={f.q} className={`faq-item ${openFaq === i ? 'open' : ''}`}>
              <button type="button" aria-expanded={openFaq === i} onClick={() => setOpenFaq(openFaq === i ? -1 : i)}>
                {f.q} <ChevronDown size={18} />
              </button>
              {openFaq === i ? <p>{f.a}</p> : null}
            </div>
          ))}
        </div>
      </section>

      <section className="ac-enquire" id="enquire">
        <div className="container ac-split">
          <div className="enquire-copy">
            <h2>Not sure which track? Talk to a mentor, free.</h2>
            <p>Tell us a little about yourself. A mentor will call within 24 hours to help you choose, with no pressure to buy.</p>
            <ul className="ac-ticks light">
              <li><Check size={16} /> 15-minute call, at a time that suits you</li>
              <li><Check size={16} /> Honest advice, even if it&apos;s &quot;not yet&quot;</li>
            </ul>
          </div>
          <div className="panel enquire-card">
            {sent ? (
              <div className="sent">
                <CircleCheck size={40} />
                <h3>Thanks, {enquiry.name.split(' ')[0]}!</h3>
                <p>A mentor will call you on {enquiry.phone} within 24 hours.</p>
                <Button variant="secondary" onClick={() => { setSent(false); setEnquiry(EMPTY_ENQUIRY); }}>Send another</Button>
              </div>
            ) : (
              <form className="form-stack" onSubmit={sendEnquiry} noValidate>
                <Field label="Full name" error={errors.name}>
                  <input value={enquiry.name} onChange={updateEnquiry('name')} autoComplete="name" />
                </Field>
                <div className="form-row">
                  <Field label="Email" error={errors.email}>
                    <input type="email" value={enquiry.email} onChange={updateEnquiry('email')} autoComplete="email" />
                  </Field>
                  <Field label="Phone" error={errors.phone}>
                    <input type="tel" value={enquiry.phone} onChange={updateEnquiry('phone')} placeholder="+91" autoComplete="tel" />
                  </Field>
                </div>
                <div className="form-row">
                  <Field label="I am a" error={errors.background}>
                    <select value={enquiry.background} onChange={updateEnquiry('background')}>
                      <option value="STUDENT">Student</option>
                      <option value="PROFESSIONAL">Working professional</option>
                    </select>
                  </Field>
                  <Field label="Interested in" optional>
                    <select value={enquiry.courseId} onChange={updateEnquiry('courseId')}>
                      <option value="">Not sure yet</option>
                      {(courses ?? []).map((c) => <option key={c.id} value={c.id}>{c.title}</option>)}
                    </select>
                  </Field>
                </div>
                <Field label="Anything we should know?" optional error={errors.message}>
                  <textarea rows={2} maxLength={500} value={enquiry.message} onChange={updateEnquiry('message')} placeholder="Your goals, best time to call..." />
                </Field>
                <Button type="submit" variant="accent" size="lg" fullWidth disabled={sending}>
                  {sending ? 'Sending...' : 'Request a callback'}
                </Button>
              </form>
            )}
          </div>
        </div>
      </section>

      <footer className="ac-footer">
        <div className="container">
          <span>&copy; ForgeAI Academy · A prototype for the AI education landing page</span>
          <Link to="/">Visit PawCare</Link>
        </div>
      </footer>

      <div className="mobile-cta violet">
        <a href="#enquire" className="ui-btn ui-btn-secondary">Ask a mentor</a>
        <Button variant="accent" onClick={() => openRegister()}>Reserve a seat</Button>
      </div>

      {register.open ? (
      <RegisterModal
        key={register.courseId ?? 'any'}
        open
        courses={courses ?? []}
        initialCourseId={register.courseId}
        source={source}
        onClose={() => setRegister({ open: false, courseId: null })}
        onRegistered={loadCourses}
      />
      ) : null}
    </div>
  );
}

export default Academy;
