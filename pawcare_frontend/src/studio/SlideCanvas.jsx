import { forwardRef } from 'react';
import { ArrowRight, Bookmark, Heart, Send } from 'lucide-react';
import { SLIDE_H, SLIDE_W, THEMES } from './themes';
import { iconFor } from './slideIcons';

/**
 * One slide at its real export size (1080 x 1350, Instagram/LinkedIn 4:5).
 * The preview scales it down with CSS; the exporter captures it as-is, so the
 * PNG and the preview are pixel-identical. Styles are inline for that reason.
 */
const SlideCanvas = forwardRef(function SlideCanvas({ slide, index, total, theme, handle }, ref) {
  const t = THEMES[theme] || THEMES.SUNRISE;
  const Icon = iconFor(slide.icon);
  const isHook = slide.kind === 'HOOK';
  const isCta = slide.kind === 'CTA';
  const dark = isHook || theme === 'MIDNIGHT';
  const color = isHook ? t.hookText : t.text;
  const muted = isHook ? 'rgba(255,255,255,0.78)' : t.muted;

  const base = {
    width: SLIDE_W,
    height: SLIDE_H,
    position: 'relative',
    overflow: 'hidden',
    background: isHook ? t.hookBg : t.bg,
    color,
    fontFamily: "'Plus Jakarta Sans Variable', 'Inter Variable', sans-serif",
    padding: '110px 96px',
    boxSizing: 'border-box',
    display: 'flex',
    flexDirection: 'column',
  };

  const blob = (size, top, right, opacity) => ({
    position: 'absolute',
    width: size,
    height: size,
    borderRadius: '50%',
    top,
    right,
    background: isHook ? '#ffffff' : t.accent,
    opacity,
  });

  const footer = (
    <div style={{ position: 'absolute', left: 96, right: 96, bottom: 80, display: 'flex', alignItems: 'center', justifyContent: 'space-between', fontSize: 30, fontWeight: 600, color: muted }}>
      <span>{handle}</span>
      <span style={{ display: 'flex', alignItems: 'center', gap: 14 }}>
        {index + 1} / {total}
        {!isCta ? (
          <span style={{ display: 'inline-flex', alignItems: 'center', justifyContent: 'center', width: 64, height: 64, borderRadius: 32, background: isHook ? 'rgba(255,255,255,0.18)' : t.chip, color: isHook ? '#fff' : t.accent }}>
            <ArrowRight size={32} />
          </span>
        ) : null}
      </span>
    </div>
  );

  if (isHook) {
    return (
      <div ref={ref} style={base}>
        <div style={blob(520, -160, -160, 0.1)} />
        <div style={blob(260, 880, 700, 0.08)} />
        <div style={{ width: 132, height: 132, borderRadius: 36, background: 'rgba(255,255,255,0.16)', display: 'flex', alignItems: 'center', justifyContent: 'center', marginBottom: 72 }}>
          <Icon size={72} strokeWidth={1.8} />
        </div>
        <h1 style={{ fontSize: slide.headline.length > 60 ? 84 : 100, lineHeight: 1.04, fontWeight: 800, letterSpacing: '-0.035em', margin: 0, maxWidth: 880 }}>
          {slide.headline}
        </h1>
        {slide.body ? (
          <p style={{ fontSize: 40, lineHeight: 1.4, marginTop: 48, maxWidth: 800, color: 'rgba(255,255,255,0.88)', fontWeight: 500 }}>{slide.body}</p>
        ) : null}
        <div style={{ marginTop: 'auto', marginBottom: 90, display: 'inline-flex', alignSelf: 'flex-start', alignItems: 'center', gap: 14, padding: '18px 30px', borderRadius: 999, background: '#ffffff', color: t.accent, fontSize: 32, fontWeight: 700 }}>
          Swipe to read <ArrowRight size={30} />
        </div>
        {footer}
      </div>
    );
  }

  if (isCta) {
    return (
      <div ref={ref} style={{ ...base, alignItems: 'center', justifyContent: 'center', textAlign: 'center' }}>
        <div style={blob(600, -220, -220, dark ? 0.12 : 0.08)} />
        <div style={blob(380, 1050, 760, dark ? 0.1 : 0.07)} />
        <div style={{ width: 150, height: 150, borderRadius: 75, background: t.accent, color: t.accentText, display: 'flex', alignItems: 'center', justifyContent: 'center', marginBottom: 64 }}>
          <Icon size={76} strokeWidth={1.8} />
        </div>
        <h1 style={{ fontSize: 88, lineHeight: 1.08, fontWeight: 800, letterSpacing: '-0.03em', margin: 0, maxWidth: 860 }}>{slide.headline}</h1>
        {slide.body ? (
          <p style={{ fontSize: 42, lineHeight: 1.4, marginTop: 40, maxWidth: 820, color: t.muted, fontWeight: 600 }}>{slide.body}</p>
        ) : null}
        <div style={{ display: 'flex', gap: 28, marginTop: 72 }}>
          {[[Heart, 'Like'], [Send, 'Share'], [Bookmark, 'Save']].map(([I, label]) => (
            <span key={label} style={{ display: 'inline-flex', alignItems: 'center', gap: 12, padding: '18px 30px', borderRadius: 999, background: t.chip, color: t.accent, fontSize: 30, fontWeight: 700 }}>
              <I size={30} /> {label}
            </span>
          ))}
        </div>
        <div style={{ marginTop: 64, fontSize: 36, fontWeight: 800, color }}>{handle}</div>
      </div>
    );
  }

  const number = String(index).padStart(2, '0');
  return (
    <div ref={ref} style={base}>
      <div style={blob(420, -170, -150, dark ? 0.14 : 0.08)} />
      <div style={blob(300, 1120, 820, dark ? 0.1 : 0.06)} />
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <span style={{ fontSize: 150, fontWeight: 800, lineHeight: 1, letterSpacing: '-0.05em', color: t.accent, opacity: 0.9 }}>{number}</span>
        <span style={{ width: 120, height: 120, borderRadius: 32, background: t.chip, color: t.accent, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <Icon size={64} strokeWidth={1.8} />
        </span>
      </div>
      {/* The text block sits in the optical centre of the space below the number. */}
      <div style={{ margin: 'auto 0', paddingBottom: 120 }}>
        <h2 style={{ fontSize: slide.headline.length > 45 ? 76 : 92, lineHeight: 1.06, fontWeight: 800, letterSpacing: '-0.03em', margin: 0, maxWidth: 880 }}>
          {slide.headline}
        </h2>
        <div style={{ width: 120, height: 10, borderRadius: 5, background: t.accent, margin: '52px 0' }} />
        {slide.body ? (
          <p style={{ fontSize: 48, lineHeight: 1.42, margin: 0, maxWidth: 860, color: dark ? 'rgba(248,250,252,0.86)' : t.text, opacity: dark ? 1 : 0.86, fontWeight: 500 }}>{slide.body}</p>
        ) : null}
      </div>
      {footer}
    </div>
  );
});

export default SlideCanvas;
