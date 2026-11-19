import { X } from 'lucide-react';

const RATINGS = [
  { value: '', label: 'Any' },
  { value: '4', label: '4.0+' },
  { value: '4.5', label: '4.5+' },
];

const FEES = [
  { value: '', label: 'Any fee' },
  { value: '0', label: 'Free' },
  { value: '500', label: 'Up to \u20b9500' },
  { value: '800', label: 'Up to \u20b9800' },
  { value: '1200', label: 'Up to \u20b91,200' },
];

const TOGGLES = [
  { key: 'open24x7', label: 'Open 24x7' },
  { key: 'emergency', label: 'Handles emergencies' },
  { key: 'homeVisit', label: 'Home visits' },
  { key: 'verified', label: 'Verified by PawCare' },
];

function FilterPanel({ filters, meta, onChange, onReset, open, onClose }) {
  return (
    <aside className={`filter-panel ${open ? 'open' : ''}`} aria-label="Filters">
      <div className="filter-head">
        <h3>Filters</h3>
        <button type="button" className="link-btn" onClick={onReset}>Clear all</button>
        <button type="button" className="filter-close" aria-label="Close filters" onClick={onClose}>
          <X size={20} />
        </button>
      </div>

      <div className="filter-group">
        <h4>City</h4>
        <select value={filters.city} onChange={(e) => onChange({ city: e.target.value })}>
          <option value="">All cities</option>
          {meta.cities.map((city) => (
            <option key={city} value={city}>{city}</option>
          ))}
        </select>
      </div>

      <div className="filter-group">
        <h4>Pet type</h4>
        <div className="chip-row">
          <button
            type="button"
            className={`chip ${filters.species === '' ? 'active' : ''}`}
            onClick={() => onChange({ species: '' })}
          >
            All
          </button>
          {meta.species.map((s) => (
            <button
              key={s}
              type="button"
              className={`chip ${filters.species === s ? 'active' : ''}`}
              onClick={() => onChange({ species: filters.species === s ? '' : s })}
            >
              {s}
            </button>
          ))}
        </div>
      </div>

      <div className="filter-group">
        <h4>Availability</h4>
        {TOGGLES.map((t) => (
          <label key={t.key} className="check-row">
            <input
              type="checkbox"
              checked={filters[t.key]}
              onChange={(e) => onChange({ [t.key]: e.target.checked })}
            />
            <span>{t.label}</span>
          </label>
        ))}
      </div>

      <div className="filter-group">
        <h4>Rating</h4>
        <div className="segmented">
          {RATINGS.map((r) => (
            <button
              key={r.label}
              type="button"
              className={filters.minRating === r.value ? 'active' : ''}
              onClick={() => onChange({ minRating: r.value })}
            >
              {r.label}
            </button>
          ))}
        </div>
      </div>

      <div className="filter-group">
        <h4>Starting fee</h4>
        <select value={filters.maxFee} onChange={(e) => onChange({ maxFee: e.target.value })}>
          {FEES.map((f) => (
            <option key={f.label} value={f.value}>{f.label}</option>
          ))}
        </select>
      </div>

      <button type="button" className="ui-btn ui-btn-primary ui-btn-full filter-apply" onClick={onClose}>
        Show results
      </button>
    </aside>
  );
}

export default FilterPanel;
