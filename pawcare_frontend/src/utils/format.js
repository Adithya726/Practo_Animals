export function rupees(value) {
  const amount = Number(value || 0);
  if (amount === 0) {
    return 'Free';
  }
  return `\u20b9${amount.toLocaleString('en-IN')}`;
}

export function formatDate(time) {
  const date = new Date(time);
  if (Number.isNaN(date.getTime())) {
    return '';
  }
  return date.toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' });
}

export function formatDateTime(time) {
  const date = new Date(time);
  if (Number.isNaN(date.getTime())) {
    return '';
  }
  return date.toLocaleString('en-IN', { day: 'numeric', month: 'short', hour: '2-digit', minute: '2-digit' });
}

export function daysUntil(isoDate) {
  const target = new Date(`${isoDate}T00:00:00`);
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  return Math.round((target - today) / 86400000);
}

/** tel: link with spaces removed. */
export function telLink(phone) {
  return `tel:${String(phone || '').replace(/[^+\d]/g, '')}`;
}

export function mapsLink(provider) {
  const query = encodeURIComponent(`${provider.name}, ${provider.address}`);
  return `https://www.google.com/maps/search/?api=1&query=${query}`;
}

export function pluralize(count, word) {
  return `${count} ${word}${count === 1 ? '' : 's'}`;
}
