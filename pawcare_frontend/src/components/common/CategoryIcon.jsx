import { CATEGORY_META } from './categoryMeta';

function CategoryIcon({ category, size = 22 }) {
  const meta = CATEGORY_META[category] || CATEGORY_META.VET_CLINIC;
  const Icon = meta.icon;
  return (
    <span className={`cat-icon tint-${meta.tint}`}>
      <Icon size={size} />
    </span>
  );
}

export default CategoryIcon;
