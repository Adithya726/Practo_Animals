/** Visual themes for carousel slides. Colours are inline so exported PNGs match the preview exactly. */
export const THEMES = {
  SUNRISE: {
    label: 'Sunrise',
    bg: 'linear-gradient(160deg, #fff7ed 0%, #ffedd5 55%, #fed7aa 100%)',
    hookBg: 'linear-gradient(150deg, #f97316 0%, #ea580c 55%, #c2410c 100%)',
    text: '#431407',
    hookText: '#fff7ed',
    muted: '#9a3412',
    accent: '#ea580c',
    accentText: '#ffffff',
    chip: 'rgba(234, 88, 12, 0.12)',
    swatch: ['#f97316', '#ffedd5'],
  },
  FOREST: {
    label: 'Forest',
    bg: 'linear-gradient(160deg, #f0fdfa 0%, #ccfbf1 100%)',
    hookBg: 'linear-gradient(150deg, #115e59 0%, #0f766e 60%, #14b8a6 100%)',
    text: '#042f2e',
    hookText: '#f0fdfa',
    muted: '#0f766e',
    accent: '#0f766e',
    accentText: '#ffffff',
    chip: 'rgba(15, 118, 110, 0.12)',
    swatch: ['#0f766e', '#ccfbf1'],
  },
  MIDNIGHT: {
    label: 'Midnight',
    bg: 'linear-gradient(160deg, #0f172a 0%, #1e1b4b 100%)',
    hookBg: 'linear-gradient(150deg, #312e81 0%, #4c1d95 60%, #7c3aed 100%)',
    text: '#f8fafc',
    hookText: '#ffffff',
    muted: '#a5b4fc',
    accent: '#a78bfa',
    accentText: '#1e1b4b',
    chip: 'rgba(167, 139, 250, 0.16)',
    swatch: ['#1e1b4b', '#a78bfa'],
  },
  CANDY: {
    label: 'Candy',
    bg: 'linear-gradient(160deg, #fdf2f8 0%, #f5f3ff 100%)',
    hookBg: 'linear-gradient(150deg, #ec4899 0%, #a855f7 100%)',
    text: '#3b0764',
    hookText: '#ffffff',
    muted: '#9d174d',
    accent: '#db2777',
    accentText: '#ffffff',
    chip: 'rgba(219, 39, 119, 0.12)',
    swatch: ['#ec4899', '#f5f3ff'],
  },
};

export const THEME_KEYS = Object.keys(THEMES);

export const SLIDE_W = 1080;
export const SLIDE_H = 1350;
