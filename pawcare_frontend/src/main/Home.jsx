import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  ArrowRight, BadgeCheck, Clock3, Flag, HeartHandshake, House, MapPin, PawPrint, PhoneCall, Search, ShieldCheck, Siren,
  SlidersHorizontal, Store,
} from 'lucide-react';
import CategoryIcon from '../components/common/CategoryIcon';
import { CATEGORY_META } from '../components/common/categoryMeta';
import ProviderCard from '../search/ProviderCard';
import CorrectionModal from '../search/CorrectionModal';
import { Skeleton } from '../components/ui/Skeleton';
import { getFeaturedProviders, getSearchMeta } from '../services/providerService';

const QUICK = [
  { label: 'Open 24x7', icon: Clock3, to: '/search?open24x7=true' },
  { label: 'Home visit vets', icon: House, to: '/search?homeVisit=true' },
  { label: 'Vaccination', icon: ShieldCheck, to: '/search?q=vaccination' },
  { label: 'Adopt a pet', icon: HeartHandshake, to: '/search?category=NGO' },
];

const STEPS = [
  { icon: Search, title: 'Search', text: 'Pick the care you need and your city. Filters for pet type, 24x7, home visits, fee and rating.' },
  { icon: SlidersHorizontal, title: 'Compare', text: 'Verified badges, real fees, hours and services side by side. No guesswork.' },
  { icon: PhoneCall, title: 'Call or visit', text: 'One tap to call or get directions. In an emergency, ambulances are a tap away.' },
];

