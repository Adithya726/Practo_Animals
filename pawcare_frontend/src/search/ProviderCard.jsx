import { Link } from 'react-router-dom';
import { BadgeCheck, Clock3, House, MapPin, Navigation, Phone, Siren } from 'lucide-react';
import CategoryIcon from '../components/common/CategoryIcon';
import Rating from '../components/ui/Rating';
import { mapsLink, rupees, telLink } from '../utils/format';

function ProviderCard({ provider, onReport }) {
  return (
    <article className="provider-card">
      <div className="pc-top">
        <CategoryIcon category={provider.category} />
        <div className="pc-title">
          <Link to={`/provider/${provider.id}`} className="pc-name">
            {provider.name}
            {provider.verified ? (
              <BadgeCheck size={16} className="verified-icon" aria-label="Verified by PawCare" />
            ) : null}
          </Link>
          <span className="pc-category">{provider.categoryLabel}</span>
        </div>
        <Rating value={provider.rating} count={provider.reviewCount} />
      </div>

      <p className="pc-location">
        <MapPin size={14} /> {provider.area}, {provider.city}
      </p>

      <div className="pc-tags">
        {provider.open24x7 ? (
          <span className="tag tag-green"><Clock3 size={12} /> Open 24x7</span>
        ) : (
          <span className="tag"><Clock3 size={12} /> {provider.openingHours}</span>
        )}
        {provider.emergency ? <span className="tag tag-red"><Siren size={12} /> Emergency</span> : null}
        {provider.homeVisit ? <span className="tag tag-blue"><House size={12} /> Home visit</span> : null}
      </div>

      <p className="pc-species">{provider.species.join(' · ')}</p>

      <div className="pc-footer">
        <div className="pc-fee">
          <span>{provider.category === 'NGO' ? 'Help' : 'From'}</span>
          <strong>{rupees(provider.feeFrom)}</strong>
        </div>
        <div className="pc-actions">
          <a className="ui-btn ui-btn-secondary ui-btn-sm" href={mapsLink(provider)} target="_blank" rel="noreferrer">
            <Navigation size={14} /> Directions
          </a>
          <a className="ui-btn ui-btn-primary ui-btn-sm" href={telLink(provider.phone)}>
            <Phone size={14} /> Call
          </a>
        </div>
      </div>

      {onReport ? (
        <button type="button" className="pc-report" onClick={() => onReport(provider)}>
          Something wrong? Submit a correction
        </button>
      ) : null}
    </article>
  );
}

export default ProviderCard;
