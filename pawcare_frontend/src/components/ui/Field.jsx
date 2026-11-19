/** Label + input wrapper that shows a field's validation message under it. */
function Field({ label, error, hint, children, optional = false }) {
  return (
    <label className={`field ${error ? 'has-error' : ''}`}>
      <span className="field-label">
        {label}
        {optional ? <em>Optional</em> : null}
      </span>
      {children}
      {error ? <span className="field-error">{error}</span> : hint ? <span className="field-hint">{hint}</span> : null}
    </label>
  );
}

export default Field;
