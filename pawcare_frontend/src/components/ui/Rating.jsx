import { Star } from 'lucide-react';

function Rating({ value, count }) {
  if (!count) {
    return <span className="rating rating-new">New</span>;
  }
  return (
    <span className="rating" aria-label={`Rated ${value} out of 5 from ${count} reviews`}>
      <Star size={14} fill="currentColor" />
      <strong>{Number(value).toFixed(1)}</strong>
      <span>({count.toLocaleString('en-IN')})</span>
    </span>
  );
}

export default Rating;
