import { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { Menu, Siren, X } from 'lucide-react';
import Brand from './Brand';
import PrototypeBar from './PrototypeBar';

const LINKS = [
  { to: '/search?category=VET_CLINIC', label: 'Find a Vet' },
  { to: '/search', label: 'Explore Services' },
  { to: '/search?category=NGO', label: 'Rescue & Adopt' },
  { to: '/list-your-business', label: 'For Business' },
];

function Navbar() {
  const [open, setOpen] = useState(false);
  const location = useLocation();
  const current = location.pathname + location.search;

  return (
    <header className="site-header">
      <PrototypeBar />
      <div className="nav container">
        <Brand />
        <nav className={`nav-links ${open ? 'open' : ''}`} onClick={() => setOpen(false)}>
          {LINKS.map((link) => (
            <Link key={link.label} to={link.to} className={`nav-link ${current === link.to ? 'active' : ''}`}>
              {link.label}
            </Link>
          ))}
          <Link to="/search?emergency=true&open24x7=true" className="nav-emergency">
            <Siren size={16} /> Emergency
          </Link>
        </nav>
        <button
          type="button"
          className="nav-toggle"
          aria-label={open ? 'Close menu' : 'Open menu'}
          aria-expanded={open}
          onClick={() => setOpen((value) => !value)}
        >
          {open ? <X size={22} /> : <Menu size={22} />}
        </button>
      </div>
    </header>
  );
}

export default Navbar;
