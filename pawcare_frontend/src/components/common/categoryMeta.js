import { Ambulance, FlaskConical, HandHeart, Hospital, House, Stethoscope } from 'lucide-react';

/** Icon, colour and one-line description for each provider category. */
export const CATEGORY_META = {
  VET_CLINIC: { icon: Stethoscope, tint: 'teal', blurb: 'Check-ups, vaccines, everyday care' },
  HOSPITAL: { icon: Hospital, tint: 'rose', blurb: 'Surgery, ICU and 24x7 emergencies' },
  BOARDING: { icon: House, tint: 'amber', blurb: 'Stays, daycare and pet sitting' },
  DIAGNOSTICS: { icon: FlaskConical, tint: 'violet', blurb: 'Blood tests, X-ray, scans' },
  AMBULANCE: { icon: Ambulance, tint: 'red', blurb: 'Emergency pick-up, day or night' },
  NGO: { icon: HandHeart, tint: 'green', blurb: 'Rescue, adoption, sterilisation' },
};
