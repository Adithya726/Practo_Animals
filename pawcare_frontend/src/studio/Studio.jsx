import { useEffect, useRef, useState } from 'react';
import {
  ArrowLeft, ArrowRight, Check, Copy, FileDown, ImageDown, Lightbulb, Plus, RotateCcw, Save, Sparkles, Trash2, Wand2,
} from 'lucide-react';
import PrototypeBar from '../components/common/PrototypeBar';
import Button from '../components/ui/Button';
import Field from '../components/ui/Field';
import { useToast } from '../components/ui/useToast';
import SlideCanvas from './SlideCanvas';
import { SLIDE_ICONS } from './slideIcons';
import { SLIDE_H, SLIDE_W, THEMES, THEME_KEYS } from './themes';
import { exportPdf, exportPngs } from './exportCarousel';
import { generateCarousel, getCarousel, getRecentCarousels, getWriter, updateCarousel } from '../services/carouselService';
import { getErrorMessage } from '../services/api';
import { formatDateTime } from '../utils/format';

const STEPS = ['Idea', 'Research & structure', 'Headlines & copy', 'Visual direction', 'Edit', 'Export'];

const WORKFLOW = [
  { title: 'Raw idea', text: 'One line plus optional notes, typed here or sent from a phone.' },
  { title: 'Research & structure', text: 'The writer (GPT or Claude) checks facts and plans a hook, one idea per slide and a CTA.' },
  { title: 'Headlines & copy', text: 'Short headlines, one-line support text, caption and hashtags.' },
  { title: 'Visual direction', text: 'Brand theme, icon and layout per slide, from a fixed design system.' },
  { title: 'Review & edit', text: 'Change any word, reorder, add or remove slides, switch theme.' },
  { title: 'Export & publish', text: 'PNGs for Instagram, a PDF for LinkedIn, then schedule in Buffer.' },
];

const EXAMPLE = {
  idea: '5 signs your dog needs a vet today',
  notes: [
    'Not eating for a day: skipping two meals in a row is worth a call, especially for puppies',
    'Repeated vomiting or diarrhoea: more than twice in a day, or any blood, needs a vet',
    'Breathing hard at rest: fast breathing, gasping or blue-grey gums are an emergency',
    'Sudden limping or crying when touched: pain is easy to miss in stoic dogs',
    'A swollen, hard belly: can mean bloat, which can turn fatal within hours',
  ].join('\n'),
  cta: 'Save this. Find a 24x7 vet near you on PawCare',
};

const EMPTY_FORM = { idea: '', notes: '', platform: 'INSTAGRAM', tone: 'FRIENDLY', theme: 'SUNRISE', slideCount: 7, cta: '' };

