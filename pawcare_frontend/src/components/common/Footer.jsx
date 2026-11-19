import { Link } from 'react-router-dom';
import Brand from './Brand';

function Footer() {
  return (
    <footer className="site-footer">
      <div className="container footer-grid">
        <div>
          <Brand light />
          <p className="footer-note">
            Find vets, pet hospitals, boarding, diagnostics, ambulances and rescue NGOs, all in one place.
          </p>
        </div>
        <div>
          <h4>Pet parents</h4>
          <Link to="/search?category=VET_CLINIC">Find a Vet</Link>
          <Link to="/search?emergency=true&open24x7=true">24x7 Emergency</Link>
          <Link to="/search?category=BOARDING">Boarding</Link>
          <Link to="/search?category=NGO">Adopt a pet</Link>
        </div>
        <div>
          <h4>Providers</h4>
          <Link to="/list-your-business">List Your Business</Link>
          <Link to="/search">Submit a Correction</Link>
          <Link to="/admin">Partner dashboard</Link>
        </div>
        <div>
          <h4>Learn</h4>
          <Link to="/academy">AI Academy</Link>
          <Link to="/studio">Content Studio</Link>
        </div>
      </div>
      <div className="container footer-bottom">
        <span>Prototype built for the Product, Design &amp; AI Systems internship assignment.</span>
        <span>Provider data shown is sample data.</span>
      </div>
    </footer>
  );
}

export default Footer;
