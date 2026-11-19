import { Link } from 'react-router-dom';
import { PawPrint } from 'lucide-react';

function Brand({ to = '/', light = false }) {
  return (
    <Link to={to} className={`brand ${light ? 'brand-light' : ''}`} aria-label="PawCare home">
      <span className="brand-mark">
        <PawPrint size={18} />
      </span>
      <span className="brand-name">
        Paw<span>Care</span>
      </span>
    </Link>
  );
}

export default Brand;