function Studio() {
  const toast = useToast();
  const [form, setForm] = useState(EMPTY_FORM);
  const [handle, setHandle] = useState(() => localStorage.getItem('studio_handle') || '@pawcare.in');
  const [writer, setWriter] = useState('');
  const [generating, setGenerating] = useState(false);
  const [carousel, setCarousel] = useState(null);
  const [selected, setSelected] = useState(0);
  const [dirty, setDirty] = useState(false);
  const [saving, setSaving] = useState(false);
  const [exporting, setExporting] = useState('');
  const [recent, setRecent] = useState([]);
  const exportRefs = useRef([]);

  useEffect(() => {
    getWriter().then(({ data }) => setWriter(data.writer)).catch(() => setWriter(''));
    getRecentCarousels().then(({ data }) => setRecent(data)).catch(() => setRecent([]));
  }, []);

  useEffect(() => {
    localStorage.setItem('studio_handle', handle);
  }, [handle]);

  // Warn before leaving with unsaved edits.
  useEffect(() => {
    if (!dirty) {
      return undefined;
    }
    const warn = (event) => {
      event.preventDefault();
    };
    window.addEventListener('beforeunload', warn);
    return () => window.removeEventListener('beforeunload', warn);
  }, [dirty]);

  const set = (key) => (event) => setForm((f) => ({ ...f, [key]: event.target.value }));

  const stage = generating ? 2 : carousel ? (dirty ? 4 : 5) : form.idea ? 1 : 0;

  const generate = async (event) => {
    event.preventDefault();
    setGenerating(true);
    try {
      const { data } = await generateCarousel({ ...form, slideCount: Number(form.slideCount) });
      setCarousel(data);
      setSelected(0);
      setDirty(false);
      setRecent((r) => [data, ...r.filter((c) => c.id !== data.id)].slice(0, 20));
      toast.success(`Drafted ${data.slides.length} slides`);
    } catch (error) {
      toast.error(getErrorMessage(error, 'Could not generate the carousel.'));
    } finally {
      setGenerating(false);
    }
  };

  const open = async (id) => {
    if (dirty && !window.confirm('Discard unsaved edits?')) {
      return;
    }
    try {
      const { data } = await getCarousel(id);
      setCarousel(data);
      setSelected(0);
      setDirty(false);
    } catch (error) {
      toast.error(getErrorMessage(error));
    }
  };

  const edit = (changes) => {
    setCarousel((c) => ({ ...c, ...changes }));
    setDirty(true);
  };

  const editSlide = (index, changes) => {
    edit({ slides: carousel.slides.map((s, i) => (i === index ? { ...s, ...changes } : s)) });
  };

  const moveSlide = (index, delta) => {
    const target = index + delta;
    if (target < 0 || target >= carousel.slides.length) {
      return;
    }
    const slides = [...carousel.slides];
    [slides[index], slides[target]] = [slides[target], slides[index]];
    edit({ slides });
    setSelected(target);
  };

  const removeSlide = (index) => {
    if (carousel.slides.length <= 3) {
      toast.error('A carousel needs at least 3 slides');
      return;
    }
    edit({ slides: carousel.slides.filter((_, i) => i !== index) });
    setSelected(Math.max(0, index - 1));
  };

  const addSlide = () => {
    if (carousel.slides.length >= 12) {
      toast.error('Keep carousels to 12 slides or fewer');
      return;
    }
    const slides = [...carousel.slides];
    const at = Math.min(selected + 1, slides.length - 1); // before the CTA
    slides.splice(at, 0, { kind: 'CONTENT', headline: 'New point', body: 'Add one clear idea here.', visualNote: '', icon: 'lightbulb' });
    edit({ slides });
    setSelected(at);
  };

  const save = async () => {
    if (carousel.slides.some((s) => !s.headline.trim())) {
      toast.error('Every slide needs a headline');
      return false;
    }
    setSaving(true);
    try {
      const { data } = await updateCarousel(carousel.id, {
        title: carousel.title,
        theme: carousel.theme,
        caption: carousel.caption,
        hashtags: carousel.hashtags,
        slides: carousel.slides,
      });
      setCarousel(data);
      setDirty(false);
      setRecent((r) => r.map((c) => (c.id === data.id ? data : c)));
      toast.success('Saved');
      return true;
    } catch (error) {
      toast.error(getErrorMessage(error, 'Could not save.'));
      return false;
    } finally {
      setSaving(false);
    }
  };

  const doExport = async (kind) => {
    if (dirty && !(await save())) {
      return;
    }
    setExporting(kind);
    try {
      const nodes = exportRefs.current.slice(0, carousel.slides.length).filter(Boolean);
      const count = kind === 'pdf' ? await exportPdf(nodes, carousel.title) : await exportPngs(nodes, carousel.title);
      toast.success(kind === 'pdf' ? `PDF with ${count} pages downloaded` : `${count} PNGs downloaded`);
    } catch {
      toast.error('Export failed. Try again in Chrome or Edge.');
    } finally {
      setExporting('');
    }
  };

  const copyCaption = async () => {
    try {
      await navigator.clipboard.writeText(`${carousel.caption || ''}\n\n${carousel.hashtags || ''}`.trim());
      toast.success('Caption copied');
    } catch {
      toast.error('Could not copy');
    }
  };

  const slide = carousel?.slides[selected];
  const scale = 0.3;

  return (
    <div className="studio">
      <PrototypeBar />
      <header className="studio-head">
        <div>
          <span className="studio-logo"><Wand2 size={18} /> Content Studio</span>
          <h1>Raw idea to ready-to-post carousel in under a minute</h1>
        </div>
        <span className={`writer-pill ${writer && writer !== 'TEMPLATE' ? 'ai' : ''}`}>
          <Sparkles size={14} /> Writer: {writer ? (writer === 'TEMPLATE' ? 'Built-in template (add OPENAI_API_KEY for AI)' : writer) : '...'}
        </span>
      </header>

      <ol className="flow-steps" aria-label="Workflow">
        {STEPS.map((s, i) => (
          <li key={s} className={i < stage ? 'done' : i === stage ? 'current' : ''}>
            <span>{i < stage ? <Check size={13} /> : i + 1}</span>{s}
          </li>
        ))}
      </ol>

      <div className="studio-grid">
        <aside className="studio-panel">
          <form className="form-stack" onSubmit={generate}>
            <div className="panel-title">
              <h2>1. Your idea</h2>
              <button type="button" className="link-btn" onClick={() => setForm((f) => ({ ...f, ...EXAMPLE }))}>
                <Lightbulb size={14} /> Use an example
              </button>
            </div>
            <Field label="Raw idea" hint="One line is enough. This becomes the hook.">
              <textarea rows={2} maxLength={300} value={form.idea} onChange={set('idea')} placeholder="e.g. How students can use AI to study smarter" required />
            </Field>
            <Field label="Rough notes" optional hint="One point per line. Use 'Headline: detail' to control each slide.">
              <textarea rows={5} maxLength={2000} value={form.notes} onChange={set('notes')} placeholder={'Not eating: skipping two meals is a red flag\nBreathing hard at rest: an emergency'} />
            </Field>
            <div className="form-row">
              <Field label="Platform">
                <select value={form.platform} onChange={set('platform')}>
                  <option value="INSTAGRAM">Instagram</option>
                  <option value="LINKEDIN">LinkedIn</option>
                </select>
              </Field>
              <Field label="Tone">
                <select value={form.tone} onChange={set('tone')}>
                  <option value="FRIENDLY">Friendly</option>
                  <option value="EXPERT">Expert</option>
                  <option value="BOLD">Bold</option>
                </select>
              </Field>
            </div>
            <Field label={`Slides: ${form.slideCount}`}>
              <input type="range" min="5" max="10" value={form.slideCount} onChange={set('slideCount')} />
            </Field>
            <Field label="Theme">
              <div className="theme-row">
                {THEME_KEYS.map((key) => (
                  <button
                    key={key}
                    type="button"
                    className={`theme-swatch ${form.theme === key ? 'active' : ''}`}
                    onClick={() => setForm((f) => ({ ...f, theme: key }))}
                    title={THEMES[key].label}
                    style={{ background: `linear-gradient(135deg, ${THEMES[key].swatch[0]} 50%, ${THEMES[key].swatch[1]} 50%)` }}
                    aria-label={THEMES[key].label}
                  />
                ))}
              </div>
            </Field>
            <Field label="Call to action" optional>
              <input maxLength={80} value={form.cta} onChange={set('cta')} placeholder="Follow for more pet care tips" />
            </Field>
            <Field label="Brand handle">
              <input maxLength={30} value={handle} onChange={(e) => setHandle(e.target.value)} />
            </Field>
            <Button type="submit" size="lg" fullWidth disabled={generating}>
              <Wand2 size={18} /> {generating ? 'Writing your slides...' : carousel ? 'Generate a new draft' : 'Generate carousel'}
            </Button>
          </form>

          {recent.length ? (
            <div className="recent">
              <h3>Recent</h3>
              {recent.slice(0, 6).map((c) => (
                <button key={c.id} type="button" className={`recent-item ${carousel?.id === c.id ? 'active' : ''}`} onClick={() => open(c.id)}>
                  <span className="recent-dot" style={{ background: THEMES[c.theme]?.swatch[0] }} />
                  <span>
                    <strong>{c.title}</strong>
                    <small>{c.slides.length} slides · {c.platform === 'LINKEDIN' ? 'LinkedIn' : 'Instagram'} · {formatDateTime(c.createdAt)}</small>
                  </span>
                </button>
              ))}
            </div>
          ) : null}
        </aside>

        <section className="studio-stage">
          {!carousel ? (
            <div className="stage-empty">
              <Wand2 size={34} />
              <h2>Your carousel appears here</h2>
              <p>Write an idea, or tap &quot;Use an example&quot;, then generate. You&apos;ll get a hook, one point per slide and a call to action, ready to edit and export.</p>
              <div className="workflow-map">
                {WORKFLOW.map((w, i) => (
                  <div key={w.title} className="wf-step">
                    <em>Step {i + 1}</em>
                    <b>{w.title}</b>
                    <span>{w.text}</span>
                  </div>
                ))}
              </div>
            </div>
          ) : (
            <>
              <div className="stage-toolbar">
                <input
                  className="title-input"
                  value={carousel.title}
                  onChange={(e) => edit({ title: e.target.value })}
                  aria-label="Carousel title"
                />
                <div className="theme-row small">
                  {THEME_KEYS.map((key) => (
                    <button
                      key={key}
                      type="button"
                      className={`theme-swatch ${carousel.theme === key ? 'active' : ''}`}
                      onClick={() => edit({ theme: key })}
                      title={THEMES[key].label}
                      style={{ background: `linear-gradient(135deg, ${THEMES[key].swatch[0]} 50%, ${THEMES[key].swatch[1]} 50%)` }}
                      aria-label={THEMES[key].label}
                    />
                  ))}
                </div>
                <Button variant="secondary" size="sm" onClick={save} disabled={!dirty || saving}>
                  <Save size={15} /> {saving ? 'Saving' : dirty ? 'Save' : 'Saved'}
                </Button>
              </div>

              <div className="slide-strip" role="listbox" aria-label="Slides">
                {carousel.slides.map((s, i) => (
                  <button
                    key={`${i}-${s.kind}`}
                    type="button"
                    role="option"
                    aria-selected={selected === i}
                    className={`slide-thumb ${selected === i ? 'active' : ''}`}
                    style={{ width: SLIDE_W * scale, height: SLIDE_H * scale }}
                    onClick={() => setSelected(i)}
                  >
                    <div style={{ transform: `scale(${scale})`, transformOrigin: 'top left' }}>
                      <SlideCanvas slide={s} index={i} total={carousel.slides.length} theme={carousel.theme} handle={handle} />
                    </div>
                  </button>
                ))}
                <button type="button" className="slide-add" onClick={addSlide} style={{ height: SLIDE_H * scale }}>
                  <Plus size={22} /> Add slide
                </button>
              </div>

              {slide ? (
                <div className="editor-grid">
                  <div className="panel slide-editor">
                    <div className="panel-title">
                      <h2>Slide {selected + 1} <span className="kind-pill">{slide.kind}</span></h2>
                      <div className="icon-actions">
                        <button type="button" aria-label="Move left" onClick={() => moveSlide(selected, -1)} disabled={selected === 0}><ArrowLeft size={16} /></button>
                        <button type="button" aria-label="Move right" onClick={() => moveSlide(selected, 1)} disabled={selected === carousel.slides.length - 1}><ArrowRight size={16} /></button>
                        <button type="button" aria-label="Delete slide" onClick={() => removeSlide(selected)}><Trash2 size={16} /></button>
                      </div>
                    </div>
                    <Field label={`Headline (${slide.headline.length}/160)`}>
                      <input maxLength={160} value={slide.headline} onChange={(e) => editSlide(selected, { headline: e.target.value })} />
                    </Field>
                    <Field label="Text">
                      <textarea rows={3} maxLength={600} value={slide.body || ''} onChange={(e) => editSlide(selected, { body: e.target.value })} />
                    </Field>
                    <div className="form-row">
                      <Field label="Layout">
                        <select value={slide.kind} onChange={(e) => editSlide(selected, { kind: e.target.value })}>
                          <option value="HOOK">Hook (cover)</option>
                          <option value="CONTENT">Content</option>
                          <option value="CTA">Call to action</option>
                        </select>
                      </Field>
                      <Field label="Icon">
                        <select value={slide.icon || 'sparkles'} onChange={(e) => editSlide(selected, { icon: e.target.value })}>
                          {Object.keys(SLIDE_ICONS).map((name) => <option key={name} value={name}>{name}</option>)}
                        </select>
                      </Field>
                    </div>
                    {slide.visualNote ? <p className="visual-note"><Sparkles size={14} /> Art direction: {slide.visualNote}</p> : null}
                  </div>

                  <div className="panel publish-panel">
                    <h2>Caption</h2>
                    <textarea rows={5} maxLength={600} value={carousel.caption || ''} onChange={(e) => edit({ caption: e.target.value })} />
                    <input value={carousel.hashtags || ''} maxLength={300} onChange={(e) => edit({ hashtags: e.target.value })} aria-label="Hashtags" />
                    <Button variant="ghost" size="sm" onClick={copyCaption}><Copy size={15} /> Copy caption</Button>
                    <div className="export-row">
                      <Button onClick={() => doExport('png')} disabled={Boolean(exporting)}>
                        <ImageDown size={16} /> {exporting === 'png' ? 'Exporting...' : 'PNGs for Instagram'}
                      </Button>
                      <Button variant="dark" onClick={() => doExport('pdf')} disabled={Boolean(exporting)}>
                        <FileDown size={16} /> {exporting === 'pdf' ? 'Exporting...' : 'PDF for LinkedIn'}
                      </Button>
                    </div>
                    <p className="fine-print">
                      <RotateCcw size={12} /> Generated by {carousel.generatedBy === 'TEMPLATE' ? 'the template writer' : carousel.generatedBy}. Edits are saved to your library.
                    </p>
                  </div>
                </div>
              ) : null}

              {/* Full-size copies used only for export, kept off-screen. */}
              <div className="export-stage" aria-hidden="true">
                {carousel.slides.map((s, i) => (
                  <SlideCanvas
                    key={`export-${i}`}
                    ref={(node) => { exportRefs.current[i] = node; }}
                    slide={s}
                    index={i}
                    total={carousel.slides.length}
                    theme={carousel.theme}
                    handle={handle}
                  />
                ))}
              </div>
            </>
          )}
        </section>
      </div>
    </div>
  );
}

export default Studio;
