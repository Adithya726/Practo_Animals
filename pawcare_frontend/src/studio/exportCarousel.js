import { toJpeg, toPng } from 'html-to-image';
import { jsPDF } from 'jspdf';
import { SLIDE_H, SLIDE_W } from './themes';

function slug(text) {
  return (text || 'carousel').toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/^-|-$/g, '').slice(0, 40) || 'carousel';
}

async function capture(nodes, format = 'png') {
  // Make sure the bundled fonts are ready, or the first capture can fall back to a system font.
  await document.fonts.ready;
  const options = { width: SLIDE_W, height: SLIDE_H, pixelRatio: 1, cacheBust: true };
  const images = [];
  for (const node of nodes) {
    images.push(format === 'jpeg' ? await toJpeg(node, { ...options, quality: 0.92 }) : await toPng(node, options));
  }
  return images;
}

function download(dataUrl, filename) {
  const link = document.createElement('a');
  link.href = dataUrl;
  link.download = filename;
  document.body.appendChild(link);
  link.click();
  link.remove();
}

/** Instagram: one PNG per slide, named in order. */
export async function exportPngs(nodes, title) {
  const images = await capture(nodes);
  const base = slug(title);
  images.forEach((img, i) => download(img, `${base}-${String(i + 1).padStart(2, '0')}.png`));
  return images.length;
}

/** LinkedIn: a single PDF, one 4:5 page per slide, which LinkedIn shows as a document carousel. */
export async function exportPdf(nodes, title) {
  // JPEG pages keep the PDF a few MB instead of tens of MB; LinkedIn caps uploads at 100 MB.
  const images = await capture(nodes, 'jpeg');
  const pdf = new jsPDF({ orientation: 'portrait', unit: 'px', format: [SLIDE_W, SLIDE_H], hotfixes: ['px_scaling'], compress: true });
  images.forEach((img, i) => {
    if (i > 0) {
      pdf.addPage([SLIDE_W, SLIDE_H], 'portrait');
    }
    pdf.addImage(img, 'JPEG', 0, 0, SLIDE_W, SLIDE_H);
  });
  pdf.save(`${slug(title)}.pdf`);
  return images.length;
}
