import { useEffect, useMemo, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { ChevronLeft, ChevronRight, MapPin, Search, SearchX, SlidersHorizontal, Store, X } from 'lucide-react';
import ProviderCard from './ProviderCard';
import FilterPanel from './FilterPanel';
import CorrectionModal from './CorrectionModal';
import EmptyState from '../components/common/EmptyState';
import { CATEGORY_META } from '../components/common/categoryMeta';
import { Skeleton } from '../components/ui/Skeleton';
import { getSearchMeta, searchProviders } from '../services/providerService';
import { getErrorMessage } from '../services/api';
import { pluralize } from '../utils/format';

const DEFAULTS = {
  q: '',
  city: '',
  category: '',
  species: '',
  open24x7: false,
  emergency: false,
  homeVisit: false,
  verified: false,
  minRating: '',
  maxFee: '',
  sort: 'relevance',
  page: 0,
};

const BOOLEAN_KEYS = ['open24x7', 'emergency', 'homeVisit', 'verified'];

/** The URL is the single source of truth for filters, so every search can be shared or bookmarked. */
function readFilters(params) {
  const filters = { ...DEFAULTS };
  Object.keys(DEFAULTS).forEach((key) => {
    const value = params.get(key);
    if (value === null) {
      return;
    }
    if (BOOLEAN_KEYS.includes(key)) {
      filters[key] = value === 'true';
    } else if (key === 'page') {
      filters.page = Math.max(0, Number.parseInt(value, 10) || 0);
    } else {
      filters[key] = value;
    }
  });
  return filters;
}

function toParams(filters) {
  const params = {};
  Object.entries(filters).forEach(([key, value]) => {
    if (value === DEFAULTS[key] || value === '' || value === false) {
      return;
    }
    params[key] = String(value);
  });
  return params;
}

const EMPTY_META = { categories: [], cities: [], species: [], totalProviders: 0 };

function SearchPage() {
  const [params, setParams] = useSearchParams();
  const filters = useMemo(() => readFilters(params), [params]);

  const [meta, setMeta] = useState(EMPTY_META);
  // Each response remembers which query it answers, so "loading" is derived, not stored.
  const [response, setResponse] = useState({ key: null, data: null, error: '' });
  const [filtersOpen, setFiltersOpen] = useState(false);
  const requestKey = params.toString();
  const loading = response.key !== requestKey;
  const result = response.data;
  const error = loading ? '' : response.error;
  const [reporting, setReporting] = useState(null);

  useEffect(() => {
    getSearchMeta()
      .then(({ data }) => setMeta(data))
      .catch(() => setMeta(EMPTY_META));
  }, []);

  useEffect(() => {
    let cancelled = false;
    searchProviders({ ...filters, size: 12 })
      .then(({ data }) => {
        if (!cancelled) {
          setResponse({ key: requestKey, data, error: '' });
        }
      })
      .catch((err) => {
        if (!cancelled) {
          setResponse((prev) => ({ key: requestKey, data: prev.data, error: getErrorMessage(err, 'Could not load providers.') }));
        }
      });
    return () => {
      cancelled = true;
    };
  }, [filters, requestKey]);

  const update = (changes) => {
    // Any filter change starts again from the first page.
    setParams(toParams({ ...filters, page: 0, ...changes }));
  };

  const goToPage = (page) => {
    setParams(toParams({ ...filters, page }));
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const submitSearch = (event) => {
    event.preventDefault();
    update({ q: String(new FormData(event.currentTarget).get('q') || '').trim() });
  };

  const activeChips = [
    filters.q && { key: 'q', label: `"${filters.q}"` },
    filters.city && { key: 'city', label: filters.city },
    filters.species && { key: 'species', label: filters.species },
    filters.open24x7 && { key: 'open24x7', label: 'Open 24x7' },
    filters.emergency && { key: 'emergency', label: 'Emergency' },
    filters.homeVisit && { key: 'homeVisit', label: 'Home visit' },
    filters.verified && { key: 'verified', label: 'Verified' },
    filters.minRating && { key: 'minRating', label: `${filters.minRating}+ rating` },
    filters.maxFee && { key: 'maxFee', label: filters.maxFee === '0' ? 'Free' : `Up to \u20b9${filters.maxFee}` },
  ].filter(Boolean);

  const categoryLabel = meta.categories.find((c) => c.category === filters.category)?.label;
  const heading = `${categoryLabel || 'Animal care'}${filters.city ? ` in ${filters.city}` : ' near you'}`;

  return (
    <div className="search-page">
      <section className="search-hero">
        <div className="container">
          <form className="search-bar compact" onSubmit={submitSearch} role="search">
            <div className="search-field grow">
              <Search size={18} />
              <input
                key={filters.q}
                name="q"
                defaultValue={filters.q}
                placeholder="Search clinics, services like X-ray, or an area"
                aria-label="Search"
              />
            </div>
            <div className="search-field city">
              <MapPin size={18} />
              <select value={filters.city} onChange={(e) => update({ city: e.target.value })} aria-label="City">
                <option value="">All cities</option>
                {meta.cities.map((city) => (
                  <option key={city} value={city}>{city}</option>
                ))}
              </select>
            </div>
            <button type="submit" className="ui-btn ui-btn-primary ui-btn-lg">Search</button>
          </form>

          <div className="category-tabs" role="tablist" aria-label="Categories">
            <button
              type="button"
              role="tab"
              aria-selected={!filters.category}
              className={`cat-tab ${!filters.category ? 'active' : ''}`}
              onClick={() => update({ category: '' })}
            >
              All <span>{meta.totalProviders}</span>
            </button>
            {meta.categories.map((c) => {
              const Icon = CATEGORY_META[c.category]?.icon;
              return (
                <button
                  key={c.category}
                  type="button"
                  role="tab"
                  aria-selected={filters.category === c.category}
                  className={`cat-tab ${filters.category === c.category ? 'active' : ''}`}
                  onClick={() => update({ category: c.category })}
                >
                  {Icon ? <Icon size={15} /> : null} {c.label} <span>{c.count}</span>
                </button>
              );
            })}
          </div>
        </div>
      </section>

      <div className="container search-layout">
        <FilterPanel
          filters={filters}
          meta={meta}
          onChange={update}
          onReset={() => setParams({})}
          open={filtersOpen}
          onClose={() => setFiltersOpen(false)}
        />
        {filtersOpen ? <div className="drawer-backdrop" onClick={() => setFiltersOpen(false)} /> : null}

        <section className="results">
          <div className="results-head">
            <div>
              <h1>{heading}</h1>
              <p className="muted">
                {loading && !result ? 'Searching...' : pluralize(result?.totalItems ?? 0, 'result')}
              </p>
            </div>
            <div className="results-tools">
              <button type="button" className="ui-btn ui-btn-secondary filter-toggle" onClick={() => setFiltersOpen(true)}>
                <SlidersHorizontal size={16} /> Filters {activeChips.length ? `(${activeChips.length})` : ''}
              </button>
              <label className="sort-select">
                <span>Sort</span>
                <select value={filters.sort} onChange={(e) => update({ sort: e.target.value })}>
                  <option value="relevance">Recommended</option>
                  <option value="rating">Top rated</option>
                  <option value="reviews">Most reviewed</option>
                  <option value="fee">Lowest fee</option>
                </select>
              </label>
            </div>
          </div>

          {activeChips.length ? (
            <div className="active-chips">
              {activeChips.map((chip) => (
                <button
                  key={chip.key}
                  type="button"
                  className="active-chip"
                  onClick={() => update({ [chip.key]: DEFAULTS[chip.key] })}
                >
                  {chip.label} <X size={13} />
                </button>
              ))}
            </div>
          ) : null}

          {error ? (
            <EmptyState icon={<SearchX size={28} />} title="Search is unavailable" text={error} />
          ) : !result ? (
            <div className="provider-grid">
              {[0, 1, 2, 3, 4, 5].map((i) => (
                <Skeleton key={i} height={250} style={{ borderRadius: 18 }} />
              ))}
            </div>
          ) : result.items.length === 0 ? (
            <EmptyState
              icon={<SearchX size={28} />}
              title="No matches yet"
              text="Try removing a filter or searching a nearby city."
              action={
                <button type="button" className="ui-btn ui-btn-secondary" onClick={() => setParams({})}>
                  Clear filters
                </button>
              }
            />
          ) : (
            <>
              <div className={`provider-grid ${loading ? 'is-refreshing' : ''}`}>
                {result.items.map((provider) => (
                  <ProviderCard key={provider.id} provider={provider} onReport={setReporting} />
                ))}
              </div>

              {result.totalPages > 1 ? (
                <nav className="pagination" aria-label="Pages">
                  <button
                    type="button"
                    className="ui-btn ui-btn-secondary ui-btn-sm"
                    disabled={result.page === 0}
                    onClick={() => goToPage(result.page - 1)}
                  >
                    <ChevronLeft size={16} /> Previous
                  </button>
                  <span>
                    Page {result.page + 1} of {result.totalPages}
                  </span>
                  <button
                    type="button"
                    className="ui-btn ui-btn-secondary ui-btn-sm"
                    disabled={result.page + 1 >= result.totalPages}
                    onClick={() => goToPage(result.page + 1)}
                  >
                    Next <ChevronRight size={16} />
                  </button>
                </nav>
              ) : null}
            </>
          )}

          <div className="list-cta">
            <Store size={22} />
            <div>
              <strong>Run a clinic, shelter or boarding?</strong>
              <span>Get found by pet parents in your city. Listing is free.</span>
            </div>
            <Link to="/list-your-business" className="ui-btn ui-btn-dark">List Your Business</Link>
          </div>
        </section>
      </div>

      <CorrectionModal provider={reporting} onClose={() => setReporting(null)} />
    </div>
  );
}

export default SearchPage;
