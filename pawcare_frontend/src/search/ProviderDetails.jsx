import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import {
  ArrowLeft, BadgeCheck, CalendarDays, Clock3, Copy, Flag, House, IndianRupee, MapPin, Navigation, PawPrint, Phone, Siren,
} from 'lucide-react';
import CategoryIcon from '../components/common/CategoryIcon';
import EmptyState from '../components/common/EmptyState';
import Rating from '../components/ui/Rating';
import { Skeleton } from '../components/ui/Skeleton';
import { useToast } from '../components/ui/useToast';
import CorrectionModal from './CorrectionModal';
import { getProvider } from '../services/providerService';
import { getErrorMessage } from '../services/api';
import { formatDate, mapsLink, rupees, telLink } from '../utils/format';

function ProviderDetails() {
  const { id } = useParams();
  const toast = useToast();
  const [state, setState] = useState({ id: null, provider: null, error: '' });
  const [reporting, setReporting] = useState(false);
  // Derived from which id the loaded data belongs to, so a new id shows the skeleton straight away.
  const provider = state.id === id ? state.provider : null;
  const error = state.id === id ? state.error : '';

  useEffect(() => {
    let cancelled = false;
    getProvider(id)
      .then(({ data }) => !cancelled && setState({ id, provider: data, error: '' }))
      .catch((err) => !cancelled && setState({ id, provider: null, error: getErrorMessage(err, 'Provider not found') }));
    return () => {
      cancelled = true;
    };
  }, [id]);

  const copyAddress = async () => {
    try {
      await navigator.clipboard.writeText(`${provider.name}, ${provider.address}`);
      toast.success('Address copied');
    } catch {
      toast.error('Could not copy the address');
    }
  };

  if (error) {
    return (
      <div className="container page-pad">
        <EmptyState
          icon={<PawPrint size={28} />}
          title="We couldn't find this provider"
          text={error}
          action={<Link className="ui-btn ui-btn-primary" to="/search">Back to search</Link>}
        />
      </div>
    );
  }

  if (!provider) {
    return (
      <div className="container page-pad">
        <Skeleton height={180} style={{ borderRadius: 20, marginBottom: 20 }} />
        <Skeleton height={300} style={{ borderRadius: 20 }} />
      </div>
    );
  }

  return (
    <div className="container page-pad detail-page">
      <Link to={`/search?category=${provider.category}&city=${encodeURIComponent(provider.city)}`} className="back-link">
        <ArrowLeft size={16} /> More {provider.categoryLabel.toLowerCase()}s in {provider.city}
      </Link>

      <section className="detail-hero">
        <CategoryIcon category={provider.category} size={30} />
        <div className="detail-title">
          <span className="eyebrow">{provider.categoryLabel}</span>
          <h1>
            {provider.name}
            {provider.verified ? <BadgeCheck size={22} className="verified-icon" aria-label="Verified" /> : null}
          </h1>
          <p className="pc-location"><MapPin size={15} /> {provider.address}</p>
          <div className="pc-tags">
            <Rating value={provider.rating} count={provider.reviewCount} />
            {provider.open24x7 ? <span className="tag tag-green"><Clock3 size={12} /> Open 24x7</span> : null}
            {provider.emergency ? <span className="tag tag-red"><Siren size={12} /> Emergency</span> : null}
            {provider.homeVisit ? <span className="tag tag-blue"><House size={12} /> Home visit</span> : null}
          </div>
        </div>
        <div className="detail-actions">
          <a className="ui-btn ui-btn-primary ui-btn-lg" href={telLink(provider.phone)}>
            <Phone size={18} /> Call {provider.phone}
          </a>
          <a className="ui-btn ui-btn-secondary ui-btn-lg" href={mapsLink(provider)} target="_blank" rel="noreferrer">
            <Navigation size={18} /> Directions
          </a>
        </div>
      </section>

      <div className="detail-grid">
        <div className="detail-main">
          <section className="panel">
            <h2>About</h2>
            <p>{provider.about || 'The provider has not added a description yet.'}</p>
          </section>
          <section className="panel">
            <h2>Services</h2>
            <div className="chip-row">
              {provider.services.map((s) => <span key={s} className="chip static">{s}</span>)}
            </div>
          </section>
          <section className="panel">
            <h2>Pets treated</h2>
            <div className="chip-row">
              {provider.species.map((s) => <span key={s} className="chip static">{s}</span>)}
            </div>
          </section>
        </div>

        <aside className="detail-side">
          <div className="panel facts">
            <div><Clock3 size={18} /><span>Hours</span><strong>{provider.openingHours}</strong></div>
            <div><IndianRupee size={18} /><span>Starting fee</span><strong>{rupees(provider.feeFrom)}</strong></div>
            <div><CalendarDays size={18} /><span>Experience</span><strong>{provider.yearsActive ? `${provider.yearsActive} years` : 'New on PawCare'}</strong></div>
            <button type="button" className="ui-btn ui-btn-ghost ui-btn-full" onClick={copyAddress}>
              <Copy size={16} /> Copy address
            </button>
          </div>
          <div className="panel correction-box">
            <Flag size={18} />
            <div>
              <strong>Is something out of date?</strong>
              <span>Last updated {formatDate(provider.updatedAt)}</span>
            </div>
            <button type="button" className="ui-btn ui-btn-secondary ui-btn-full" onClick={() => setReporting(true)}>
              Submit a Correction
            </button>
          </div>
        </aside>
      </div>

      <CorrectionModal provider={reporting ? provider : null} onClose={() => setReporting(false)} />
    </div>
  );
}

export default ProviderDetails;
