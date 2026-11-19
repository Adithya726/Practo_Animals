import {
  AlarmClock, Bookmark, BookOpen, Briefcase, Cat, ChartColumn, CircleCheck, Dog, Droplet, Eye, Heart, Lightbulb,
  Palette, PawPrint, Siren, Soup, Sparkles, Stethoscope, Syringe, TriangleAlert, Wallet, Workflow, Wrench,
} from 'lucide-react';

/** Icon names the backend writers use, mapped to lucide components. */
export const SLIDE_ICONS = {
  paw: PawPrint,
  dog: Dog,
  cat: Cat,
  stethoscope: Stethoscope,
  syringe: Syringe,
  heart: Heart,
  siren: Siren,
  bowl: Soup,
  droplet: Droplet,
  sparkles: Sparkles,
  workflow: Workflow,
  wrench: Wrench,
  wallet: Wallet,
  clock: AlarmClock,
  book: BookOpen,
  briefcase: Briefcase,
  palette: Palette,
  chart: ChartColumn,
  alert: TriangleAlert,
  eye: Eye,
  check: CircleCheck,
  lightbulb: Lightbulb,
  bookmark: Bookmark,
};

export function iconFor(name) {
  return SLIDE_ICONS[name] || Sparkles;
}
