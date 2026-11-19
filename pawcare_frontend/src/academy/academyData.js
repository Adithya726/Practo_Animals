import { Briefcase, GraduationCap, Hammer, Rocket, UserRoundCheck, Users } from 'lucide-react';

export const VALUE_PROPS = [
  { icon: Hammer, title: 'Build, don\u2019t just watch', text: 'Every week ends with something you shipped: a bot, an automation, a page or a portfolio piece.' },
  { icon: UserRoundCheck, title: 'Live mentors', text: 'Small live sessions with practitioners, plus doubt-clearing on WhatsApp between classes.' },
  { icon: Briefcase, title: 'Career-ready portfolio', text: 'Finish with 3+ projects, a LinkedIn-ready case study and interview prep.' },
  { icon: Users, title: 'Small cohorts', text: 'Capped seats so mentors know your name and review your work.' },
];

export const AUDIENCES = {
  STUDENT: {
    icon: GraduationCap,
    title: 'Students',
    points: [
      'You use ChatGPT for assignments but want to build with it',
      'You want projects that stand out in placements and internships',
      'You can spare 5 hours a week on weekends',
    ],
  },
  PROFESSIONAL: {
    icon: Rocket,
    title: 'Young professionals',
    points: [
      'You spend hours on reports, emails and follow-ups that could run themselves',
      'You want to be the person on your team who knows AI tools',
      'You can do two evening sessions a week',
    ],
  },
};

export const JOURNEY = [
  { title: 'Enquire or reserve', text: 'Pick a programme and leave your details. Takes under a minute.' },
  { title: 'Counselling call', text: 'A mentor calls within 24 hours to understand your goals and confirm the right fit.' },
  { title: 'Confirm your seat', text: 'Pay securely or choose EMI. Your seat is held for 48 hours after the call.' },
  { title: 'Learn and build', text: 'Live classes, weekly projects and feedback from mentors.' },
  { title: 'Demo day', text: 'Present your best project and get a certificate plus portfolio review.' },
];

export const PROJECTS = [
  { title: 'Personal research assistant', text: 'A Claude or GPT project that reads PDFs and answers with sources.' },
  { title: 'Content engine', text: 'An n8n flow that turns one idea into a carousel, a post and a newsletter.' },
  { title: 'Lead-capture landing page', text: 'A page built with Lovable or Framer, connected to a Google Sheet.' },
  { title: 'Inbox autopilot', text: 'Sorts, summarises and drafts replies to your emails every morning.' },
];

export const FAQS = [
  { q: 'Do I need to know coding?', a: 'No. The foundations and design tracks are no-code. The professional track uses simple automation tools, and we teach the little logic you need.' },
  { q: 'What if I miss a live class?', a: 'Every session is recorded and posted within a few hours, with notes and the project brief.' },
  { q: 'Is there a certificate?', a: 'Yes, once you submit the weekly projects and present at demo day. It links to your project portfolio.' },
  { q: 'Can I pay in instalments?', a: 'Yes. No-cost EMI is available on all programmes over \u20b95,000.' },
  { q: 'What is the refund policy?', a: 'Full refund if you withdraw within 7 days of the cohort start, no questions asked.' },
];
