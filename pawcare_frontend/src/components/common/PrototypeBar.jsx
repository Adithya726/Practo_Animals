import { NavLink } from 'react-router-dom';

/** A thin switcher so reviewers can move between the three assignment prototypes. */
function PrototypeBar() {
  const link = ({ isActive }) => `proto-link ${isActive ? 'active' : ''}`;
  return (
    <div className="proto-bar">
      <span className="proto-label">Prototypes</span>
      <nav>
        <NavLink to="/" end className={link}>1. PawCare</NavLink>
        <NavLink to="/academy" className={link}>2. AI Academy</NavLink>
        <NavLink to="/studio" className={link}>3. Content Studio</NavLink>
        <NavLink to="/admin" className={link}>Admin</NavLink>
      </nav>
    </div>
  );
}

export default PrototypeBar;