function Home() {
  const navigate = useNavigate();
  const [meta, setMeta] = useState(null);
  const [featured, setFeatured] = useState(null);
  const [what, setWhat] = useState('');
  const [city, setCity] = useState('');
  const [reporting, setReporting] = useState(null);

  useEffect(() => {
    getSearchMeta().then(({ data }) => setMeta(data)).catch(() => setMeta(null));
    getFeaturedProviders().then(({ data }) => setFeatured(data)).catch(() => setFeatured([]));
  }, []);

  const search = (event) => {
    event.preventDefault();
    const params = new URLSearchParams();
    // The "what" box accepts a category from the list or free text.
    const category = meta?.categories.find((c) => c.label.toLowerCase() === what.trim().toLowerCase());
    if (category) {
      params.set('category', category.category);
    } else if (what.trim()) {
      params.set('q', what.trim());
    }
    if (city) {
      params.set('city', city);
    }
    navigate(`/search?${params.toString()}`);
  };

  return (
    <div className="home">
      <section className="hero">
        <div className="container hero-grid">
          <div className="hero-copy">
            <span className="eyebrow-pill"><PawPrint size={14} /> India&apos;s animal-care directory</span>
            <h1>
              Trusted care for every paw, <span>a tap away.</span>
            </h1>
            <p className="hero-sub">
              Find verified vets, pet hospitals, boarding, diagnostic labs, pet ambulances and rescue NGOs near you.
              Compare fees and hours, then call in one tap.
            </p>

            <form className="search-bar" onSubmit={search} role="search">
              <div className="search-field grow">
                <Search size={18} />
                <input
                  list="care-types"
                  value={what}
                  onChange={(e) => setWhat(e.target.value)}
                  placeholder="Vet clinic, X-ray, boarding..."
                  aria-label="What do you need?"
                />
                <datalist id="care-types">
                  {(meta?.categories ?? []).map((c) => <option key={c.category} value={c.label} />)}
                </datalist>
              </div>
              <div className="search-field city">
                <MapPin size={18} />
                <select value={city} onChange={(e) => setCity(e.target.value)} aria-label="City">
                  <option value="">All cities</option>
                  {(meta?.cities ?? []).map((c) => <option key={c} value={c}>{c}</option>)}
                </select>
              </div>
              <button type="submit" className="ui-btn ui-btn-primary ui-btn-lg">Find a Vet</button>
            </form>

            <div className="quick-row">
              {QUICK.map(({ label, icon: Icon, to }) => (
                <Link key={label} to={to} className="quick-chip"><Icon size={15} /> {label}</Link>
              ))}
            </div>

            <div className="hero-stats">
              <div><strong>{meta ? meta.totalProviders : '--'}</strong><span>providers listed</span></div>
              <div><strong>{meta ? meta.cities.length : '--'}</strong><span>cities</span></div>
              <div><strong>6</strong><span>kinds of care</span></div>
            </div>
          </div>

          <div className="hero-art" aria-hidden="true">
            <div className="art-blob" />
            <div className="art-card art-card-1">
              <CategoryIcon category="HOSPITAL" />
              <div><strong>Banjara Pet Hospital</strong><span>Open 24x7 · ICU · Surgery</span></div>
              <span className="tag tag-green">Open now</span>
            </div>
            <div className="art-card art-card-2">
              <span className="art-paw"><PawPrint size={44} /></span>
              <div className="art-rating">4.8 <small>412 reviews</small></div>
              <span className="tag tag-blue"><BadgeCheck size={12} /> Verified</span>
            </div>
            <div className="art-card art-card-3">
              <Siren size={20} />
              <div><strong>Ambulance in ~30 min</strong><span>Oxygen on board</span></div>
            </div>
          </div>
        </div>
      </section>

      <section className="emergency-strip">
        <div className="container emergency-inner">
          <Siren size={22} />
          <p><strong>Pet emergency?</strong> See 24x7 hospitals and pet ambulances that can help right now.</p>
          <Link to="/search?emergency=true&open24x7=true" className="ui-btn ui-btn-light">
            Get emergency help <ArrowRight size={16} />
          </Link>
        </div>
      </section>

      <section className="section container" id="services">
        <div className="section-head">
          <div>
            <span className="eyebrow">Explore Services</span>
            <h2>Everything your pet needs, in one place</h2>
          </div>
          <Link to="/search" className="link-arrow">See all providers <ArrowRight size={16} /></Link>
        </div>
        <div className="category-grid">
          {(meta?.categories ?? Object.keys(CATEGORY_META).map((k) => ({ category: k, label: '', count: null }))).map((c) => (
            <Link key={c.category} to={`/search?category=${c.category}`} className="category-card">
              <CategoryIcon category={c.category} size={26} />
              <div>
                <h3>{c.label || <Skeleton width={120} />}</h3>
                <p>{CATEGORY_META[c.category].blurb}</p>
              </div>
              <span className="count">{c.count ?? ''}{c.count !== null ? ' listed' : ''}</span>
            </Link>
          ))}
        </div>
      </section>

      <section className="section container">
        <div className="section-head">
          <div>
            <span className="eyebrow">Top rated</span>
            <h2>Loved by pet parents</h2>
          </div>
          <Link to="/search?sort=rating" className="link-arrow">View more <ArrowRight size={16} /></Link>
        </div>
        <div className="provider-grid three">
          {featured === null
            ? [0, 1, 2].map((i) => <Skeleton key={i} height={250} style={{ borderRadius: 18 }} />)
            : featured.slice(0, 3).map((p) => <ProviderCard key={p.id} provider={p} onReport={setReporting} />)}
        </div>
      </section>

      <section className="section how">
        <div className="container">
          <div className="section-head center">
            <div>
              <span className="eyebrow">How it works</span>
              <h2>The right care in three steps</h2>
            </div>
          </div>
          <div className="steps">
            {STEPS.map(({ icon: Icon, title, text }, index) => (
              <div key={title} className="step">
                <span className="step-num">{index + 1}</span>
                <Icon size={26} />
                <h3>{title}</h3>
                <p>{text}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="section container cta-pair">
        <div className="cta-card cta-business">
          <Store size={28} />
          <h3>Own a clinic, shelter or pet boarding?</h3>
          <p>List your business for free and get calls from pet parents in your city. We verify every listing.</p>
          <Link to="/list-your-business" className="ui-btn ui-btn-light">List Your Business <ArrowRight size={16} /></Link>
        </div>
        <div className="cta-card cta-correction">
          <Flag size={28} />
          <h3>Spotted something out of date?</h3>
          <p>Phone numbers and hours change. Tell us and we&apos;ll fix it, so the next pet parent isn&apos;t left waiting.</p>
          <Link to="/search" className="ui-btn ui-btn-secondary">Submit a Correction <ArrowRight size={16} /></Link>
        </div>
      </section>

      <div className="mobile-cta">
        <Link to="/search?category=VET_CLINIC" className="ui-btn ui-btn-primary"><Search size={16} /> Find a Vet</Link>
        <Link to="/search?emergency=true&open24x7=true" className="ui-btn ui-btn-danger"><Siren size={16} /> Emergency</Link>
      </div>

      <CorrectionModal provider={reporting} onClose={() => setReporting(null)} />
    </div>
  );
}

export default Home;
