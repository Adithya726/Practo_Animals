import { Link } from 'react-router-dom';
import { PawPrint } from 'lucide-react';
import EmptyState from '../components/common/EmptyState';

function NotFound() {
  return (
    <div className="container page-pad">
      <EmptyState
        icon={<PawPrint size={28} />}
        title="This page wandered off"
        text="The link may be old. Let's get you back on track."
        action={<Link to="/" className="ui-btn ui-btn-primary">Go home</Link>}
      />
    </div>
  );
}

export default NotFound;
